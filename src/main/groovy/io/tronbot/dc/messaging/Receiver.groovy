package io.tronbot.dc.messaging

import javax.transaction.Transactional

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
@Transactional
class Receiver implements Emitter{
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
		log.debug "Saving RequestHistory: ${requestHistory.getRequest()}"
		RequestHistory r = requestHistoryRepository.findOneByRequest(requestHistory.getRequest())
		if(r){
			//Update request history
			requestHistory.setId(r.getId())
			BeanUtils.copyProperties(r, requestHistory)
			return requestHistoryRepository.save(r)
		}else{
			//Save new request history
			return requestHistoryRepository.save(requestHistory)
		}
	}

	@ServiceActivator(inputChannel=Emitter.saveOrUpdatePlace)
	public Place saveOrUpdatePlace(Place place) {
		log.debug "Saving Place: ${ToStringBuilder.reflectionToString(place)}"
		Place p = placeRepository.findOneByPlaceId(place.getPlaceId())
		if(p){
			//Update place
			place.setId(p.getId())
			BeanUtils.copyProperties(p, place)
			return placeRepository.save(p)
		}else{
			//Save new place
			return placeRepository.save(place)
		}
	}

	@ServiceActivator(inputChannel=Emitter.saveOrUpdateHospital)
	public Hospital saveOrUpdateHospital(Hospital hospital) {
		log.debug "Saving Hospital: ${ToStringBuilder.reflectionToString(hospital)}"
		Hospital h = hospitalRepository.findOneByPlaceId(hospital.getPlaceId())
		if(h){
			//Update Hospital
			hospital.setId(h.getId())
			BeanUtils.copyProperties(h, hospital)
			return hospitalRepository.save(h)
		}else{
			//Save new Hospital
			return hospitalRepository.save(hospital)
		}
	}
}
