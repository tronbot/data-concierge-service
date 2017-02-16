package io.tronbot.dc.web.rest

import org.apache.commons.lang3.StringUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import groovy.util.logging.Log4j
import io.tronbot.dc.domain.Business
import io.tronbot.dc.domain.Business.Type
import io.tronbot.dc.dto.Reconciliation
import io.tronbot.dc.service.ReconciliationService

/**
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

	private ResponseEntity<Reconciliation> businessInception(String type, String keywords){
		Type businessType = Type.valueOfSynonym(type)
		Business result = service.queryBusiness(businessType, keywords)
		if(result){
			return Reconciliation.accurate(result)
		}else if(businessType){
			String name = !keywords ?:  StringUtils.substring(keywords, 0, keywords.indexOf(','))
			result = service.queryBusiness(businessType, StringUtils.replaceFirst(keywords, name, Type.valueOfSynonym(type)?.toString()))
			if(result){
				return Reconciliation.confident(result)
			}
		}else{
			result = service.queryBusiness(businessType, StringUtils.substring(keywords, keywords.indexOf(',')+1))
			if(result){
				return Reconciliation.possible(result)
			}
		}
		return Reconciliation.notFound()
	}

	@GetMapping('/hospital')
	@ResponseBody
	public ResponseEntity<Reconciliation> hospital(@RequestParam('q') String keywords){

		return businessInception('hospital', keywords);
	}
	
	@GetMapping('/doctor')
	@ResponseBody
	public ResponseEntity<Reconciliation> doctor(@RequestParam('q') String keywords){

		return businessInception('doctor', keywords);
	}


	@GetMapping('/business')
	@ResponseBody
	public ResponseEntity<Reconciliation> business(@RequestParam('q') String keywords){
		Business result = service.queryBusiness(keywords)
		return result ? Reconciliation.accurate(result) : Reconciliation.notFound()
	}

	@GetMapping('/businessRaw')
	@ResponseBody
	public ResponseEntity<String> businessRaw(@RequestParam('q') String keywords){
		String result = service.query(keywords)
		return StringUtils.isNotBlank(result) ? Reconciliation.accurate(result) : Reconciliation.notFound()
	}
}




