package io.tronbot.dc.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import feign.Request

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */

@Configuration
public class GoogleApiFeignConfiguration{
	@Bean
	public Request.Options options(@Value('${google.api.connectTimeoutMillis}') Integer connectTimeoutMillis, 
								   @Value('${google.api.readTimeoutMillis}') Integer readTimeoutMillis) {
		return new Request.Options(connectTimeoutMillis, readTimeoutMillis);
	}
}

