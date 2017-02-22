package io.tronbot.dc.test

import java.nio.charset.Charset

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringEscapeUtils
import org.apache.commons.lang3.StringUtils
import org.junit.Test

import com.google.gson.Gson

import io.tronbot.dc.client.NPIQuery
import io.tronbot.dc.domain.Business.Type
import io.tronbot.dc.helper.JsonHelper
import io.tronbot.dc.service.ReconciliationService

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 22, 2017
 */
class NPITester {
	@Test
	public void testNPIResponse(){
		Gson gson = new Gson()
		JsonHelper jsonHelper = new JsonHelper(gson, null)
		NPIQuery q = new NPIQuery()
		q.setFirstName('TONY')
		q.setLastName('HWANG')
		q.setCity('INDIO')
		q.setState('CA')
		q.setPostalCode('92201')
		String str = "ucsd emer physicians thornton,9300 campus point dr,la jolla,ca"
		println ReconciliationService.groomKeywords(str)
		String npiQuery = gson.toJson(q)
		String npiRawURL = "http://localhost:8000/reconciliation/raw/npi?'${npiQuery}'"
		String googlePlaceURL = 'https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyCzR2RLfJp-fF1Ui0tPRwKXLNWTDXDUu3E&query=TONY%20HWANG,%2081709%20DR%20CARREON%20BLVD,%20INDIO,%20CA,%2092201'
		println npiRawURL
		Map<String, Object> jsonMap = jsonHelper.stringToMap(IOUtils.toString(new URL(googlePlaceURL), Charset.defaultCharset()))
		String jsonStr = jsonHelper.mapToString(jsonMap);
		println jsonMap
		println jsonStr
		println jsonHelper.stringToMap(null);
		println jsonHelper.mapToString(null);
		println jsonHelper.stringToMap("");
		println jsonHelper.mapToString(new HashMap<String,Object>());
		println Type.insurance_agency
		
		//		println IOUtils.toString(new URL(npiRawURL), Charset.defaultCharset())
	}
}
