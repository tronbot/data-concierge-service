package io.tronbot.dc.config

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jayway.jsonpath.Configuration

import io.tronbot.dc.common.json.JsonPathReflector

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 10, 2017
 */
@org.springframework.context.annotation.Configuration
class JsonConfiguration {

	@Bean
	JsonPathReflector jsonPathReflector(Configuration jsonPathConfiguration){
		return new JsonPathReflector(jsonPathConfiguration)
	}

	@Bean
	Configuration jsonPathConfiguration(){
		return Configuration.defaultConfiguration()
	}

	@Bean
	Gson gson(){
		return new Gson();
	}
}