package io.tronbot.dc.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Transient

import io.tronbot.dc.domain.Place.Type

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 23, 2017
 */
@Entity
class Hospital {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id
	@ManyToOne
	Place place
	@Transient
	final Type type = Type.hospital

	public Hospital(){
		super()
	}

	public Hospital(Place place){
		this.place = place
	}
}
