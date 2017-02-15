package io.tronbot.dc.domain

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

import io.tronbot.dc.common.json.ArrayToSingleInterpreter
import io.tronbot.dc.common.json.ArrayToStringInterpreter
import io.tronbot.dc.common.json.JsonPathField


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
	@JsonPathField('$.result.place_id')
	String placeId
	@JsonPathField('$.result.name')
	String name
	@JsonPathField('$.result.website')
	String website
	@JsonPathField('$.result.url')
	String googleMapURL
	@JsonPathField('$.result.formatted_phone_number')
	String phone
	@JsonPathField('$.result.formatted_address')
	String address
	@JsonPathField(value = '$.result.address_components[?(\'street_number\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String streetNumber
	@JsonPathField(value = '$.result.address_components[?(\'route\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String streetName
	@JsonPathField(value = '$.result.address_components[?(\'locality\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String city
	@JsonPathField(value = '$.result.address_components[?(\'administrative_area_level_2\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String county
	@JsonPathField(value = '$.result.address_components[?(\'administrative_area_level_1\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String state
	@JsonPathField(value = '$.result.address_components[?(\'postal_code\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String postalCode
	@JsonPathField(value = '$.result.address_components[?(\'postal_code_suffix\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String postalCodeSuffix
	@JsonPathField(value = '$.result.address_components[?(\'country\' in @.types )].long_name', interpreter=ArrayToSingleInterpreter.class)
	String country
	@JsonPathField(value = '$.result.types', interpreter=ArrayToStringInterpreter.class)
	String types
	@JsonPathField('$.result.geometry.location.lat')
	Double latitude
	@JsonPathField('$.result.geometry.location.lng')
	Double longitude
	Date timestamp = new Date()
}
