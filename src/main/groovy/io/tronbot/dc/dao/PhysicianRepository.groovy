package io.tronbot.dc.dao

import org.springframework.data.jpa.repository.JpaRepository

import io.tronbot.dc.domain.Physician




/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
interface PhysicianRepository extends JpaRepository<Physician, Long>{
	//	@Query('SELECT physician FROM Physician physician, Place place WHERE physician.id = place.id and place.placeId = :placeId')
	//	List<Physician> findByPlaceId(@Param('placeId') final String placeId);

	List<Physician> findByPlaceId(final String placeId);
}
