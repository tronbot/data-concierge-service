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


	//	private ResponseEntity<Reconciliation> businessInception(String keywords, Type... businessTypes){
	//		Type businessType = businessTypes[0]
	//		Business result = service.queryBusiness(businessType, keywords)
	//		if(result){
	//			return Reconciliation.accurate(result)
	//		}else if(businessTypes){
	//			String name = !keywords ?:  StringUtils.substring(keywords, 0, keywords.indexOf(','))
	//			businessTypes.each { type ->
	//				String confidentKeywords = StringUtils.replaceFirst(keywords, name, type.toString())
	//				result = service.queryBusiness(businessType, confidentKeywords)
	//				if(result){
	//					return
	//				}
	//			}
	//			if(result){
	//				return Reconciliation.confident(result)
	//			}
	//		}
	//		String possibleKeywords = StringUtils.substring(keywords, keywords.indexOf(',')+1)
	//		result = service.queryBusiness(businessType, possibleKeywords)
	//		return result ? Reconciliation.possible(result) : Reconciliation.notFound()
	//	}
	//
	//	private ResponseEntity<Reconciliation> doctorInception(String keywords, Type... businessTypes){
	//		Type businessType = businessTypes[0]
	//		Business result = service.queryPhysician(keywords)
	//		if(result){
	//			return Reconciliation.accurate(result)
	//		}else if(businessTypes){
	//			String name = !keywords ?:  StringUtils.substring(keywords, 0, keywords.indexOf(','))
	//			businessTypes.each { type ->
	//				String confidentKeywords = StringUtils.replaceFirst(keywords, name, type.toString())
	//				result = service.queryPhysician(confidentKeywords)
	//				if(result){
	//					return
	//				}
	//			}
	//			if(result){
	//				return Reconciliation.confident(result)
	//			}
	//		}
	//		String possibleKeywords = StringUtils.substring(keywords, keywords.indexOf(',')+1)
	//		result = service.queryPhysician( possibleKeywords)
	//		return result ? Reconciliation.possible(result) : Reconciliation.notFound()
	//	}
	//
	//	@GetMapping('/business')
	//	@ResponseBody
	//	public ResponseEntity<Reconciliation> business(@RequestParam('q') String keywords){
	//		Business result = service.queryBusiness(keywords)
	//		return result ? Reconciliation.accurate(result) : Reconciliation.notFound()
	//	}
	//
	//	@GetMapping('/businessRaw')
	//	@ResponseBody
	//	public ResponseEntity<String> businessRaw(@RequestParam('q') String keywords){
	//		String result = service.queryPlace(keywords)
	//		return StringUtils.isNotBlank(result) ? Reconciliation.accurate(result) : Reconciliation.notFound()
	//	}
	//
	//	@GetMapping('/hospital')
	//	@ResponseBody
	//	public ResponseEntity<Reconciliation> hospital(@RequestParam('q') String keywords){
	//
	//		return businessInception(keywords, Type.hospital, Type.health);
	//	}
	//
	//	@GetMapping('/practice')
	//	@ResponseBody
	//	public ResponseEntity<Reconciliation> practice(@RequestParam('q') String keywords){
	//
	//		return businessInception(keywords, Type.hospital, Type.health);
	//	}
	//
	//	@GetMapping('/physician')
	//	@ResponseBody
	//	public ResponseEntity<Reconciliation> physician(@RequestParam('q') String keywords){
	//		return doctorInception(keywords, Type.doctor);
	//	}
}




