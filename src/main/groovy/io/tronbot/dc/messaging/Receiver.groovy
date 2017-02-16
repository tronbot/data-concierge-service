package io.tronbot.dc.messaging

import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.integration.annotation.ServiceActivator

import groovy.util.logging.Log4j
import io.tronbot.dc.dao.BusinessRepository
import io.tronbot.dc.dao.RequestHistoryRepository
import io.tronbot.dc.domain.Business
import io.tronbot.dc.domain.RequestHistory

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 16, 2017
 */
@MessageEndpoint
@Log4j
class Receiver  {
	private final RequestHistoryRepository requestHistoryRepository
	private final BusinessRepository businessRepository

	public Receiver(RequestHistoryRepository requestHistoryRepository,BusinessRepository businessRepository) {
		this.requestHistoryRepository = requestHistoryRepository
		this.businessRepository = businessRepository
	}


	@ServiceActivator(inputChannel=Sink.INPUT)
	public void saveBusiness(Business business){
		log.debug "Saving Business: ${ToStringBuilder.reflectionToString(business)}"
		if(!businessRepository.findByPlaceId(business.getPlaceId())){
			businessRepository.save(business)
		}
			
		
	}

	@ServiceActivator(inputChannel=Sink.INPUT)
	public void saveReqHistory(RequestHistory requestHistory){
		log.debug "Saving RequestHistory: ${requestHistory.getRequest()}"
		requestHistoryRepository.save(requestHistory)
	}
}
