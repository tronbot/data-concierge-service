package io.tronbot.dc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.cloud.stream.messaging.Source
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.IntegrationComponentScan
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.integration.annotation.ServiceActivator

import groovy.util.logging.Log4j
import io.tronbot.dc.dao.RequestHistoryRepository
import io.tronbot.dc.domain.RequestHistory

@SpringBootApplication
@IntegrationComponentScan
@EnableFeignClients
@EnableDiscoveryClient
@EnableBinding([Source.class,Sink.class])
public class DataConciergeServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(DataConciergeServiceApp.class, args)
	}
}



@MessagingGateway
interface RequestHistoryWriter{
	@Gateway(requestChannel = Source.OUTPUT)
	void save(String requestHistory)
}


@MessageEndpoint
@Log4j
class RequestHistoryProcessor{

	private final RequestHistoryRepository requestHistoryRepository

	public RequestHistoryProcessor(RequestHistoryRepository requestHistoryRepository) {
		this.requestHistoryRepository = requestHistoryRepository
	}
	@ServiceActivator(inputChannel=Sink.INPUT)
	public void onNewRequestHistory(String requestHistory){
		log.debug(requestHistory)
	}
}