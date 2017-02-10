package io.tronbot.dc.cache

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity

import com.fasterxml.jackson.databind.ObjectMapper
import com.hazelcast.nio.ObjectDataInput
import com.hazelcast.nio.ObjectDataOutput
import com.hazelcast.nio.serialization.StreamSerializer

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 10, 2017
 */
//@Component
class ResponseEntityStreamSerializer implements StreamSerializer<ResponseEntity<?>>{
	@Autowired
	private ObjectMapper mapper

	@Override
	public int getTypeId() {
		return 1;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void write(ObjectDataOutput dataOut, ResponseEntity<?> object) throws IOException {

		mapper.writeValue(dataOut, object)
	}

	@Override
	public ResponseEntity<?> read(ObjectDataInput dataIn) throws IOException {
		return mapper.readValue(dataIn, ResponseEntity.class)
	}


	public static void main(String[] args){
		String resp = '''
{
   "results" : [
      {
         "address_components" : [
            {
               "long_name" : "1100",
               "short_name" : "1100",
               "types" : [ "street_number" ]
            },
            {
               "long_name" : "North Wood Dale Road",
               "short_name" : "N Wood Dale Rd",
               "types" : [ "route" ]
            },
            {
               "long_name" : "Wood Dale",
               "short_name" : "Wood Dale",
               "types" : [ "locality", "political" ]
            },
            {
               "long_name" : "Addison Township",
               "short_name" : "Addison Township",
               "types" : [ "administrative_area_level_3", "political" ]
            },
            {
               "long_name" : "DuPage County",
               "short_name" : "Dupage County",
               "types" : [ "administrative_area_level_2", "political" ]
            },
            {
               "long_name" : "Illinois",
               "short_name" : "IL",
               "types" : [ "administrative_area_level_1", "political" ]
            },
            {
               "long_name" : "United States",
               "short_name" : "US",
               "types" : [ "country", "political" ]
            },
            {
               "long_name" : "60191",
               "short_name" : "60191",
               "types" : [ "postal_code" ]
            },
            {
               "long_name" : "1060",
               "short_name" : "1060",
               "types" : [ "postal_code_suffix" ]
            }
         ],
         "formatted_address" : "1100 N Wood Dale Rd, Wood Dale, IL 60191, USA",
         "geometry" : {
            "location" : {
               "lat" : 41.986542,
               "lng" : -87.98094549999999
            },
            "location_type" : "APPROXIMATE",
            "viewport" : {
               "northeast" : {
                  "lat" : 41.9878909802915,
                  "lng" : -87.97959651970849
               },
               "southwest" : {
                  "lat" : 41.9851930197085,
                  "lng" : -87.9822944802915
               }
            }
         },
         "place_id" : "ChIJq6hvwIWxD4gR4r9Yu1ycshc",
         "types" : [ "establishment", "point_of_interest" ]
      },
      {
         "address_components" : [
            {
               "long_name" : "Wood Dale Road",
               "short_name" : "Wood Dale Rd",
               "types" : [ "route" ]
            },
            {
               "long_name" : "Wood Dale",
               "short_name" : "Wood Dale",
               "types" : [ "locality", "political" ]
            },
            {
               "long_name" : "Addison Township",
               "short_name" : "Addison Township",
               "types" : [ "administrative_area_level_3", "political" ]
            },
            {
               "long_name" : "DuPage County",
               "short_name" : "Dupage County",
               "types" : [ "administrative_area_level_2", "political" ]
            },
            {
               "long_name" : "Illinois",
               "short_name" : "IL",
               "types" : [ "administrative_area_level_1", "political" ]
            },
            {
               "long_name" : "United States",
               "short_name" : "US",
               "types" : [ "country", "political" ]
            },
            {
               "long_name" : "60191",
               "short_name" : "60191",
               "types" : [ "postal_code" ]
            }
         ],
         "formatted_address" : "Wood Dale Rd, Wood Dale, IL 60191, USA",
         "geometry" : {
            "location" : {
               "lat" : 41.9789474,
               "lng" : -87.9797112
            },
            "location_type" : "APPROXIMATE",
            "viewport" : {
               "northeast" : {
                  "lat" : 41.9802963802915,
                  "lng" : -87.97836221970849
               },
               "southwest" : {
                  "lat" : 41.9775984197085,
                  "lng" : -87.98106018029151
               }
            }
         },
         "place_id" : "ChIJmfd-qSmyD4gRqOB7Lt6asnk",
         "types" : [ "establishment", "point_of_interest" ]
      }
   ],
   "status" : "OK"
}
'''


		ObjectMapper mapper = new ObjectMapper();
		ResponseEntity<String> respEntity = ResponseEntity.ok(resp);
		println respEntity;
		println mapper.writeValueAsString(respEntity);
		println mapper.readValue(mapper.writeValueAsString(respEntity), ResponseEntity.class)
	}
}
