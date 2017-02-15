package io.tronbot.dc.test

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.builder.ReflectionToStringBuilder
import org.junit.Test

import groovy.util.logging.Log4j
import io.tronbot.dc.domain.Business
import io.tronbot.dc.common.json.JsonPathReflector

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 13, 2017
 */
@Log4j
class JsonTester {
	@Test
	public void testReflector() throws Exception{
		URL url = new URL('http://localhost:8000/reconciliation/businessRaw?q=Saint%20Thomas%20Hickman%20Hospital,135%20E%20Swan%20St,%20Centerville,TN,37033,%20')
		JsonPathReflector jr = new JsonPathReflector()
		Business biz = jr.from(IOUtils.toString(url), new Business())
		println ReflectionToStringBuilder.reflectionToString(biz);
	}
}
