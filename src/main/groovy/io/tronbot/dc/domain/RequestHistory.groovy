package io.tronbot.dc.domain

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

import com.jayway.jsonpath.JsonPath

/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Entity
@Access(AccessType.FIELD)
@Table(name='dc_request_history', indexes = [@Index(name = 'IDX_REQHIS', columnList = 'request')])
public class RequestHistory{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id
	@Column(length = 1024, nullable = false)
	String request
	@Column(columnDefinition = 'text', nullable = false)
	String response
	@Column(nullable = false)
	Date timestamp

	public RequestHistory(){
		//why JPA!!!
	}
	public RequestHistory(String request, String response) {
		super()
		this.request = request
		this.response = response
		this.timestamp = new Date()
	}
	//	@Enumerated(EnumType.STRING)
	//	Status status = Status.UNKNOWN
	//	enum Status{
	//		OK, UNKNOWN, OVER_QUERY_LIMIT, ZERO_RESULTS
	//	}
}
