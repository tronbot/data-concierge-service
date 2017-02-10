package io.tronbot.dc.messaging

import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.integration.annotation.ServiceActivator

import io.tronbot.dc.dao.RequestHistoryRepository
import io.tronbot.dc.domain.RequestHistory

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 9, 2017
 */
@MessageEndpoint
@EnableBinding(Processor.class)
class RequestHistoryProcessor{

	private final RequestHistoryRepository requestHistoryRepository

	public RequestHistoryProcessor(RequestHistoryRepository requestHistoryRepository) {
		this.requestHistoryRepository = requestHistoryRepository
	}
	@ServiceActivator(inputChannel=Sink.INPUT)
	public void onNewRequestHistory(RequestHistory requestHistory){
		this.requestHistoryRepository.save(requestHistory)
	}
}

