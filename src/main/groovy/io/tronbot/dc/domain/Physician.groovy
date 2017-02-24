package io.tronbot.dc.domain

import javax.persistence.Entity
import javax.persistence.Transient

import io.tronbot.dc.common.json.ArrayToSingleInterpreter
import io.tronbot.dc.common.json.JsonPathElement
import io.tronbot.dc.domain.Place.Type

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 23, 2017
 */
@Entity
class Physician extends Place{
	@JsonPathElement('$.number')
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
	@Transient
	final Type type = Type.doctor
}
