package io.tronbot.dc.cache

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation

import java.lang.reflect.Method

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.cache.interceptor.SimpleKeyGenerator
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.core.env.ConfigurablePropertyResolver
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.RequestMapping

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 9, 2017
 */
@Component
class FeignCacheKeyGenerator implements KeyGenerator{
	//	@Autowired
	//	private FeignClientFactoryBean feignClientFactory
	@Autowired
	private ConfigurablePropertyResolver propertyResolver


	/**
	 * CURRENT : GET - https://maps.googleapis.com/maps/api/place/textsearch/json?key=KEY&query=XYZ
	 * TODO : user CURL command line for : http://stackoverflow.com/questions/14978411/http-post-and-get-using-curl-in-linux
	 */
	@Override
	public Object generate(Object target, Method method, Object... params) {
		Class<?> targetType = target.getClass()
		//get url parts from feign client annotation on the class
		FeignClient clzFeign= findMergedAnnotation(targetType,
				FeignClient.class)
		RequestMapping clzReq = findMergedAnnotation(targetType,
				RequestMapping.class)
		//get url parts from request mapping annotation on the method
		RequestMapping mtdReq = findMergedAnnotation(method,
				RequestMapping.class)

		StringBuilder keyBuilder = new StringBuilder()
		!mtdReq?:keyBuilder.append(mtdReq.method().join(', '))
		!clzReq?:keyBuilder.append(clzReq.method().join(', '))
		keyBuilder.append(' - ')
		!clzFeign?:keyBuilder.append(clzFeign.url()+'/')
				.append(clzFeign.path()+'/')
		!clzReq?:keyBuilder.append(clzReq.value()[0]+'/')
		!mtdReq?:keyBuilder.append(mtdReq.value()[0])
		String key = propertyResolver.resolvePlaceholders(keyBuilder.toString())
		return key.replaceAll('(?<!(http:|https:))[//]+', '/') // remove duplicate slashes
				.replaceAll('\\{(.+)\\}', SimpleKeyGenerator.generateKey(params).toString()) // FIXME current only handle single parameter
	}

}
