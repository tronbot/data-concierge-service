package io.tronbot.dc.domain

import javax.persistence.Entity

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

import io.tronbot.dc.common.json.JsonPathElement
import io.tronbot.dc.common.json.JsonPathReflector

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 17, 2017
 */

@Entity
@JsonPathElement('$.results[0]')
public class Physician  {
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
	@JsonPathElement('$.taxonomies')
	List<License> licenses
	@JsonPathElement('$.taxonomies')
	List<License> licenses2
	@JsonPathElement(value='$.taxonomies', required=true)
	License[] licenseArray
	@JsonPathElement(value='$.taxonomies')
	List<String> licenseStrs
	@JsonPathElement(value='$.taxonomies')
	List<?> licenseJsons
	@JsonPathElement('$.identifiers')
	List<ProductType> productTypes
	Business business
	@JsonPathElement('$.basic')
	Basic basicInfo






	public static void main(String[] args){
		URL url = new URL('https://npiregistry.cms.hhs.gov/api?number=&enumeration_type=NPI-1&taxonomy_description=&first_name=TONY&last_name=HWANG&organization_name=&address_purpose=&city=INDIO&state=CA&postal_code=92201&country_code=US&limit=20&skip=&pretty=')
		JsonPathReflector jr = new JsonPathReflector()
		Physician obj = jr.from(IOUtils.toString(url), new Physician())
		println ReflectionToStringBuilder.reflectionToString(obj);
	}
}

public class Basic{
	@JsonPathElement('$.status')
	String status
	@JsonPathElement('$.credential')
	String credential
	@JsonPathElement('$.first_name')
	String first_name
	@JsonPathElement('$.last_name')
	String last_name
	@JsonPathElement('$.last_updated')
	String last_updated
	@JsonPathElement('$.name')
	String name
}

@Entity
class License{
	@JsonPathElement('$.license')
	String license
	@JsonPathElement('$.state')
	String state
	@JsonPathElement('$.desc')
	String speciality
	@JsonPathElement('$.primary')
	Boolean primary
}

@Entity
class ProductType{
	String desc
	String id
	String state
}