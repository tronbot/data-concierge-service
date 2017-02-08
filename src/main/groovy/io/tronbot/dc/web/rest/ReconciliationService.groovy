package io.tronbot.dc.web.rest

import org.springframework.stereotype.Service

import groovy.util.logging.Log4j
import io.tronbot.dc.client.GooglePlacesClient

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Service
@Log4j
public class ReconciliationService{
	private final GooglePlacesClient client

	ReconciliationService(GooglePlacesClient client){
		this.client=client
	}

	public Object queryBusiness(String keywords){
		keywords = keywords.toLowerCase().replace('\t', ' ').replaceAll('null', '').replaceAll('\\W -,.&', '').trim().replaceAll(' +', ' ')
		def res = client.query(keywords);
		return res
	}
}
