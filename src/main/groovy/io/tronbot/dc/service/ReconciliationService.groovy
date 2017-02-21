package io.tronbot.dc.service

import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service

import com.jayway.jsonpath.PathNotFoundException

import groovy.util.logging.Log4j
import io.tronbot.dc.client.GooglePlacesClient
import io.tronbot.dc.client.NPIRegistryClientHelper
import io.tronbot.dc.common.json.JsonPathReflector
import io.tronbot.dc.dao.BusinessRepository
import io.tronbot.dc.domain.Business
import io.tronbot.dc.domain.Business.Type
import io.tronbot.dc.messaging.Emitter

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Service
@Log4j
public class ReconciliationService{
	private final GooglePlacesClient googlePlacesClient
	private final NPIRegistryClientHelper npiRegistryClient
	private final JsonPathReflector json
	private final BusinessRepository businessRepository
	private final Emitter emitter

	ReconciliationService(GooglePlacesClient googlePlacesClient, NPIRegistryClientHelper npiRegistryClient,JsonPathReflector json, BusinessRepository businessRepository, Emitter emitter){
		this.googlePlacesClient = googlePlacesClient
		this.npiRegistryClient = npiRegistryClient
		this.json = json
		this.emitter = emitter
		this.businessRepository = businessRepository
	}

	/**
	 * 
	 * @param keywords - comma separated string, business name, address... 
	 * @return
	 */
	public Business queryBusiness(String keywords){
		return queryBusiness(guessBusinessType(keywords), keywords)
	}

	public Business queryBusiness(Type businessType, String keywords){
		Business result
		String resp = query(keywords)
		if(StringUtils.isNotBlank(resp)){
			result = json.from(query(keywords), new Business())
			result.setType(businessType)
			emitter.saveBusiness(result)
		}
		return result
	}

	private Type guessBusinessType(String q){
		String k = !q ?:  StringUtils.substring(q, 0, q.indexOf(','))
		return Type.valueOfIgnoreCase(k)
	}

	public Business queryPhysician(String keywords){
		Business business = queryBusiness(Type.doctor, keywords)
		//		if(business){
		//			NPIQuery query = NPIQuery.fromBusiness(business)
		//			String firstName = keywords.substring(0, keywords.indexOf(' '))
		//			String lastName = keywords.substring(keywords.indexOf(' ')+1, keywords.indexOf(','))
		//			query.setFirstName(firstName)
		//			query.setLastName(lastName)
		//			Object npi = npiRegistryClient.api(query)
		//			log.info(npi)
		//		}
		return
	}

	public String query(String keywords){
		String q = groomKeywords(keywords)
		if(!q){
			return null
		}
		String place = null
		try {
			String placeId = json.read(googlePlacesClient.query(q), '$.results[0].place_id')
			place = googlePlacesClient.detail(placeId)
		} catch (PathNotFoundException e) {
			log.warn "Unable to resolve : ${q}"
		}
		return place
	}

	public static String groomKeywords(String keywords){
		return keywords.toLowerCase().replace('\t', ' ').replaceAll('null', '').replaceAll('\\W -,.&', '').trim().replaceAll(' +', ' ')
	}
}
