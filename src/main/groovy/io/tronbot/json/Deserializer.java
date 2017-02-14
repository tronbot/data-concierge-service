package io.tronbot.json;
/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a> 
 * @date Feb 13, 2017
 */
public interface Deserializer<T> {
	T transfer(Object raw);
}
