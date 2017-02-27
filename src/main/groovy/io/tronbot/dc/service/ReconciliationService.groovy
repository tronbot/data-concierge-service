package io.tronbot.dc.service

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.stereotype.Service

import groovy.util.logging.Log4j
import io.tronbot.dc.client.GooglePlacesClient
import io.tronbot.dc.client.NPIQuery
import io.tronbot.dc.client.NPIRegistryClientHelper
import io.tronbot.dc.domain.Hospital
import io.tronbot.dc.domain.Physician
import io.tronbot.dc.domain.Place
import io.tronbot.dc.domain.Place.Type
import io.tronbot.dc.dto.Provider
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

	/**
	 * 
	 * @param id - npi number
	 * @return Physician
	 */
	private Physician npi(String id){
		Physician physician = null
		NPIQuery query = new NPIQuery()
		query.setNumber(id)
		Map<String, Object> npiJson = json.read(npiRegistry.query(query), '$.results[*]')?.find()
		if(npiJson){
			Provider provider = json.from(npiJson, new Provider())
			Place place = places(provider.keywords())?.find()
			if(!place){
				place = places(provider.address())?.find()
			}
			physician = new Physician()
			BeanUtils.copyProperties(physician, provider)
			BeanUtils.copyProperties(physician, place)
			emitter.saveOrUpdatePhysician(physician)
		}

		return physician
	}


	/**
	 * @param keywords - firstname lastname, street, city, state, zip
	 * @return List of hospital
	 */
	private List<Physician> queryPhysicians(String firstName, String lastName, String city, String state){
		List<Physician> physicians = new ArrayList()
		NPIQuery query = new NPIQuery()
		query.setFirstName(firstName)
		query.setLastName(lastName)
		query.setCity(city)
		query.setState(state)
		//		query.setPostalCode(postalCode)
		List npiJson = json.read(npiRegistry.query(query), '$.results[*]')
		npiJson?.each{ npi ->
			physicians.add(json.from(npi, new Physician()))
		}
		if(!npiJson){
			log.warn("No provider found for : ${ToStringBuilder.reflectionToString(query)}")
		}
		return physicians
	}


	/**
	 * @param keywords - firstname, lastname, street, city, state, zip
	 * @return List of hospital
	 */
	public List<Physician> physicians(String keywords){
		List<Physician> physicians = new ArrayList()
		if(!keywords){
			return physicians
		}
		Map<String, Object> placeJson = queryPlaces(keywords)
		if(!'OK'.equalsIgnoreCase(json.read(placeJson, 'status'))){
			placeJson =  queryPlaces(keywords.substring(StringUtils.ordinalIndexOf(keywords, ',', 2)+1))
		}
		List<String> physicianPids =  json.read(placeJson, '$.results[*].place_id')
		if(physicianPids){
			log.info("${physicianPids.size()} results found for ${keywords}")
			physicianPids.each{ pid ->
				String firstName = keywords.split(',')[0].trim()
				String lastName = keywords.split(',')[1].trim()
				Place place = placeDetail(pid)
				List<Physician> ps = queryPhysicians(firstName, lastName, place.getCity(), place.getState())
				if(ps){
					ps.each { physician ->
						BeanUtils.copyProperties(physician, place)
						physicians.add(physician)
						emitter.saveOrUpdatePhysician(physician)
					}
				}else{
					// Physician is not resolved in NPI Registry
					Physician physician = new Physician()
					physician.setFirstName(firstName)
					physician.setLastName(lastName)
					BeanUtils.copyProperties(physician, place)
					physicians.add(physician)
					emitter.saveOrUpdatePhysician(physician)
				}
			}
		}else{
			log.warn("No result found for : ${keywords}")
		}
		return physicians
	}


	/**
	 * @param keywords - business name, street, city, state, zip
	 * @return List of hospital
	 */
	public List<Hospital> hospitals(String keywords){
		List<Hospital> hospitals = new ArrayList()
		if(!keywords){
			return hospitals
		}
		Map<String, Object> hospitalJson = queryPlaces(keywords, Type.hospital)
		if(!'OK'.equalsIgnoreCase(json.read(hospitalJson, 'status'))){
			hospitalJson = queryPlaces(keywords.substring(StringUtils.indexOf(keywords, ',')+1), Type.hospital)
		}
		List<String> hospitalPids =  json.read(hospitalJson, '$.results[*].place_id')
		if(hospitalPids){
			log.info("${hospitalPids.size()} results found for ${keywords}")
			hospitalPids.each{ pid ->
				Hospital h = new Hospital()
				BeanUtils.copyProperties(h, placeDetail(pid))
				hospitals.add(h)
				emitter.saveOrUpdateHospital(h)
			}
		}else{
			log.warn("No result found for : ${keywords}")
		}
		return hospitals
	}

	/**
	 * @param keywords - business name, street, city, state, zip
	 * @return List of google places
	 */
	public List<Place> places(String keywords){
		List<Place> places = new ArrayList()
		if(!keywords){
			return places
		}
		List<String> placeIds =  json.read(queryPlaces(keywords), '$.results[*].place_id')
		if(placeIds){
			log.info("${placeIds.size()} results found for ${keywords}")
			placeIds.each{ pid ->
				places.add(placeDetail(pid))
			}
		}else{
			log.warn("No result found for : ${keywords}")
		}
		return places
	}

	/**
	 * @param placeid
	 * @return google place detail
	 */
	public Place placeDetail(String pid){
		if(!pid){
			return null
		}
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
		return keywords ? googlePlaces.query(keywords, type) : null
	}

	/**
	 * @param keywords
	 * @return JSON String of google places
	 */
	public Map<String, Object>  queryPlaces(String keywords){
		keywords = groomKeywords(keywords)
		return keywords ? googlePlaces.query(keywords) : null
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
		return keywords.toLowerCase().replace('\t', ' ').replaceAll('null', '').replaceAll('\\W -,.&', '').replace(', ', ',').trim().replaceAll(' +', ' ')
	}
}
