package io.tronbot.dc.dao

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

import io.tronbot.dc.domain.Physician




/**
 * @author <a href='mailto:juanyong.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
interface PhysicianRepository extends JpaRepository<Physician, Long>{
	//	@Query('SELECT physician FROM Physician physician, Place place WHERE physician.id = place.id and place.placeId = :placeId')
	//	List<Physician> findByPlaceId(@Param('placeId') final String placeId);

	List<Physician> findByPlaceId(final String placeId);

	/**
	 * Using Physician info and lat/lon to identify the record 
	 * @param npi
	 * @param firstName
	 * @param lastName
	 * @param latitude
	 * @param longitude
	 */
	@Modifying
	@Query('SELECT physician FROM Physician physician WHERE physician.npi = :npi and physician.firstName = :firstName and physician.lastName = :lastName and physician.place.latitude = :latitude and physician.place.longitude = :longitude')
	List<Physician> findUnique(@Param('npi') final Integer npi, @Param('firstName') final String firstName, @Param('lastName') final String lastName, @Param('latitude') final Double latitude, @Param('longitude') final Double longitude);
}
