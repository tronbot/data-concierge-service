package io.tronbot.dc.server

import javax.persistence.Entity
import javax.persistence.Id

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import feign.Request
import groovy.util.logging.Log4j

/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 2, 2017
 */

@RestController
@RequestMapping('/reconciliation')
@RefreshScope
@Log4j
class ReconciliationController {
	private final ReconciliationService service

	ReconciliationController(ReconciliationService service){
		this.service=service
	}

	@GetMapping('/business')
	@ResponseBody
	public ResponseEntity business(@RequestParam('q') String keywords){
		def res = service.queryBusiness(keywords)
		return ResponseEntity.ok(res)
	}
}

@Service
@Log4j
public class ReconciliationService{
	private final GooglePlacesClient client
	private final RequestHistoryRepository repository

	ReconciliationService(GooglePlacesClient client, RequestHistoryRepository repository){
		this.client=client
		this.repository = repository
	}

	Object queryBusiness(String keywords){
		keywords = keywords.toLowerCase().replace('\t', ' ').replaceAll('null', '').replaceAll('\\W -,.&', '').trim().replaceAll(' +', ' ')
		def res = client.query(keywords);
		return res
	}
}

@Repository
interface RequestHistoryRepository extends JpaRepository<RequestHistory, String>{
}

public class Reconciliation<T>{
	T entity
	Date refreshDate
	public Reconciliation(T entity, Date refreshDate){
		this.entity = entity
		this.refreshDate = refreshDate
	}
	public Reconciliation(T entity){
		this.entity = entity
		this.refreshDate = new Date()
	}
}


@Entity
public class RequestHistory{
	// id: operation|parameter
	@Id
	String id
	String response
	Date timestamp
}


@FeignClient(name = 'GooglePlaces', url = '${google.places.api.url}', configuration = GooglePlacesFeignConfiguration.class)
public interface GooglePlacesClient {

	@Cacheable('google-places')
	@GetMapping('textsearch/json?query={keywords}&key=${google.places.api.key}')
	Map query(@PathVariable('keywords') String keywords);

	@Cacheable('google-places-detail')
	@GetMapping('details/json?placeid=${place_id}&key=${google.places.api.key}')
	Map detail(@PathVariable('place_id') String placeId);
}


@Configuration
public class GooglePlacesFeignConfiguration{
	@Bean
	public Request.Options options(@Value('${google.places.api.timeout}') Integer timeout) {
		return new Request.Options(timeout, timeout);
	}
}

