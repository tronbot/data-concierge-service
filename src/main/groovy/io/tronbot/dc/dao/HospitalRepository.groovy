package io.tronbot.dc.dao

import org.springframework.data.jpa.repository.JpaRepository

import io.tronbot.dc.domain.Hospital




/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
interface HospitalRepository extends JpaRepository<Hospital, Long>{
	List<Hospital> findByPlaceId(final String placeId);
}
