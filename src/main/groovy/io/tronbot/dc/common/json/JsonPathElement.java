package io.tronbot.dc.common.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a>
 * @date Feb 13, 2017
 */
@Target({ ElementType.ANNOTATION_TYPE,ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonPathElement {
	String value() default "$"; // default to be current path

	Class<? extends Interpreter<?>> interpreter() default EmptyInterpreter.class;

	boolean required() default false;

	class EmptyInterpreter implements Interpreter<Object> {
		public Object interpret(Object raw) {
			return raw;
		}
	}
}
