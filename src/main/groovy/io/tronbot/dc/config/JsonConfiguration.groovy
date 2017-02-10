package io.tronbot.dc.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import groovy.json.JsonSlurper

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 10, 2017
 */
@Configuration
class JsonConfiguration {

	@Bean
	JsonSlurper jsonSlurper(){
		return new JsonSlurper()
	}
}
