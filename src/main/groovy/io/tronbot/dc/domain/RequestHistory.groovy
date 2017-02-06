package io.tronbot.dc.domain

import javax.persistence.Entity
import javax.persistence.Id

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Entity
public class RequestHistory{
	// id: operation|parameter
	@Id
	String id
	String response
	Date timestamp
}
