package io.tronbot.dc.service

import org.springframework.stereotype.Service

import com.hazelcast.core.MapStoreAdapter

import groovy.util.logging.Log4j
import io.tronbot.dc.dao.RequestHistoryRepository
import io.tronbot.dc.domain.RequestHistory
import io.tronbot.dc.helper.JsonHelper
import io.tronbot.dc.messaging.Emitter

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Service
@Log4j
public class RequestHistoryStore extends MapStoreAdapter<String, Map<String, Object>> {
	private final RequestHistoryRepository repository
	private final Emitter emitter
	private final JsonHelper jsonHelper

	public RequestHistoryStore(RequestHistoryRepository repository, Emitter emitter, JsonHelper jsonHelper) {
		super()
		this.repository = repository
		this.emitter = emitter
		this.jsonHelper = jsonHelper
	}

	@Override
	public Map<String, Object> load(String key) {
		RequestHistory reqHis = repository.findByRequest(key)?.find()
		return reqHis ? jsonHelper.stringToMap(reqHis.getResponse()) : null
	}

	@Override
	public Iterable<String> loadAllKeys() {
		return repository.findAllRequests()
	}

	@Override
	public void store(String key, Map<String, Object> json) {
		String value = jsonHelper.mapToString(json);
		if(value){
			RequestHistory his = new RequestHistory(key, value)
			emitter.saveOrUpdateRequestHistory(his)
		}else{
			log.warn "Unable to resolve : ${key}"
		}
	}


	@Override
	public void delete(String key) {
		repository.deleteByRequest(key)
	}
}
