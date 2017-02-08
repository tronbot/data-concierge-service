package io.tronbot.dc.client

import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.netflix.feign.EnableFeignClients
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

import io.tronbot.dc.config.GooglePlacesFeignConfiguration



/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Component
@EnableFeignClients
@FeignClient(name = 'GooglePlaces', url = '${google.places.api.url}', configuration = GooglePlacesFeignConfiguration.class)
public interface GooglePlacesClient {

	@Cacheable('persistableCache')
	@GetMapping('textsearch/json?query={keywords}&key=${google.places.api.key}')
	Map query(@PathVariable('keywords') String keywords);

	@Cacheable('persistableCache')
	@GetMapping('details/json?placeid=${place_id}&key=${google.places.api.key}')
	Map detail(@PathVariable('place_id') String placeId);
}