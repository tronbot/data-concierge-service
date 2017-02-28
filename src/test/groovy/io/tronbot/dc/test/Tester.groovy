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
import io.tronbot.dc.helper.JsonHelper
import io.tronbot.dc.service.ReconciliationService
import io.tronbot.dc.utils.StringHelper

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 22, 2017
 */
@Log4j
class Tester {
	static final Gson gson = new Gson()
	static final JsonPathReflector jsonPathReflector = new JsonPathReflector(Configuration.defaultConfiguration())
	static final JsonHelper jsonHelper = new JsonHelper(gson, jsonPathReflector)

	public void testGooglePlaces(){
		//Google Places
		String keywords = "ucsd emer physicians thornton,9300 campus point dr,la jolla,ca"
		String placesURL = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyCzR2RLfJp-fF1Ui0tPRwKXLNWTDXDUu3E&query=${URLEncoder.encode(ReconciliationService.groomKeywords(keywords), 'UTF-8')}"
		Map<String, Object> jsonMap = jsonHelper.stringToMap(IOUtils.toString(new URL(placesURL), Charset.defaultCharset()))
		String jsonStr = jsonHelper.mapToString(jsonMap)
		println jsonMap
		println jsonStr
		println jsonHelper.stringToMap(null)
		println jsonHelper.mapToString(null)
		println jsonHelper.stringToMap("")
		println jsonHelper.mapToString(new HashMap<String,Object>())
		println Type.insurance_agency
		List<String> placeIds = jsonHelper.read(jsonMap, '$.results[*].place_id')
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
			Place place = jsonHelper.from(IOUtils.toString(new URL(placeDetailURL)), new Place())
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
		List npis = jsonHelper.read(IOUtils.toString(new URL(npiURL)), '$.results')
		if(!npis){
			npiURL = "https://npiregistry.cms.hhs.gov/api?number=&enumeration_type=NPI-1&taxonomy_description=&first_name=${firstName}&last_name=${lastName}&organization_name=&address_purpose=&city=&state=${state}&postal_code=&country_code=US&limit=200&skip=&pretty="
			npis = jsonHelper.read(IOUtils.toString(new URL(npiURL)), '$.results')
		}

		npis.any { npi ->
			Physician physician = jsonHelper.from(npi, new Physician())
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
		final String origins = ReconciliationService.groomKeywords(URLEncoder.encode("${address}, ${city}, ${state}, ${postalCode}", 'UTF-8'))
		final String destinations = URLEncoder.encode(physician.getAddressString(), 'UTF-8')
		String distanceURL = "https://maps.googleapis.com/maps/api/distancematrix/json?key=AIzaSyDtjzZV79yvZeVKaXiLQghUvIBaWLnYZeY&origins=${origins}&destinations=${destinations}"
		Integer distance = jsonHelper.read(IOUtils.toString(new URL(distanceURL)), '$.rows[0].elements[0].distance.value')
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
		List providers = jsonHelper.read(IOUtils.toString(new URL(npiURL)), '$.results[*]')
		providers.each { pJson ->
			Physician p = jsonHelper.from(pJson, new Physician())
			Assert.assertTrue(StringUtils.equals(q.getLastName(),p.getLastName()));
			println p.keywords()
			println p.getAddressString()
			println ReflectionToStringBuilder.toString(p)
		}
	}

	@Test
	public void testKeywords(){
		String str = StringHelper.groomKeywords("PHILLIP, REICH,9300 campus point dr,la jolla,ca,92037,858-554-9100")
		List strs = str.split(',') as List
		String firstName = strs[7]
		
		println firstName
	}
	
	public void tempTest(){
		String keywords = 'CHORNG LII , HWANG , 81709 DR CARREON BLVD, INDIO, CA, 92201'
		keywords = ReconciliationService.groomKeywords(keywords);

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
