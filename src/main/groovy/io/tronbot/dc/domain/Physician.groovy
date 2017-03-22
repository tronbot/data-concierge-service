package io.tronbot.dc.domain

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table

import com.fasterxml.jackson.annotation.JsonIgnore

import io.tronbot.dc.common.json.ArrayToSingleInterpreter
import io.tronbot.dc.common.json.IntegerInterpreter
import io.tronbot.dc.common.json.JsonPathElement
import io.tronbot.dc.common.json.PostalCodeInterpreter
import io.tronbot.dc.domain.Place.Type

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 23, 2017
 */
@Entity
@Table(name='dc_physician')
class Physician extends Answer{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	Long id

	@JsonPathElement(value='$.number', interpreter=IntegerInterpreter.class)
	Integer npi

	@JsonPathElement('$.basic.first_name')
	String firstName

	@JsonPathElement('$.basic.last_name')
	String lastName

	@JsonPathElement('$.basic.name_prefix')
	String namePrefix

	@JsonPathElement('$.basic.name')
	String orgName

	@JsonPathElement('$.basic.gender')
	String gender

	@JsonPathElement('$.basic.credential')
	String credential

	@JsonPathElement('$.basic.status')
	String status

	@JsonPathElement(value='$.addresses[?(@.address_purpose == "LOCATION")]', interpreter=ArrayToSingleInterpreter.class)
	@ManyToOne(cascade = CascadeType.ALL)
	Address address

	@JsonPathElement(value='$.addresses[?(@.address_purpose == "MAILING")]', interpreter=ArrayToSingleInterpreter.class)
	@ManyToOne(cascade = CascadeType.ALL)
	Address mailingAddress

	@JsonPathElement(value='$.taxonomies[?(@.primary)]', interpreter=ArrayToSingleInterpreter.class)
	@OneToOne(cascade = CascadeType.ALL)
	License license

	@JsonPathElement(value='$.taxonomies[?(!@.primary)]')
	@OneToMany(cascade = CascadeType.ALL)
	List<License> otherLicenses

	@JsonPathElement(value='$.addresses[?(@.address_purpose == "LOCATION")].telephone_number', interpreter=ArrayToSingleInterpreter.class)
	String phoneNumber

	@JsonPathElement(value='$.addresses[?(@.address_purpose == "LOCATION")].fax_number', interpreter=ArrayToSingleInterpreter.class)
	String faxNumber

	@JsonPathElement(value='$.other_names')
	@OneToMany(cascade = CascadeType.ALL)
	List<PhysicianName> otherNames

	@OneToOne(cascade = CascadeType.PERSIST)
	Place place
	
	@JsonIgnore
	String keywords(){
		return "${orgName},${address?.address1},${address?.city},${address?.state}"
	}
	@JsonIgnore
	String getAddressString(){
		return "${address?.address1},${address?.city},${address?.state}"
	}
	@JsonIgnore
	Type[] getTypes(){
		return [Type.doctor, Type.dentist, Type.physiotherapist]
	}
}

@Entity
@Table(name='dc_physician_address')
class Address{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	Long id
	@JsonPathElement('$.address_1')
	String address1
	@JsonPathElement('$.address_2')
	String address2
	@JsonPathElement('$.city')
	String city
	@JsonPathElement('$.state')
	String state
	@JsonPathElement(value='$.postal_code', interpreter=PostalCodeInterpreter.class)
	String postalCode
	@JsonPathElement('$.country_code')
	String contryCode
	@JsonPathElement('$.country_name')
	String contry
}

@Entity
@Table(name='dc_physician_license')
class License{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	Long id
	@JsonPathElement('license')
	String license
	@JsonPathElement('code')
	String code
	@JsonPathElement('state')
	String state
	@JsonPathElement('desc')
	String speciality
}

@Entity
@Table(name='dc_physician_identifier')
class Identifier{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	Long id
	@JsonPathElement('code')
	String code
	@JsonPathElement('desc')
	String desc
	@JsonPathElement('identifier')
	String identifier
	@JsonPathElement('issuer')
	String issuer
	@JsonPathElement('state')
	String state
}

@Entity
@Table(name='dc_physician_name')
class PhysicianName{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	Long id
	@JsonPathElement('first_name')
	String firstName
	@JsonPathElement('last_name')
	String lastName
	@JsonPathElement('credential')
	String credential
	@JsonPathElement('code')
	String code
	@JsonPathElement('type')
	String type
}