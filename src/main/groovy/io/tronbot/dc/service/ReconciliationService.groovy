package io.tronbot.dc.service

import org.springframework.stereotype.Service

import groovy.util.logging.Log4j
import io.tronbot.dc.client.GooglePlacesClient
import io.tronbot.dc.repository.RequestHistoryRepository

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Service
@Log4j
public class ReconciliationService{
	private final GooglePlacesClient client
	private final RequestHistoryRepository repository

	ReconciliationService(GooglePlacesClient client, RequestHistoryRepository repository){
		this.client=client
		this.repository = repository
	}

	Object queryBusiness(String keywords){
		keywords = keywords.toLowerCase().replace('\t', ' ').replaceAll('null', '').replaceAll('\\W -,.&', '').trim().replaceAll(' +', ' ')
		def res = client.query(keywords);
		return res
	}
}
