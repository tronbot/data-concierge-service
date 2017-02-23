package io.tronbot.dc.service

import org.springframework.stereotype.Service

import groovy.util.logging.Log4j
import io.tronbot.dc.client.GooglePlacesClient
import io.tronbot.dc.client.NPIQuery
import io.tronbot.dc.client.NPIRegistryClientHelper
import io.tronbot.dc.domain.Hospital
import io.tronbot.dc.domain.Place
import io.tronbot.dc.domain.Place.Type
import io.tronbot.dc.helper.JsonHelper
import io.tronbot.dc.messaging.Emitter

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Service
@Log4j
public class ReconciliationService{
	private final GooglePlacesClient googlePlaces
	private final NPIRegistryClientHelper npiRegistry
	private final JsonHelper json
	private final Emitter emitter

	ReconciliationService(GooglePlacesClient googlePlaces, NPIRegistryClientHelper npiRegistry,JsonHelper json, Emitter emitter){
		this.googlePlaces = googlePlaces
		this.npiRegistry = npiRegistry
		this.json = json
		this.emitter = emitter
	}

	//	/**
	//	 *
	//	 * @param keywords - comma separated string, business name, address...
	//	 * @return
	//	 */
	//	public Business queryBusiness(String keywords){
	//		return queryBusiness(guessBusinessType(keywords), keywords)
	//	}
	//
	//	public Business queryBusiness(Type businessType, String keywords){
	//		Business result
	//		String resp = query(keywords)
	//		if(StringUtils.isNotBlank(resp)){
	//			result = json.from(query(keywords), new Business())
	//			result.setType(businessType)
	//			emitter.saveBusiness(result)
	//		}
	//		return result
	//	}
	//
	//	private Type guessBusinessType(String q){
	//		String k = !q ?:  StringUtils.substring(q, 0, q.indexOf(','))
	//		return Type.valueOfIgnoreCase(k)
	//	}
	//
	//	public Business queryPhysician(String keywords){
	//		Business business = queryBusiness(Type.doctor, keywords)
	//		return
	//	}
	//	/**
	//	 * @param keywords
	//	 * @return JSON String of google place detail
	//	 */
	//	public String queryPlace(String keywords){
	//		String q = groomKeywords(keywords)
	//				if(!q){
	//					return null
	//				}
	//		String place = null
	//				try {
	//					String placeId = json.read(googlePlaces.query(q), '$.results[0].place_id')
	//							place = googlePlaces.detail(placeId)
	//				} catch (PathNotFoundException e) {
	//					log.warn "Unable to resolve : ${q}"
	//				}
	//		return place
	//	}
	/**
	 * @param keywords - business name, street, city, state, zip
	 * @return List of hospital
	 */
	public List<Hospital> hospitals(String keywords){
		List<Hospital> hospitals = new ArrayList()
		List<String> hospitalPids =  json.read(queryPlaces(keywords, Type.hospital), '$.results[*].place_id')
		if(hospitalPids){
			log.info("${hospitalPids.size()} results found for ${keywords}")
			hospitalPids.each{ pid ->
				Hospital h = new Hospital(placeDetail(pid))
				hospitals.add(h)
				emitter.saveOrUpdateHospital(h)
			}
		}else{
			log.warn("No result found for : ${keywords}")
		}
		return hospitals;

	}

	/**
	 * @param keywords - business name, street, city, state, zip
	 * @return List of google places
	 */
	public List<Place> places(String keywords){
		List<Place> places = new ArrayList()
		List<String> placeIds =  json.read(queryPlaces(keywords), '$.results[*].place_id')
		if(placeIds){
			log.info("${placeIds.size()} results found for ${keywords}")
			placeIds.each{ pid ->
				places.add(placeDetail(pid))
			}
		}else{
			log.warn("No result found for : ${keywords}")
		}
		return places;
	}

	/**
	 * @param placeid
	 * @return google place detail
	 */
	public Place placeDetail(String pid){
		Place place = json.from(queryPlaceDetail(pid), new Place())
		emitter.saveOrUpdatePlace(place)
		return place
	}


	/**
	 * @param keywords
	 * @return JSON String of google places
	 */
	public Map<String, Object>  queryPlaces(String keywords, Type type){
		keywords = groomKeywords(keywords)
		return keywords ? googlePlaces.query(keywords, type) : null;
	}

	/**
	 * @param keywords
	 * @return JSON String of google places
	 */
	public Map<String, Object>  queryPlaces(String keywords){
		keywords = groomKeywords(keywords)
		return keywords ? googlePlaces.query(keywords) : null;
	}

	/**
	 * @param placeId
	 * @return JSON String of google place detail
	 */
	public Map<String, Object> queryPlaceDetail(String placeId){
		return placeId ? googlePlaces.detail(placeId) : null
	}

	/**
	 * 
	 * @param q
	 * @returnJSON String of npi registry result
	 */
	public Map<String, Object> queryNpi(NPIQuery q){
		return q ? npiRegistry.query(q) : null
	}

	/**
	 * 
	 * @param keywords
	 * @return keywords without 'null', multi whitespace, multi tab, multi backslash.
	 */
	public static String groomKeywords(String keywords){
		return keywords.toLowerCase().replace('\t', ' ').replaceAll('null', '').replaceAll('\\W -,.&', '').trim().replaceAll(' +', ' ')
	}
}
