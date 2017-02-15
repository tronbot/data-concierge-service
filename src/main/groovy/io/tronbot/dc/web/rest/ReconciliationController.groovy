package io.tronbot.dc.web.rest

import org.apache.commons.lang3.StringUtils
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import groovy.util.logging.Log4j
import io.tronbot.dc.common.test.json.Business
import io.tronbot.dc.dto.Reconciliation
import io.tronbot.dc.service.ReconciliationService

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
		this.service = service
	}

	@GetMapping('/business')
	@ResponseBody
	public ResponseEntity<Reconciliation> business(@RequestParam('q') String keywords){
		Business result = service.queryBusiness(keywords)
		return result ? Reconciliation.ok(result) : Reconciliation.notFound()
	}

	@GetMapping('/businessRaw')
	@ResponseBody
	public ResponseEntity<String> businessRaw(@RequestParam('q') String keywords){
		String result = service.query(keywords)
		return StringUtils.isNotBlank(result) ? Reconciliation.ok(result) : Reconciliation.notFound()
	}
}




