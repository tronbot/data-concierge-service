package io.tronbot.dc.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import io.tronbot.dc.domain.Business




/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
interface BusinessRepository extends JpaRepository<Business, Long>{

	@Query('SELECT biz FROM Business biz WHERE biz.placeId = :placeId ORDER BY biz.timestamp DESC')
	Business findByPlaceId(@Param('placeId') final String placeId);
}
