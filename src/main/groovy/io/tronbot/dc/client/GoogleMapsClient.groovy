package io.tronbot.dc.client

import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

import io.tronbot.dc.config.GooglePlacesFeignConfiguration
import io.tronbot.dc.domain.Place.Type



/**
 * @author <a href = "mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@FeignClient(name = 'GoogleMapsClient', url = '${google.maps.api.url}', configuration = GooglePlacesFeignConfiguration.class)
public interface GoogleMapsClient {
	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping('place/textsearch/json?key=${google.places.api.key}&query={keywords}&type={types}')
	Map<String, Object> query(@PathVariable('keywords') String keywords, @PathVariable('types') Type... types)

	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping('place/details/json?key=${google.places.api.key}&placeid={place_id}')
	Map<String, Object> detail(@PathVariable('place_id') String placeId)
	
	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping('distancematrix/json?key=${google.maps.api.key}&origins={origins}&destinations={destinations}')
	Map<String, Object> distance(@PathVariable('origins') String origins,@PathVariable('destinations') String destinations)
	
	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping('geocode/json?address={address}')
	Map<String, Object> address(@PathVariable('address') String address)
}