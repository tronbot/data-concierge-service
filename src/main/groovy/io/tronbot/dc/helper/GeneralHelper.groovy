package io.tronbot.dc.helper

import java.lang.annotation.Annotation
import java.lang.reflect.AnnotatedElement

import org.springframework.core.annotation.AnnotatedElementUtils

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Mar 21, 2017
 */
class GeneralHelper {
	public static Annotation findMatchAnnotations(AnnotatedElement element, Class<Annotation>... annotationTypes) {
		Annotation a = null
		if(annotationTypes){
			for(Class<Annotation> aType : annotationTypes){
				a = AnnotatedElementUtils.findMergedAnnotation(element, aType)
				if(a){
					break
				}
			}
		}
		return a
	}

}
