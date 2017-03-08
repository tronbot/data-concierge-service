package io.tronbot.dc.config

import java.nio.file.Paths

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.cloud.client.serviceregistry.Registration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

import com.hazelcast.config.Config
import com.hazelcast.config.EvictionPolicy
import com.hazelcast.config.MapConfig
import com.hazelcast.config.MaxSizeConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance

import io.tronbot.dc.service.RequestHistoryStore

@Configuration
@EnableCaching
public class CacheConfiguration {
	private static final Logger log = LoggerFactory.getLogger(DiscoveryClient.class);
	
	@Autowired
	private Environment env
	@Autowired
	private DiscoveryClient discoveryClient
	@Autowired
	private Registration registration
	@Autowired
	private RequestHistoryStore store
	//	@Autowired
	//	private ResponseEntityStreamSerializer responseEntityStreamSerializer
	@Value('${data-concierge.cache.hazelcast.port}')
	private int port
	@Value('${data-concierge.cache.hazelcast.backup-Count}')
	private int backupCount
	@Value('${data-concierge.cache.hazelcast.write-batch-size}')
	private int writeBatchSize
	@Value('${data-concierge.cache.hazelcast.write-delay}')
	private int writeDelay
	@Value('${data-concierge.cache.hazelcast.time-to-live-seconds}')
	private int timeToLiveSecs
	


	@Bean
	@Autowired
	public HazelcastInstance getHazelcastInstance() {
		log.debug('Configuring Hazelcast')
		Config config = new Config()
		// The serviceId is by default the application's name, see Spring Boot's
		// eureka.instance.appname property
		String serviceId = registration.getServiceId()
		log.debug("Configuring Hazelcast clustering for instanceId: ${serviceId}")
		// Network Config
		config.getNetworkConfig().setPort(port)
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false)
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true)
		for (ServiceInstance instance : discoveryClient.getInstances(serviceId)) {
			String clusterMember = "${instance.getHost()}:${port}"
			log.debug("Adding Hazelcast (prod) cluster member ${clusterMember}")
			config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(clusterMember)
		}
		// hot restart
		config.getHotRestartPersistenceConfig().setEnabled(true)
		config.getHotRestartPersistenceConfig().setBaseDir(Paths.get(System.getenv('APPDATA'), serviceId).toFile())
		config.getHotRestartPersistenceConfig().setValidationTimeoutSeconds(600)
		config.getHotRestartPersistenceConfig().setDataLoadTimeoutSeconds(1200)

		config.getMapConfigs().put('default', initializeDefaultMapConfig())
		config.getMapConfigs().put('persistableCache', initializePersistableMapConfig())
		return Hazelcast.newHazelcastInstance(config)
	}

	private MapConfig initializeDefaultMapConfig() {
		MapConfig mapConfig = new MapConfig()
		/*
		 * Number of backups. If 1 is set as the backup-count for example, then
		 * all entries of the map will be copied to another JVM for fail-safety.
		 * Valid numbers are 0 (no backup), 1, 2, 3.
		 */
		mapConfig.setBackupCount(backupCount)
		/*
		 * Valid values are: NONE (no eviction), LRU (Least Recently Used), LFU
		 * (Least Frequently Used). NONE is the default.
		 */
		mapConfig.setEvictionPolicy(EvictionPolicy.LRU)
		/*
		 * Maximum size of the map. When max size is reached, map is evicted
		 * based on the policy defined. Any integer between 0 and
		 * Integer.MAX_VALUE. 0 means Integer.MAX_VALUE. Default is 0.
		 */
		mapConfig.setMaxSizeConfig(new MaxSizeConfig(0, MaxSizeConfig.MaxSizePolicy.USED_HEAP_SIZE))
		return mapConfig
	}

	private MapConfig initializePersistableMapConfig() {
		MapConfig mapConfig = new MapConfig()
		mapConfig.setBackupCount(1)
		mapConfig.getHotRestartConfig().setEnabled(true)
		mapConfig.setTimeToLiveSeconds(timeToLiveSecs) // 20 weeks of expiration
		// Config Map Store
		mapConfig.getMapStoreConfig().setEnabled(true)
		// mapConfig.getMapStoreConfig().setClassName('io.tronbot.dc.service.RequestHistoryStore')
		mapConfig.getMapStoreConfig().setImplementation(store)
		mapConfig.getMapStoreConfig().setWriteBatchSize(writeBatchSize)
		mapConfig.getMapStoreConfig().setWriteDelaySeconds(writeDelay)
		mapConfig.getMapStoreConfig().setWriteCoalescing(true)
		return mapConfig
	}
}
