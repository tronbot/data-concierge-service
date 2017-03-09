package io.tronbot.dc.common.json

import org.apache.commons.lang3.builder.ReflectionToStringBuilder

import groovy.util.logging.Log4j

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 21, 2017
 */
@Log4j
class ObjectToStringInterpreter implements Interpreter<String> {
	String interpret(Object raw){
		return raw?.toString()
	}
}
