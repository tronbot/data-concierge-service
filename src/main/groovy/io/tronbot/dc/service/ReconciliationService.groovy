package io.tronbot.dc.service

import org.springframework.stereotype.Service

import groovy.json.JsonSlurper
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
	private final JsonSlurper json

	ReconciliationService(GooglePlacesClient client, JsonSlurper json){
		this.json = json
		this.client = client
	}

	public Object queryBusiness(String keywords){
		def result = query(keywords)
		return result
	}

	public Map<String, Object> query(String keywords){
		keywords = keywords.toLowerCase().replace('\t', ' ').replaceAll('null', '').replaceAll('\\W -,.&', '').trim().replaceAll(' +', ' ')
		def result = null;
		try {
			def places = json.parseText(client.query(keywords))
			result = json.parseText(client.detail(places.results[0].place_id))
		} catch (NullPointerException e) {
			//Not a happy path, ignore
			log.warn("Unable to resolve : ${keywords}")
		}
		return result
	}
}
