package io.tronbot.dc.web.rest

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import groovy.util.logging.Log4j
import io.tronbot.dc.client.NPIQuery
import io.tronbot.dc.dto.Reconciliation
import io.tronbot.dc.service.ReconciliationService

/**
 * placeRaw('business name, address')
 * npiRaw(string response of nipRegistry)
 * npi(string response of nipRegistry)
 * place('business name, address')
 * hospital('business name, address') equal to business, with desiretype of hospital
 * practice ('organization name, address') - google place + NPI response  = Practice(Persist)
 * physician('firstname lastname, address') - google place + NPI response = Physician(Persist)
 * 
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 2, 2017
 */

@RestController
@RequestMapping('/reconciliation')
@Log4j
class ReconciliationController {
	private final ReconciliationService service

	ReconciliationController(ReconciliationService service){
		this.service = service
	}

	@GetMapping('/raw/places')
	public @ResponseBody ResponseEntity placesRaw(@RequestParam('q') String keywords){
		Object result = service.queryPlaces(keywords)
		return result ? Reconciliation.accurate(result) : Reconciliation.notFound()
	}

	@GetMapping('/raw/places/{placeId}')
	public @ResponseBody ResponseEntity placeDetailRaw(@PathVariable String placeId){
		Object result = service.queryPlaceDetail(placeId)
		return result ? Reconciliation.accurate(result) : Reconciliation.notFound()
	}

	@PutMapping('/raw/npi')
	public @ResponseBody ResponseEntity npiRaw(@RequestBody NPIQuery query){
		Object result = service.queryNpi(query)
		return result ? Reconciliation.accurate(result) : Reconciliation.notFound()
	}

	/**
	 * @param keywords - business name, street, city, state, zip
	 * @return json list of google places
	 */
	@GetMapping('/places')
	public @ResponseBody ResponseEntity places(@RequestParam('q') String keywords){
		Object result = service.places(keywords)
		return result ? Reconciliation.accurate(result) : Reconciliation.notFound()
	}

	/**
	 * @param keywords - business name, street, city, state, zip
	 * @return json list of hospitals
	 */
	@GetMapping('/hospitals')
	public @ResponseBody ResponseEntity hospitals(@RequestParam('q') String keywords){
		Object result = service.hospitals(keywords)
		return result ? Reconciliation.accurate(result) : Reconciliation.notFound()
	}

	/**
	 * @param keywords - firstname lastname, street, city, state, zip
	 * @return json list of hospitals
	 */
	@GetMapping('/physicians')
	public @ResponseBody ResponseEntity physicians(@RequestParam('q') String keywords){
		Object result = service.physicians(keywords)
		return result ? Reconciliation.accurate(result) : Reconciliation.notFound()
	}


	@GetMapping('/npi/{id}')
	public @ResponseBody ResponseEntity npi(@PathVariable('id') String id){
		Object result = service.npi(id)
		return result ? Reconciliation.accurate(result) : Reconciliation.notFound()
	}
}




