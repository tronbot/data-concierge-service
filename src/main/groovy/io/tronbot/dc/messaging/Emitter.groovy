package io.tronbot.dc.messaging

import org.springframework.cloud.stream.messaging.Source
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway

import io.tronbot.dc.domain.Business
import io.tronbot.dc.domain.RequestHistory

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 16, 2017
 */
@MessagingGateway
interface Emitter{
	@Gateway(requestChannel = Source.OUTPUT)
	void saveBusiness(Business business)

	@Gateway(requestChannel = Source.OUTPUT)
	void saveRequestHistory(RequestHistory requestHistory)
}
