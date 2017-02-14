package io.tronbot.json

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 13, 2017
 */
class ArrayToStringDeserializer implements Deserializer<String> {
	String transfer(Object raw){
		if(raw instanceof List){
			raw.join(', ')
		}
		return raw
	}
}
