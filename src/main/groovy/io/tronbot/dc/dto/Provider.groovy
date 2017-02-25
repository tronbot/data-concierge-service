package io.tronbot.dc.dto

import io.tronbot.dc.common.json.ArrayToSingleInterpreter
import io.tronbot.dc.common.json.IntegerInterpreter
import io.tronbot.dc.common.json.JsonPathElement
import io.tronbot.dc.domain.Place.Type

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 24, 2017
 */
class Provider {
	@JsonPathElement(value='$.number', interpreter=IntegerInterpreter.class)
	Integer npi
	@JsonPathElement('$.basic.first_name')
	String firstName
	@JsonPathElement('$.basic.last_name')
	String lastName
	@JsonPathElement('$.basic.name')
	String orgName
	@JsonPathElement('$.basic.gender')
	String gender
	@JsonPathElement(value='$.taxonomies[?(@.primary)].license', interpreter=ArrayToSingleInterpreter.class)
	String license
	@JsonPathElement(value='$.taxonomies[?(@.primary)].state', interpreter=ArrayToSingleInterpreter.class)
	String licenseState
	@JsonPathElement(value='$.taxonomies[?(@.primary)].desc', interpreter=ArrayToSingleInterpreter.class)
	String speciality
	@JsonPathElement(value='$.identifiers[0].identifier', interpreter=ArrayToSingleInterpreter.class)
	String identifier
	@JsonPathElement(value='$.identifiers[0].desc', interpreter=ArrayToSingleInterpreter.class)
	String identifierType
	@JsonPathElement(value='$.identifiers[0].state', interpreter=ArrayToSingleInterpreter.class)
	String identifierState
	@JsonPathElement(value='$.addresses[?(@.address_purpose == "LOCATION")].address_1', interpreter=ArrayToSingleInterpreter.class)
	String street
	@JsonPathElement(value='$.addresses[?(@.address_purpose == "LOCATION")].city', interpreter=ArrayToSingleInterpreter.class)
	String city
	@JsonPathElement(value='$.addresses[?(@.address_purpose == "LOCATION")].state', interpreter=ArrayToSingleInterpreter.class)
	String state
	@JsonPathElement(value='$.addresses[?(@.address_purpose == "LOCATION")].postal_code', interpreter=ArrayToSingleInterpreter.class)
	String postalCode
	final Type type = Type.doctor
}
