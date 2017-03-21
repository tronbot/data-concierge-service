package io.tronbot.dc.test

import java.nio.charset.Charset

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.junit.Assert
import org.junit.Test

import com.google.gson.Gson
import com.jayway.jsonpath.Configuration

import groovy.util.logging.Log4j
import io.tronbot.dc.client.NPIQuery
import io.tronbot.dc.common.json.JsonPathReflector
import io.tronbot.dc.common.json.PostalCodeInterpreter
import io.tronbot.dc.domain.Physician
import io.tronbot.dc.domain.Place
import io.tronbot.dc.domain.Place.Type
import io.tronbot.dc.helper.GeoHelper
import io.tronbot.dc.helper.JsonHelper
import io.tronbot.dc.helper.StringHelper

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 22, 2017
 */
@Log4j
class Tester {
	static final Gson gson = new Gson()
	static final JsonPathReflector jsonPathReflector = new JsonPathReflector(Configuration.defaultConfiguration())
	static final JsonHelper json = new JsonHelper(gson, jsonPathReflector)
	//	@Test
	public void testGooglePlaces(){
		//Google Places
		String keywords = "ucsd emer physicians thornton,9300 campus point dr,la jolla,ca"
		String placesURL = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyCzR2RLfJp-fF1Ui0tPRwKXLNWTDXDUu3E&query=${URLEncoder.encode(StringHelper.groomKeywords(keywords), 'UTF-8')}"
		Map<String, Object> jsonMap = json.stringToMap(IOUtils.toString(new URL(placesURL), Charset.defaultCharset()))
		String jsonStr = json.mapToString(jsonMap)
		println jsonMap
		println jsonStr
		println json.stringToMap(null)
		println json.mapToString(null)
		println json.stringToMap("")
		println json.mapToString(new HashMap<String,Object>())
		println Type.insurance_agency
		List<String> placeIds = json.read(jsonMap, '$.results[*].place_id')
		List<Place> places = new ArrayList()
		if(placeIds){
			println "${placeIds.size()} results ${placeIds} found for ${keywords}"
		}else{
			println "Not result found for : ${keywords}"
		}

		placeIds.each{ pid ->
			//Query Place Detail
			String placeDetailURL = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyCzR2RLfJp-fF1Ui0tPRwKXLNWTDXDUu3E&placeid=${pid}"
			println "Query URL: ${placeDetailURL}"
			Place place = json.from(IOUtils.toString(new URL(placeDetailURL)), new Place(), '$.result')
			places << place
			println ReflectionToStringBuilder.toString(place)
		}
		//Bean clone
		println Objects.equals(places[0], places[1])
		println ReflectionToStringBuilder.toString(places[0])
		BeanUtils.copyProperties(places[0], places[1])
		println ReflectionToStringBuilder.toString(places[0])
		println Objects.equals(places[0], places[1])
	}


	public void testNPIRanking(){
		final String firstName = 'Douglas'
		final String lastName = 'Nguyen'
		final String address = '501 E Peltason'
		final String city = 'Irvine'
		final String state = 'CA'
		final String postalCode = null
		final String phoneNumber = null

		Map<Double, Physician> physicians = new TreeMap()
		String npiURL = "https://npiregistry.cms.hhs.gov/api?number=&enumeration_type=NPI-1&taxonomy_description=&first_name=${firstName}&last_name=${lastName}&organization_name=&address_purpose=&city=${city}&state=${state}&postal_code=${postalCode}&country_code=US&limit=200&skip=&pretty="
		List npis = json.read(IOUtils.toString(new URL(npiURL)), '$.results')
		if(!npis){
			npiURL = "https://npiregistry.cms.hhs.gov/api?number=&enumeration_type=NPI-1&taxonomy_description=&first_name=${firstName}&last_name=${lastName}&organization_name=&address_purpose=&city=&state=${state}&postal_code=&country_code=US&limit=200&skip=&pretty="
			npis = json.read(IOUtils.toString(new URL(npiURL)), '$.results')
		}

		npis.any { npi ->
			Physician physician = json.from(npi, new Physician())
			Double soccer = 0d
			if(npis.size() > 1){
				soccer = soccerPhysicians(physician,firstName,lastName,address,city,state,postalCode,phoneNumber )
			}
			if(soccer < 200001){
				physicians.put(soccer + Math.random(), physician)
			}
			if(soccer < 500){
				// since we found the matching phone number return there
				return true
			}
		}
		physicians.each { k, v ->
			println "${k}\t: ${ToStringBuilder.reflectionToString(v)}"
		}
	}

	Double soccerPhysicians(final Physician physician,
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
		//		if(postalCode && physician.getAddress().getPostalCode() && StringUtils.equals(firstFiveZip(postalCode), firstFiveZip(physician.getAddress().getPostalCode()))){
		//			soccer -= 10000
		//		}
		final String origins = StringHelper.groomKeywords(URLEncoder.encode("${address}, ${city}, ${state}, ${postalCode}", 'UTF-8'))
		final String destinations = URLEncoder.encode(physician.getAddressString(), 'UTF-8')
		String distanceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyDtjzZV79yvZeVKaXiLQghUvIBaWLnYZeY&origins=${origins}&destinations=${destinations}"
		Integer distance = json.read(IOUtils.toString(new URL(distanceURL)), '$.rows[0].elements[0].distance.value')
		return soccer += distance?distance:150000
	}


	String firstFiveZip(String postalCode){
		if(!postalCode){
			return null;
		}
		return StringUtils.substring(postalCode, 0, 5)
	}


	String stripPhoneNumber(String phoneNumber){
		if(!phoneNumber){
			return null;
		}
		return StringUtils.replaceAll(phoneNumber, '[\\D]','').substring(0,10)
	}


	public void testNPIRegistry(){
		//jsonize the NPIQuery
		NPIQuery q = new NPIQuery()
		q.setFirstName('TONY')
		q.setLastName('HWANG')
		q.setCity('INDIO')
		q.setState('CA')
		q.setPostalCode('92201')
		q.setOrganizationName('HWANG CHORNG LII')
		q.setNumber('')
		q.setTaxonomyDescription('')


		String npiQuery = gson.toJson(q)
		println npiQuery

		String npiURL = 'https://npiregistry.cms.hhs.gov/api?number=1629040878'
		List providers = json.read(IOUtils.toString(new URL(npiURL)), '$.results[*]')
		providers.each { pJson ->
			Physician p = json.from(pJson, new Physician())
			Assert.assertTrue(StringUtils.equals(q.getLastName(),p.getLastName()));
			println p.keywords()
			println p.getAddressString()
			println ReflectionToStringBuilder.toString(p)
		}
	}

	public void testKeywords(){
		String keywords = StringHelper.groomKeywords('Robin')
		if(!keywords){
			println 'No keywords... return!!'
			return
		}

		List<String> breakdowns = keywords.split(',') as List<String>
		String firstName = (breakdowns[0]?.split(' ') as List).find()
		String lastName = (breakdowns[1]?.split(' ') as List).find()
		String address = breakdowns[2]
		String city = breakdowns[3]
		String state = breakdowns[4]
		String postalCode = breakdowns[5]
		String phoneNumber = breakdowns[6]
		println firstName
		println lastName
		println address
		println city
		println state
		println postalCode
		println phoneNumber
	}


	private Place getPlace(String address){
		String geocodeURL = "https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(address, 'UTF-8')
		return json.from(IOUtils.toString(new URL(geocodeURL), 'UTF-8'), new Place(), '$.results[0]')
	}

	private Integer getDistanceByGoogle(String origins, String destinations){
		String distanceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyDtjzZV79yvZeVKaXiLQghUvIBaWLnYZeY&origins=${URLEncoder.encode(origins, 'UTF-8')}&destinations=${URLEncoder.encode(destinations, 'UTF-8')}"
		return json.read(IOUtils.toString(new URL(distanceURL), 'UTF-8'), '$.rows[0].elements[0].distance.value')
	}


	@Test
	public void testGeocode(){
		String origins = '123 main st, reisterstown, md, 21136'
		String destinations = '6455 Irvine Center Dr, Irvine, CA 92618'
		Place originsPlace = getPlace(origins)
		Place destinationsPlace = getPlace(destinations)
		Integer distance = getDistanceByGoogle(origins, destinations)
		println "Geohelp distance: ${GeoHelper.distance(originsPlace, destinationsPlace)}"
		println "Google Map distance: ${distance}"
	}


	public void testAddress(){
		String address = '911 Sunset Dr,Hollister,CA'
		String geocodeURL = "https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(address, 'UTF-8')
		Place place = json.from(IOUtils.toString(new URL(geocodeURL), 'UTF-8'), new Place(), '$.results[0]')
		println ToStringBuilder.reflectionToString(place)
		Object res = json.read(IOUtils.toString(new URL(geocodeURL), 'UTF-8'), '$.results[0]')
		Place place2 = json.from(res, new Place())
		println ToStringBuilder.reflectionToString(place2)
	}

	public void tempTest(){
		String keywords = 'Robin,Zasio Kipp,9300 Tech Center Dr,Sacramento,CA'
		keywords = StringHelper.groomKeywords(keywords);

		String firstName = keywords.split(',')[0].trim()
		String lastName = keywords.split(',')[1].trim()
		String addressKeyword =
				println firstName
		println lastName
		println addressKeyword

		def array = []
		if(array){
			println "array exists"
		}else{
			println "array is null"
		}
		println array?.find()

		Physician physician = new Physician()

		physician.setFirstName('adadf')
		physician.setLastName('zxcvzxc')
		println physician

		PostalCodeInterpreter postalCodeInterpreter = new PostalCodeInterpreter()
		println postalCodeInterpreter.interpret(null)
	}
}
