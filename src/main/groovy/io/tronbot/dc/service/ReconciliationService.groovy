package io.tronbot.dc.service

import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service

import com.jayway.jsonpath.PathNotFoundException

import groovy.util.logging.Log4j
import io.tronbot.dc.client.GooglePlacesClient
import io.tronbot.dc.common.json.JsonPathReflector
import io.tronbot.dc.dao.BusinessRepository
import io.tronbot.dc.domain.Business
import io.tronbot.dc.domain.Business.Type

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Service
@Log4j
public class ReconciliationService{
	private final GooglePlacesClient client
	private final JsonPathReflector json
	private final BusinessRepository businessRepository

	ReconciliationService(GooglePlacesClient client, JsonPathReflector json, BusinessRepository businessRepository){
		this.client = client
		this.json = json
		this.businessRepository = businessRepository
	}

	
	/**
	 * 
	 * @param keywords - comma separated string, business name, address... 
	 * @return
	 */
	public Business queryBusiness(String keywords){
		return queryWithType(guessBusinessType(keywords), keywords)
	}
	
	public Business queryWithType(Type businessType, String keywords){
		Business result = null
		String resp = query(keywords)
		if(StringUtils.isNotBlank(resp)){
			result = json.from(query(keywords), new Business())
			result.setType(businessType)
			//FIXME MOVE TO KAFKA - Non-blocking way
			if(!businessRepository.findByPlaceId(result.getPlaceId())){
				businessRepository.save(result)
			}
		}
		return result
	}
	
	
	private Type guessBusinessType(String q){
		String k = !q ?:  StringUtils.substring(q, 0, q.indexOf(','))
		return Type.valueOfIgnoreCase(k)
	}

	public String query(String keywords){
		keywords = keywords.toLowerCase().replace('\t', ' ').replaceAll('null', '').replaceAll('\\W -,.&', '').trim().replaceAll(' +', ' ')
		String place = null
		try {
			String placeId = json.read(client.query(keywords), '$.results[0].place_id')
			place = client.detail(placeId)
		} catch (PathNotFoundException e) {
			log.warn "Unable to resolve : ${keywords}"
		}
		return place
	}
}
