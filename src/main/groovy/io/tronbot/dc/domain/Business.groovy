package io.tronbot.dc.domain

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Entity
@Access(AccessType.FIELD)
@Table(indexes = [@Index(name = 'IDX_BIZ', columnList = 'place_id')])
public class Business{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id
	String place_id
	String name
	String website
	String googleMapURL
	String phone
	//Address
	String address
	
	Float latitude
	Float longitude
}