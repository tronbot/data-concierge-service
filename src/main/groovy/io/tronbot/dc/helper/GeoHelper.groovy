package io.tronbot.dc.helper

import io.tronbot.dc.domain.Place

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a>
 * @date Mar 2, 2017
 */
public class GeoHelper {
	
	public static final int MAX_DISTANCE = 20036000

	/**
	 * 
	 * @param p1
	 * @param p2
	 * @return METERS of distance
	 */
	public static Integer distance(Place p1, Place p2) {
		if(!p1 || !p2){
			return MAX_DISTANCE
		}
		return distance(p1.getLatitude(), p1.getLongitude(), p2.getLatitude(), p2.getLongitude())
	}

	/**
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return METERS of distance
	 */
	public static Integer distance(Double lat1, Double lng1, Double lat2, Double lng2) {
		if(!lat1 || !lat2 || !lng1 || !lng2 ){
			return MAX_DISTANCE
		}
		Integer r = 6371 // average radius of the earth in km
		Double dLat = Math.toRadians(lat2 - lat1)
		Double dLon = Math.toRadians(lng2 - lng1)
		Double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)
		Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
		Double d = r * c * 1000
		return (Integer) d
	}
}
