package io.tronbot.dc.test

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.junit.Test

import groovy.util.logging.Log4j
import io.tronbot.dc.domain.Business
import io.tronbot.json.JsonPathReflector

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 13, 2017
 */
@Log4j
class JsonTester {
	@Test
	public void testReflector() throws Exception{
		URL url = new URL('http://localhost:8000/reconciliation/businessRaw?q=Saint%20Thomas%20Hickman%20Hospital,135%20E%20Swan%20St,%20Centerville,TN,37033,%20')
		//		ObjectMapper mapper = new ObjectMapper()
		//		JsonNode root = mapper.readTree(url)
		//		println root.at('/result/id').asText()
		//		Business biz = mapper.readValue(url, Business.class)
		//		println ReflectionToStringBuilder.reflectionToString(biz)
		//
		//		Gson gson = new Gson();
		//		biz = gson.fromJson(IOUtils.toString(url), Business.class)
		//		println ReflectionToStringBuilder.reflectionToString(biz)
		//		//		Business business = JSONPopulater.eval(json, new Business())
		JsonPathReflector jr = new JsonPathReflector()
		Business biz = jr.from(IOUtils.toString(url), new Business())
		println ReflectionToStringBuilder.reflectionToString(biz);
		//		println JsonPath.read(IOUtils.toString(url), '$.result.id')

	}
}
