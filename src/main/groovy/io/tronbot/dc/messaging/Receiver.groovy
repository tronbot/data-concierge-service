package io.tronbot.dc.messaging

import org.apache.commons.beanutils.BeanUtils
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.integration.annotation.ServiceActivator

import groovy.util.logging.Log4j
import io.tronbot.dc.dao.HospitalRepository
import io.tronbot.dc.dao.PlaceRepository
import io.tronbot.dc.dao.RequestHistoryRepository
import io.tronbot.dc.domain.Hospital
import io.tronbot.dc.domain.Place
import io.tronbot.dc.domain.RequestHistory

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 16, 2017
 */
@MessageEndpoint
@Log4j
class Receiver implements  Emitter{
	private final RequestHistoryRepository requestHistoryRepository
	private final PlaceRepository placeRepository
	private final HospitalRepository hospitalRepository


	public Receiver(RequestHistoryRepository requestHistoryRepository, PlaceRepository placeRepository, HospitalRepository hospitalRepository) {
		this.requestHistoryRepository = requestHistoryRepository
		this.placeRepository = placeRepository
		this.hospitalRepository = hospitalRepository
	}

	@ServiceActivator(inputChannel=Emitter.saveOrUpdateRequestHistory)
	public RequestHistory saveOrUpdateRequestHistory(RequestHistory requestHistory) {
		RequestHistory exists = requestHistoryRepository.findOneByRequest(requestHistory.getRequest())
		if(!exists){
			log.debug "Saving RequestHistory: ${requestHistory.getRequest()}"
			requestHistoryRepository.save(requestHistory)
		}
	}

	@ServiceActivator(inputChannel=Emitter.saveOrUpdatePlace)
	public Place saveOrUpdatePlace(Place place) {
		log.debug "Saving Place: ${ToStringBuilder.reflectionToString(place)}"
		Place p = placeRepository.findOneByPlaceId(place.getPlaceId())
		if(p){
			Long id = p.getId()
			BeanUtils.copyProperties(p, place)
			p.setId(id)
			return placeRepository.save(p)
		}else{
			return placeRepository.save(place)
		}
	}

	@ServiceActivator(inputChannel=Emitter.saveOrUpdateHospital)
	public Hospital saveOrUpdateHospital(Hospital hospital) {
		log.debug "Saving Hospital: ${ToStringBuilder.reflectionToString(hospital)}"
		Hospital h = hospitalRepository.findOneByPlaceId(hospital.getPlaceId())
		if(h){
			return h
		}else{
			return hospitalRepository.save(hospital)
		}
	}
}
