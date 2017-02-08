package io.tronbot.dc.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import com.hazelcast.core.MapStoreAdapter

import groovy.util.logging.Log4j
import io.tronbot.dc.dao.RequestHistoryRepository
import io.tronbot.dc.domain.RequestHistory

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Service
@Transactional
@Log4j
public class RequestHistoryStore extends MapStoreAdapter<String, String> {
	@Autowired
	private RequestHistoryRepository repository

	@Override
	public String load(String key) {
		RequestHistory reqHis = repository.findByRequestURL(key)
		return reqHis?.getResponse() 
	}

	@Override
	public Iterable<String> loadAllKeys() {
		return repository.findAllRequestURLs()
	}

	@Override
	public void store(String key, String value) {
		RequestHistory his = new RequestHistory(key, value);
		repository.save(his)
	}


	@Override
	public void delete(String key) {
		repository.deleteByRequestURL(key)
	}
}
