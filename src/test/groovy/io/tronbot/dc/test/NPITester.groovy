package io.tronbot.dc.test

import java.nio.charset.Charset

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.junit.Test

import com.google.gson.Gson
import com.jayway.jsonpath.Configuration

import groovy.util.logging.Log4j
import io.tronbot.dc.client.NPIQuery
import io.tronbot.dc.common.json.JsonPathReflector
import io.tronbot.dc.domain.Place
import io.tronbot.dc.domain.Place.Type
import io.tronbot.dc.helper.JsonHelper
import io.tronbot.dc.service.ReconciliationService

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 22, 2017
 */
@Log4j
class NPITester {
	@Test
	public void testNPIResponse(){
		Gson gson = new Gson()
		JsonPathReflector jsonPathReflector = new JsonPathReflector(Configuration.defaultConfiguration())
		JsonHelper jsonHelper = new JsonHelper(gson, jsonPathReflector)
		//jsonize the NPIQuery
		NPIQuery q = new NPIQuery()
		q.setFirstName('TONY')
		q.setLastName('HWANG')
		q.setCity('INDIO')
		q.setState('CA')
		q.setPostalCode('92201')
		String npiQuery = gson.toJson(q)
		String npiRawURL = "http://localhost:8000/reconciliation/raw/npi?'${npiQuery}'"
		println npiRawURL

		//Query Places
		String keywords = "ucsd emer physicians thornton,9300 campus point dr,la jolla,ca"
		String placesURL = "https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyCzR2RLfJp-fF1Ui0tPRwKXLNWTDXDUu3E&query=${URLEncoder.encode(ReconciliationService.groomKeywords(keywords), "UTF-8")}"
		Map<String, Object> jsonMap = jsonHelper.stringToMap(IOUtils.toString(new URL(placesURL), Charset.defaultCharset()))
		String jsonStr = jsonHelper.mapToString(jsonMap);
		println jsonMap
		println jsonStr
		println jsonHelper.stringToMap(null);
		println jsonHelper.mapToString(null);
		println jsonHelper.stringToMap("");
		println jsonHelper.mapToString(new HashMap<String,Object>());
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
		
		println ReflectionToStringBuilder.toString(places[0])
		BeanUtils.copyProperties(places[0], places[1])
		println ReflectionToStringBuilder.toString(places[0])
	}
}
