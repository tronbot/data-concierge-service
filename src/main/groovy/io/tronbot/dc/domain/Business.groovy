package io.tronbot.dc.domain

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

import io.tronbot.dc.common.json.ArrayToSingleInterpreter
import io.tronbot.dc.common.json.ArrayToStringInterpreter
import io.tronbot.dc.common.json.JsonPathElement
import io.tronbot.dc.domain.Business.Type


/**
 * @author <a href='mailto:juanyo
 import com.jayway.jsonpath.JsonPathng.zhang@gmail.com'>Juanyong Zhang</a> 
 * @date Feb 6, 2017
 */
@Entity
@Access(AccessType.FIELD)
@Table(indexes = [@Index(name = 'IDX_BIZ', columnList = 'placeId')])
public class Business{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id
	//	@JsonProperty('status')
	@JsonPathElement('$.result.place_id')
	String placeId
	@JsonPathElement('$.result.name')
	String name
	@JsonPathElement('$.result.website')
	String website
	@JsonPathElement('$.result.url')
	String googleMapURL
	@JsonPathElement('$.result.formatted_phone_number')
	String phone
	@JsonPathElement('$.result.formatted_address')
	String address
	@JsonPathElement(value = '$.result.address_components[?(\'street_number\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String streetNumber
	@JsonPathElement(value = '$.result.address_components[?(\'route\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String streetName
	@JsonPathElement(value = '$.result.address_components[?(\'locality\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String city
	@JsonPathElement(value = '$.result.address_components[?(\'administrative_area_level_2\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String county
	@JsonPathElement(value = '$.result.address_components[?(\'administrative_area_level_1\' in @.types )].short_name', interpreter=ArrayToSingleInterpreter.class)
	String state
	@JsonPathElement(value = '$.result.address_components[?(\'postal_code\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String postalCode
	@JsonPathElement(value = '$.result.address_components[?(\'postal_code_suffix\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String postalCodeSuffix
	@JsonPathElement(value = '$.result.address_components[?(\'country\' in @.types )].short_name', interpreter=ArrayToSingleInterpreter.class)
	String country
	@JsonPathElement(value = '$.result.types', interpreter=ArrayToStringInterpreter.class)
	String types
	@JsonPathElement('$.result.geometry.location.lat')
	Double latitude
	@JsonPathElement('$.result.geometry.location.lng')
	Double longitude
	Date timestamp = new Date()
	@Enumerated(EnumType.STRING)
	Type type

	enum Type{
		accounting,
		administrative_area_level_1,
		administrative_area_level_2,
		administrative_area_level_3,
		administrative_area_level_4,
		administrative_area_level_5,
		airport,
		amusement_park,
		aquarium,
		art_gallery,
		atm,
		bakery,
		bank,
		bar,
		beauty_salon,
		bicycle_store,
		book_store,
		bowling_alley,
		bus_station,
		cafe,
		campground,
		car_dealer,
		car_rental,
		car_repair,
		car_wash,
		casino,
		cemetery,
		church,
		city_hall,
		clothing_store,
		colloquial_area,
		convenience_store,
		country,
		courthouse,
		dentist,
		department_store,
		doctor,
		electrician,
		electronics_store,
		embassy,
		establishment,
		finance,
		fire_station,
		floor,
		florist,
		food,
		funeral_home,
		furniture_store,
		gas_station,
		general_contractor,
		geocode,
		grocery_or_supermarket,
		gym,
		hair_care,
		hardware_store,
		health,
		hindu_temple,
		home_goods_store,
		hospital,
		insurance_agency,
		intersection,
		jewelry_store,
		laundry,
		lawyer,
		library,
		liquor_store,
		local_government_office,
		locality,
		locksmith,
		lodging,
		meal_delivery,
		meal_takeaway,
		mosque,
		movie_rental,
		movie_theater,
		moving_company,
		museum,
		natural_feature,
		neighborhood,
		night_club,
		painter,
		park,
		parking,
		pet_store,
		pharmacy,
		physiotherapist,
		place_of_worship,
		plumber,
		point_of_interest,
		police,
		political,
		post_box,
		post_office,
		postal_code,
		postal_code_prefix,
		postal_code_suffix,
		postal_town,
		premise,
		real_estate_agency,
		restaurant,
		roofing_contractor,
		room,
		route,
		rv_park,
		school,
		shoe_store,
		shopping_mall,
		spa,
		stadium,
		storage,
		store,
		street_address,
		street_number,
		sublocality,
		sublocality_level_1,
		sublocality_level_2,
		sublocality_level_3,
		sublocality_level_4,
		sublocality_level_5,
		subpremise,
		subway_station,
		synagogue,
		taxi_stand,
		train_station,
		transit_station,
		travel_agency,
		university,
		veterinary_care,
		zoo

		public static Type valueOfIgnoreCase(String key){
			if(key){
				return Type.values().findResult  {
					it.name().equalsIgnoreCase(key) ? it : null
				}
			}
			return null
		}
		
		public String toString(){
			return this.name()
		}
	}
}



