package io.tronbot.dc.domain

import javax.persistence.Entity
import javax.persistence.Transient

import io.tronbot.dc.domain.Place.Type

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 23, 2017
 */
@Entity
class Hospital extends Place{
	@Transient
	final Type type = Type.hospital

	public Hospital(){
		super()
	}
}
