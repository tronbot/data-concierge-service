package io.tronbot.dc.messaging

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.cloud.stream.messaging.Sink
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.integration.annotation.ServiceActivator

import groovy.util.logging.Log4j
import io.tronbot.dc.dao.PlaceRepository
import io.tronbot.dc.dao.RequestHistoryRepository
import io.tronbot.dc.domain.Place
import io.tronbot.dc.domain.RequestHistory

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 16, 2017
 */
@MessageEndpoint
@Log4j
class Receiver  {
	private final RequestHistoryRepository requestHistoryRepository
	private final PlaceRepository placeRepository


	public Receiver(RequestHistoryRepository requestHistoryRepository, PlaceRepository placeRepository ) {
		this.requestHistoryRepository = requestHistoryRepository
		this.placeRepository = placeRepository
	}


	@ServiceActivator(inputChannel=Sink.INPUT)
	public void saveRequestHistory(RequestHistory requestHistory) {
		log.debug "Saving RequestHistory: ${requestHistory.getRequest()}"
		requestHistoryRepository.save(requestHistory)
	}

	@ServiceActivator(inputChannel=Sink.INPUT)
	public void saveOrUpdatePlace(Place place) {
		log.debug "Saving Place: ${ToStringBuilder.reflectionToString(place)}"
		Place p = placeRepository.findOneByPlaceId(place.getPlaceId())
		if(p){
			Long id = p.getId()
			BeanUtils.copyProperties(p, place)
			p.setId(id)
			placeRepository.save(p)
		}else{
			placeRepository.save(place)
		}
	}
}
