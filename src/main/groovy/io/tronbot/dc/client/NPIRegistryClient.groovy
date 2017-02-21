package io.tronbot.dc.client

import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.http.MediaType
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

import io.tronbot.dc.domain.Business

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 17, 2017
 */
@FeignClient(name = 'NPIRegistry', url = '${npiregistry.api.url}')
public interface NPIRegistryClient {
	@Cacheable(value = 'persistableCache', keyGenerator = 'feignCacheKeyGenerator')
	@GetMapping(value = 'api?number={number}&enumeration_type={enumeration_type}&taxonomy_description={taxonomy_description}&first_name={first_name}&last_name={last_name}&organization_name={organization_name}&address_purpose={address_purpose}&city={city}&state={state}&postal_code={postal_code}&country_code={country_code}&limit={limit}&skip={skip}&pretty={pretty}'
	, produces = MediaType.APPLICATION_JSON_VALUE)
	String api(@PathVariable('number') String number,
			@PathVariable('enumeration_type') String enumerationType,
			@PathVariable('taxonomy_description') String taxonomyDescription,
			@PathVariable('first_name') String firstName,
			@PathVariable('last_name') String lastName,
			@PathVariable('organization_name') String organizationName,
			@PathVariable('address_purpose') String addressPurpose,
			@PathVariable('city') String city,
			@PathVariable('state') String state,
			@PathVariable('postal_code') String postalCode,
			@PathVariable('country_code') String countryCode,
			@PathVariable('limit') Integer limit,
			@PathVariable('skip') Integer skip,
			@PathVariable('pretty') Boolean pretty
	)
}

public class NPIQuery{
	String number
	String enumerationType = 'NPI-1'
	String taxonomyDescription
	String firstName
	String lastName
	String organizationName
	String addressPurpose
	String city
	String state
	String postalCode
	String countryCode = 'US'
	Integer limit = 20
	Integer skip
	Boolean pretty = false

	public static NPIQuery fromBusiness(Business business){
		NPIQuery q = new NPIQuery()
		//		q.organizationName = business.getName()
		q.city = business.getCity()
		q.state = business.getState()
		q.postalCode = business.getPostalCode()
		return q
	}
}

@Repository
public class NPIRegistryClientHelper{
	private final NPIRegistryClient npiRegistryClient

	NPIRegistryClientHelper(NPIRegistryClient npiRegistryClient){
		this.npiRegistryClient = npiRegistryClient
	}

	String api(NPIQuery query){
		npiRegistryClient.api(	query.getNumber(),
				query.getEnumerationType(),
				query.getTaxonomyDescription(),
				query.getFirstName(),
				query.getLastName(),
				query.getOrganizationName(),
				query.getAddressPurpose(),
				query.getCity(),
				query.getState(),
				query.getPostalCode(),
				query.getCountryCode(),
				query.getLimit(),
				query.getSkip(),
				query.getPretty())
	}
}