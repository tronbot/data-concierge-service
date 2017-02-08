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
@Table(indexes = [@Index(name = 'REQURL_IDX', columnList = 'requestURL')])
public class RequestHistory{


	// id: operation|parameter
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id
	@Column(length = 1024, nullable = false)
	String requestURL
	@Column(columnDefinition = 'text', nullable = false)
	String response
	String status
	@Column(nullable = false)
	Date timestamp



	public RequestHistory(String requestURL, String response) {
		super()
		this.requestURL = requestURL
		this.response = response
		this.timestamp = new Date()
	}

}
