package io.tronbot.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a>
 * @date Feb 13, 2017
 */
@Target({ ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonPathField {
	String value();

	Class<? extends Deserializer<?>> deserializer() default EmptyDeserializer.class;

	boolean required() default false;

	class EmptyDeserializer implements Deserializer<Object> {
		public Object transfer(Object raw) {
			return raw;
		}
	}
}
