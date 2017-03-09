package io.tronbot.dc.common.json

import org.apache.commons.lang3.StringUtils

import groovy.util.logging.Log4j

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 13, 2017
 */
@Log4j
class PostalCodeInterpreter implements Interpreter<String> {
	String interpret(Object raw){
		if(raw){
			if(raw instanceof String){
				return StringUtils.substring(raw, 0, 5)
			}else{
				throw new UnexpectedValueExceptione(String.class, raw?.getClass())
			}
		}
		return raw
	}
}
