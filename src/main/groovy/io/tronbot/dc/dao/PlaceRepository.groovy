package io.tronbot.dc.dao

import org.springframework.data.jpa.repository.JpaRepository

import io.tronbot.dc.domain.Place




/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
interface PlaceRepository extends JpaRepository<Place, Long>{
	//	@Query('SELECT p FROM Place p WHERE p.placeId = :placeId ORDER BY p.timestamp DESC')
	//	Place findOneByPlaceId(@Param('placeId') final String placeId);
	
	Place findOneByPlaceId(String placeId);

	//	@Modifying
	//	@Query('Update Place p SET p = newP WHERE p.placeId = newP.placeId')
	//	void updateByPlaceId(@Param('newP') Place newPlace)
}
