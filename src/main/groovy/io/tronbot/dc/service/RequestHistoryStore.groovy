package io.tronbot.dc.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import com.hazelcast.core.MapStoreAdapter

import groovy.util.logging.Log4j
import io.tronbot.dc.RequestHistoryWriter
import io.tronbot.dc.dao.RequestHistoryRepository
import io.tronbot.dc.domain.RequestHistory
import io.tronbot.dc.domain.RequestHistory.Status

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Service
@Log4j
public class RequestHistoryStore extends MapStoreAdapter<String, String> {
	@Autowired
	private RequestHistoryRepository repository
	@Autowired
	private RequestHistoryWriter writer

	@Override
	public String load(String key) {
		RequestHistory reqHis = repository.findByRequest(key)
		return reqHis?.getResponse()
	}

	@Override
	public Iterable<String> loadAllKeys() {
		return repository.findAllRequests()
	}

	@Override
	public void store(String key, String value) {
		RequestHistory his = new RequestHistory(key, value);
		if(Status.OK.equals(his.getStatus())){
			repository.save(his)
		}else{
			log.warn "Unable to resolve : ${key}"
			log.warn value
		}
	}


	@Override
	public void delete(String key) {
		repository.deleteByRequest(key)
	}
}
