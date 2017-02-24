package io.tronbot.dc.messaging

import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.scheduling.annotation.Async

import io.tronbot.dc.domain.Hospital
import io.tronbot.dc.domain.Physician
import io.tronbot.dc.domain.Place
import io.tronbot.dc.domain.RequestHistory

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 16, 2017
 */
@MessagingGateway
interface Emitter{
	public static final String saveOrUpdateRequestHistory = 'saveOrUpdateRequestHistory'
	public static final String saveOrUpdatePlace = 'saveOrUpdatePlace'
	public static final String saveOrUpdateHospital = 'saveOrUpdateHospital'
	public static final String saveOrUpdatePhysician = 'saveOrUpdatePhysician'

	@Async
	@Gateway(requestChannel = saveOrUpdateRequestHistory)
	RequestHistory saveOrUpdateRequestHistory(RequestHistory requestHistory)

	@Async
	@Gateway(requestChannel = saveOrUpdatePlace)
	Place saveOrUpdatePlace(Place place)

	@Async
	@Gateway(requestChannel = saveOrUpdateHospital)
	Hospital saveOrUpdateHospital(Hospital hospital)
	
	@Async
	@Gateway(requestChannel = saveOrUpdatePhysician)
	Physician saveOrUpdatePhysician(Physician physician)
}

