package io.tronbot.dc.service

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service

import groovy.util.logging.Log4j
import io.tronbot.dc.client.GoogleMapsClient
import io.tronbot.dc.client.NPIQuery
import io.tronbot.dc.client.NPIRegistryClientHelper
import io.tronbot.dc.dao.PhysicianRepository
import io.tronbot.dc.domain.Hospital
import io.tronbot.dc.domain.Physician
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
	private final GoogleMapsClient googleMaps
	private final NPIRegistryClientHelper npiRegistry
	private final JsonHelper json
	private final Emitter emitter

	ReconciliationService(GoogleMapsClient googleMaps, NPIRegistryClientHelper npiRegistry,JsonHelper json, Emitter emitter){
		this.googleMaps = googleMaps
		this.npiRegistry = npiRegistry
		this.json = json
		this.emitter = emitter
	}


	/**
	 * 
	 * @param firstName
	 * @param lastName
	 * @param address 123 main street, big city, mighty state, 54321
	 * @return List of Physicians in ranking
	 */
	public Collection<Physician> physicians(String firstName, String lastName, String address, String city, String state, String postalCode, String phoneNumber){
		Map<Double, Physician> physicians = new TreeMap()
		NPIQuery query = new NPIQuery()
		query.setFirstName(firstName)
		query.setLastName(lastName)
		query.setCity(city)
		query.setState(state)
		query.setPostalCode(postalCode)
		List npis = json.read(npiRegistry.query(query), '$.results')
		if(!npis){
			query.setCity('')
			query.setPostalCode('')
			npis = json.read(npiRegistry.query(query), '$.results')
		}
		npis.any { npi ->
			Physician physician = json.from(npi, new Physician())
			Double soccer = 0d
			if(npis.size() > 1){
				soccer = soccerPhysicians(physician,firstName,lastName,address,city,state,postalCode,phoneNumber)
			}
			if(soccer < 200001){
				// within 200km radius
				physician.setPlace(resolvePlaceForPhysician(physician))
				physicians.put(soccer + Math.random(), physician)
				emitter.saveOrUpdatePhysician(physician)
			}
			if(soccer < 500){
				// since we found the matching phone number return there
				return true
			}
		}
		return physicians.values()
	}

	private Place queryPlaceDetailByKeywords(String firstName, String lastName, String address, String city, String state){
		String keywords = "${firstName} ${lastName},${address},${city},${state}"
		String placeId = json.read(queryPlaces(keywords), '$.results[0].place_id')
		if(!placeId){
			keywords = "${address}, ${city}, ${state}"
			placeId = json.read(queryPlaces(keywords), '$.results[0].place_id')
		}
		return json.from(queryPlaceDetail(placeId), new Place())
	}

	private Double soccerPhysicians(final Physician physician,
			final String firstName,
			final String lastName,
			final String address,
			final String city,
			final String state,
			final String postalCode,
			final String phoneNumber){
		Double soccer = 0
		// if check if phone number match
		if(phoneNumber && physician.getPhoneNumber() && StringUtils.equals(stripPhoneNumber(phoneNumber), stripPhoneNumber(physician.getPhoneNumber()))){
			return soccer
		}
		final String origins = groomKeywords("${address}, ${city}, ${state}, ${postalCode}")
		final String destinations = groomKeywords(physician.getAddressString())
		Integer distance = json.read(googleMaps.distance(origins, destinations), '$.rows[0].elements[0].distance.value')
		return soccer += distance?distance:150000
	}

	/**
	 *
	 * @param id - npi number
	 * @return Physician
	 */
	public Physician npi(String id){
		Physician physician = null
		NPIQuery query = new NPIQuery()
		query.setNumber(id)
		Map<String, Object> npiJson = json.read(npiRegistry.query(query), '$.results[0]')
		if(npiJson){
			physician = json.from(npiJson, new Physician())
			physician.setPlace(resolvePlaceForPhysician(physician))
			emitter.saveOrUpdatePhysician(physician)
		}
		return physician
	}


	private Place resolvePlaceForPhysician(Physician physician){
		if(!physician){
			return null
		}
		String placeId = json.read(queryPlaces(physician.keywords()), '$.results[0].place_id')
		if(!placeId){
			log.info("Unable to find Physician[${physician.npi}] by keywords[${physician.keywords()}] in google, getting street address by [${physician.getAddressString()}] instead.")
			placeId = json.read(queryPlaces(physician.getAddressString()), '$.results[0].place_id')
		}
		return json.from(queryPlaceDetail(placeId), new Place())
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
				BeanUtils.copyProperties(h, json.from(queryPlaceDetail(pid), new Place()))
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
		return keywords ? googleMaps.query(keywords, type) : null
	}

	/**
	 * @param keywords
	 * @return JSON String of google places
	 */
	public Map<String, Object>  queryPlaces(String keywords){
		keywords = groomKeywords(keywords)
		return keywords ? googleMaps.query(keywords) : null
	}

	/**
	 * @param placeId
	 * @return JSON String of google place detail
	 */
	public Map<String, Object> queryPlaceDetail(String placeId){
		return placeId ? googleMaps.detail(placeId) : null
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

	public static String firstFiveZip(String postalCode){
		if(!postalCode){
			return null;
		}
		return StringUtils.substring(postalCode, 0, 5)
	}


	public static String stripPhoneNumber(String phoneNumber){
		if(!phoneNumber){
			return null;
		}
		return StringUtils.replaceAll(phoneNumber, '[\\D]','').substring(0,10)
	}
}
