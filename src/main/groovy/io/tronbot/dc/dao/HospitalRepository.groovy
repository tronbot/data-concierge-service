package io.tronbot.dc.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import io.tronbot.dc.domain.Hospital




/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
interface HospitalRepository extends JpaRepository<Hospital, Long>{
	@Query('SELECT h FROM Hospital h JOIN h.place p WHERE p.placeId = :placeId')
	Hospital findOneByPlaceId(@Param('placeId') final String placeId);
}
