package io.tronbot.dc.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import io.tronbot.dc.domain.Place




/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
interface PlaceRepository extends JpaRepository<Place, Long>{
	List<Place> findByPlaceId(final String placeId);
}
