package io.tronbot.dc.server

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import feign.Request

/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 2, 2017
 */

@RestController
@RequestMapping('/reconciliation')
@RefreshScope
class ReconciliationController {
	private final GooglePlacesClient client;


	ReconciliationController(GooglePlacesClient client){
		this.client=client
	}

	@GetMapping('/business')
	public Object business(@RequestParam('q') String keywords){
		return client.query(keywords)
	}
}


@FeignClient(name = 'GooglePlaces', url = '${google.places.api.url}', configuration = GooglePlacesFeignConfiguration.class)
public interface GooglePlacesClient {

	@Cacheable
	@RequestMapping(method = RequestMethod.GET, value = 'textsearch/json?query={keywords}&key=${google.places.api.key}')
	Object query(@PathVariable('keywords') String keywords);

	@Cacheable
	@RequestMapping(method = RequestMethod.GET, value = 'details/json?placeid=${place_id}&key=${google.places.api.key}')
	Object detail(@PathVariable('place_id') String placeId);
}


@Configuration
public class GooglePlacesFeignConfiguration{
	@Bean
	public Request.Options options(@Value('${google.places.api.timeout}') Integer timeout) {
		return new Request.Options(timeout, timeout);
	}
}