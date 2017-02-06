package io.tronbot.dc.web.rest

import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import groovy.util.logging.Log4j
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
		this.service=service
	}

	@GetMapping('/business')
	@ResponseBody
	public ResponseEntity business(@RequestParam('q') String keywords){
		def res = service.queryBusiness(keywords)
		return ResponseEntity.ok(res)
	}
}