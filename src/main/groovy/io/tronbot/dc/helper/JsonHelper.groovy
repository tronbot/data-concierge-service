package io.tronbot.dc.helper

import org.springframework.stereotype.Component

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jayway.jsonpath.JsonPathException
import com.jayway.jsonpath.Predicate

import groovy.util.logging.Log4j
import io.tronbot.dc.common.json.JsonPathReflector

/**
 * Convenient utility class for JSON
 * 
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 22, 2017
 */
@Component
@Log4j
class JsonHelper{
	private final Gson gson
	private final JsonPathReflector jsonPathReflector

	public JsonHelper(Gson gson, JsonPathReflector jsonPathReflector){
		super()
		this.gson = gson
		this.jsonPathReflector = jsonPathReflector
	}

	public String mapToString(Map<String, Object> jsonMap){
		return jsonMap ? gson.toJson(jsonMap, Map.class) : null
	}

	public Map<String, Object> stringToMap(String jsonString){
		return jsonString ? gson.fromJson(jsonString, new TypeToken<HashMap<String, Object>>(){}.getType()) : null
	}

	public <T> T from(String json, T obj, String... paths) {
		if(paths){
			return from(read(json, paths.join('.')), obj)
		}else{
			return jsonPathReflector.from(json, obj)
		}
	}

	public <T> T from(Map<String, Object> json, T obj, String... paths) {
		return from(mapToString(json), obj, paths)
	}

	public <T> T read(String json, String jsonPath, Predicate... filters) {
		T val = null
		try{
			val = jsonPathReflector.read(json, jsonPath, filters)
		}catch(JsonPathException e){
			log.info(e.getMessage())
		}
		return val
	}

	public <T> T read(Map<String, Object> json, String jsonPath, Predicate... filters) {
		return read(mapToString(json), jsonPath, filters)
	}
}