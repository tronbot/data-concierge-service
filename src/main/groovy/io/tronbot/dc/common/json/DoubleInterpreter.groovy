package io.tronbot.dc.common.json

import org.apache.commons.lang3.builder.ReflectionToStringBuilder

import groovy.util.logging.Log4j

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 13, 2017
 */
@Log4j
class DoubleInterpreter implements Interpreter<Double> {
	Double interpret(Object raw){
		return raw ? (Double)raw : null
	}
}
