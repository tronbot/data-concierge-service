package io.tronbot.dc.cache

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation

import java.lang.annotation.Annotation
import java.lang.reflect.Method
import java.nio.charset.Charset

import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.core.env.ConfigurablePropertyResolver
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

import com.google.gson.Gson

import io.tronbot.dc.helper.GeneralHelper

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 9, 2017
 */
@Component
class FeignCacheKeyGenerator implements KeyGenerator{
	private final ConfigurablePropertyResolver propertyResolver
	private final Gson gson

	public FeignCacheKeyGenerator(ConfigurablePropertyResolver propertyResolver, Gson gson) {
		super()
		this.propertyResolver = propertyResolver
		this.gson = gson
	}

	/**
	 * CURRENT : GET - https://maps.googleapis.com/maps/api/place/textsearch/json?key=KEY&query=XYZ
	 * TODO : user CURL command line for : http://stackoverflow.com/questions/14978411/http-post-and-get-using-curl-in-linux
	 */
	@Override
	public Object generate(Object target, Method method, Object... params) {
		List<Object> paramLst = params as List
		Class<?> targetType = target.getClass()
		//get url parts from feign client annotation on the class
		FeignClient clzFeign = findMergedAnnotation(targetType,
				FeignClient.class)
		RequestMapping clzReq = findMergedAnnotation(targetType,
				RequestMapping.class)
		//get url parts from request mapping annotation on the method
		RequestMapping mtdReq = findMergedAnnotation(method,
				RequestMapping.class)
		//		curl "https://maps.googleapis.com/maps/api/geocode/json?key=${google.api.key}&address=211%20n%20prairie%20ave,inglewood,ca,"
		//curl -X PUT -d {xsad:adfa} localhost:8080
		String requestKey = null;
		RequestMethod reqMtd = (mtdReq?mtdReq.method():clzReq?.method())?.find()
		String requestMethod = ''
		if(reqMtd){
			if(!RequestMethod.GET.equals(reqMtd)){
				requestMethod = "-X ${reqMtd} "
			}
		}

		String reqURL = propertyResolver.resolvePlaceholders(clzFeign.url()+'/'+clzFeign.path()+'/')+mtdReq.value()?.find()
		method.getParameters().eachWithIndex  { param, idx ->
			Annotation ann = GeneralHelper.findMatchAnnotations(param, PathVariable.class, RequestParam.class)
			if(ann){
				reqURL = reqURL.replaceAll("\\{${ann.value()}\\}", paramLst[idx] ? URLEncoder.encode(paramLst[idx].toString(), Charset.defaultCharset().name()) : '')
			}
		}
		reqURL = reqURL.replaceAll('(?<!(http:|https:))[//]+', '/')

		String reqData = ''
		method.getParameters().eachWithIndex  { param, idx ->
			Annotation ann = GeneralHelper.findMatchAnnotations(param, RequestBody.class)
			if(ann && paramLst[idx]){
				reqData = "-d ${gson.toJson(paramLst[idx])} "
			}
		}

		requestKey = "curl ${requestMethod}${reqData}\"${reqURL}\""

		return requestKey;
	}


}
