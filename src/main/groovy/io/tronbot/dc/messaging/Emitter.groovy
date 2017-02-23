package io.tronbot.dc.messaging

import org.springframework.cloud.stream.messaging.Source
import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway

import io.tronbot.dc.domain.Hospital
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

	@Gateway(requestChannel = saveOrUpdateRequestHistory)
	RequestHistory saveOrUpdateRequestHistory(RequestHistory requestHistory)

	@Gateway(requestChannel = saveOrUpdatePlace)
	Place saveOrUpdatePlace(Place place)

	@Gateway(requestChannel = saveOrUpdateHospital)
	Hospital saveOrUpdateHospital(Hospital hospital)
}
