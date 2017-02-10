package io.tronbot.dc.client

import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody

import io.tronbot.dc.config.GooglePlacesFeignConfiguration



/**
 * @author <a href = "mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@FeignClient(name = 'GooglePlaces', url = '${google.places.api.url}', configuration = GooglePlacesFeignConfiguration.class)
public interface GooglePlacesClient {
	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping(value = 'textsearch/json?key=${google.places.api.key}&query={keywords}', produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	String query(@PathVariable('keywords') String keywords);

	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping(value = 'details/json?key=${google.places.api.key}&placeid={place_id}', produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	String detail(@PathVariable('place_id') String placeId);
}