package io.tronbot.dc.client

import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

import io.tronbot.dc.config.GoogleApiFeignConfiguration
import io.tronbot.dc.domain.Place.Type



/**
 * @author <a href = "mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@FeignClient(name = 'GoogleApisClient', url = '${google.api.url}', configuration = GoogleApiFeignConfiguration.class)
public interface GoogleApisClient {
	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping('place/textsearch/json?key=${google.api.keys.place}&query={keywords}&type={types}')
	Map<String, Object> queryPlaces(@PathVariable('keywords') String keywords, @PathVariable('types') Type... types)

	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping('place/details/json?key=${google.api.keys.place}&placeid={place_id}')
	Map<String, Object> placeDetail(@PathVariable('place_id') String placeId)
	
	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping('distancematrix/json?key=${google.api.keys.distance}&origins={origins}&destinations={destinations}')
	Map<String, Object> distance(@PathVariable('origins') String origins,@PathVariable('destinations') String destinations)
	
	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping('geocode/json?key=${google.api.keys.geocode}&address={address}')
	Map<String, Object> geocode(@PathVariable('address') String address)
}