package io.tronbot.dc.web.rest

import org.apache.commons.lang3.StringUtils
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
import io.tronbot.dc.helper.StringHelper
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
		return Reconciliation.resp(result)
	}

	@GetMapping('/raw/places/{placeId}')
	public @ResponseBody ResponseEntity placeDetailRaw(@PathVariable String placeId){
		Object result = service.queryPlaceDetail(placeId)
		return Reconciliation.resp(result)
	}

	@PutMapping('/raw/npi')
	public @ResponseBody ResponseEntity npiRaw(@RequestBody NPIQuery query){
		Object result = service.queryNpi(query)
		return Reconciliation.resp(result)
	}

	/**
	 * @param keywords - business name, street, city, state, zip
	 * @return json list of google places
	 */
	@GetMapping('/places')
	public @ResponseBody ResponseEntity places(@RequestParam('q') String keywords){
		Object result = service.places(keywords)
		return Reconciliation.resp(result)
	}

	/**
	 * @param keywords - business name, street, city, state, zip
	 * @return json list of hospitals
	 */
	@GetMapping('/hospitals')
	public @ResponseBody ResponseEntity hospitals(@RequestParam('q') String keywords){
		Object result = service.hospitals(keywords)
		return Reconciliation.resp(result)
	}

	/**
	 * @param keywords - firstname lastname, street, city, state, zip
	 * @return json list of hospitals
	 */
	@GetMapping('/physicians')
	public @ResponseBody ResponseEntity physicians(@RequestParam('q') String keywords)
			//			@RequestParam(value='firstName') String firstName,
			//			@RequestParam(value='lastName') String lastName,
			//			@RequestParam(value='address') String address,
			//			@RequestParam(value='city') String city,
			//			@RequestParam(value='state') String state,
			//			@RequestParam(value='postalCode', required=false) String postalCode,
			//			@RequestParam(value='phoneNumber', required=false) String phoneNumber)
	{
		keywords = StringHelper.groomKeywords(keywords)
		if(!keywords){
			return Reconciliation.resp(null)
		}
		List<String> breakdowns = keywords.split(',') as List<String>
		String firstName = (breakdowns[0]?.split(' ') as List).find()
		String lastName = (breakdowns[1]?.split(' ') as List).find()
		String address = breakdowns[2]
		String city = breakdowns[3]
		String state = breakdowns[4]
		String postalCode = breakdowns[5]
		String phoneNumber = breakdowns[6]
		if(!firstName || !lastName || !address || !city || !state ){
			return Reconciliation.resp(null)
		}else{
			firstName =breakdowns[0]?.split(' ')[0]
			lastName = breakdowns[1]?.split(' ')[0]
	
			Object result = service.physicians(firstName,lastName,address,city,state,postalCode,phoneNumber)
			return Reconciliation.resp(result)
		}
	}

	@GetMapping('/npi/{id}')
	public @ResponseBody ResponseEntity npi(@PathVariable('id') String id){
		Object result = service.npi(id)
		return Reconciliation.resp(result)
	}
}




