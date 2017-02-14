package io.tronbot.json

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 13, 2017
 */
class ArrayToSingleDeserializer implements Deserializer<String> {
	String transfer(Object raw){
		if(raw instanceof List){
			if(raw){
				return raw.first().toString()
			}else{
				return null;
			}
		}
		return raw
	}
}
