package io.tronbot.dc.dao

import org.springframework.data.jpa.repository.JpaRepository

import io.tronbot.dc.domain.Business


/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
interface BusinessRepository extends JpaRepository<Business, Long>{
	
}
