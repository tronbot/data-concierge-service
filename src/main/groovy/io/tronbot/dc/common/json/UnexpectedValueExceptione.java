package io.tronbot.dc.common.json;

import com.jayway.jsonpath.JsonPathException;

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a>
 * @date Feb 27, 2017
 */
public class UnexpectedValueExceptione extends JsonPathException {
	private static final long serialVersionUID = -6515399539253963565L;

	public UnexpectedValueExceptione(final Class<?> expectedType, final Class<?> actualType) {
		super(String.format("Expected JSON value of [%s], while getting [%s]!", expectedType.getName(),
				null != actualType ? actualType.getName() : "Unknown Class"));
	}

}
