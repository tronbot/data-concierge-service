package io.tronbot.dc.common.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.spi.json.JsonProvider;

import io.tronbot.dc.common.json.JsonPathElement.EmptyInterpreter;

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a>
 * @date Feb 13, 2017
 */
public class JsonPathReflector {
	private static final Logger logger = LoggerFactory.getLogger(JsonPathReflector.class);

	private final Configuration configuration;

	private final JsonProvider jsonProvider;

	public JsonPathReflector() {
		this(Configuration.defaultConfiguration());
	}

	public JsonPathReflector(Configuration configuration) {
		super();
		this.configuration = configuration;
		this.jsonProvider = this.configuration.jsonProvider();
	}

	public static <T> T read(String json, String jsonPath, Predicate... filters) {
		return JsonPath.read(json, jsonPath, filters);
	}

	public <T> T from(String json, T obj) {
		return eval(JsonPath.parse(json, configuration), obj);
	}

	/**
	 * Evaluate from Object class level
	 * 
	 * @param doc
	 * @param obj
	 * @return
	 */
	private <T> T eval(DocumentContext doc, T obj) {
		// handle the class level JsonPathElement
		JsonPathElement elmt = obj.getClass().getAnnotation(JsonPathElement.class);
		try {
			if (null != elmt) {
				evalFields(readValueAsDoc(doc, elmt), obj);
			} else {
				evalFields(doc, obj);
			}
		} catch (ReflectiveOperationException e) {
			logger.debug(String.format("Json path object %s:[%s] can't not be evaluated",
					obj, elmt));
			throw new RuntimeException(e);
		}
		return obj;
	}

	/**
	 * Evaluate the fields within given object
	 * 
	 * @param doc
	 * @param obj
	 * @return
	 */
	private <T> T evalFields(DocumentContext doc, T obj) {
		FieldUtils.getAllFieldsList(obj.getClass()).stream()
				.filter(f -> null != f.getAnnotation(JsonPathElement.class))
				.forEach(f -> {
					JsonPathElement elmt = f.getAnnotation(JsonPathElement.class);
					String errorMsg = String.format(String.format(
							"Json path field %s:[%s] can't not be evaluated, considering creating a Interpreter for arbitary objects.",
							f.getName(), f.getType()));
					try {
						if (Package.getPackage("java.lang").equals(f.getType().getPackage())) {
							// Handle for java lang type fields
							evalLangField(doc, elmt, obj, f);
						} else if (Collection.class.isAssignableFrom(f.getType())) {
							evalCollectionField(doc, elmt, obj, f);
						} else if (f.getType().isArray()) {
							evalArrayField(doc, elmt, obj, f);
						} else if (null != ConstructorUtils.getAccessibleConstructor(f.getType())) {
							// Handle for Custom type fields
							evalPojoField(doc, elmt, obj, f);
						} else {
							logger.debug(errorMsg);
							if (elmt.required()) {
								throw new RuntimeException(errorMsg);
							}
						}
					} catch (ReflectiveOperationException e) {
						logger.debug(errorMsg);
						if (elmt.required()) {
							throw new RuntimeException(e);
						}
					}
				});
		return obj;
	}

	private void evalArrayField(DocumentContext doc, JsonPathElement elmt, Object obj, Field f)
			throws ReflectiveOperationException {
		// FIXME GAVE UP THE ARRAY!! :(
		List<Object> resLst = readJSONArray(doc, elmt, obj, f);
		Object arry = Array.newInstance(f.getType().getComponentType(), resLst.size());
		FieldUtils.writeField(f, obj, arry, true);
		throw new NotImplementedException(
				"The array type is not implemented, please use the java.util.Collection classes =_+");
	}

	private void evalPojoField(DocumentContext doc, JsonPathElement elmt, Object obj, Field f)
			throws ReflectiveOperationException {
		// Invoke the default constructor
		Object rawObj = ConstructorUtils.invokeExactConstructor(f.getType());
		// Recursively invoke eval for custom object
		FieldUtils.writeField(f, obj, eval(readValueAsDoc(doc, elmt),
				rawObj), true);
	}

	private void evalCollectionField(DocumentContext doc, JsonPathElement elmt, Object obj, Field f)
			throws ReflectiveOperationException {
		List<Object> lst = readJSONArray(doc, elmt, obj, f);
		FieldUtils.writeField(f, obj, lst, true);
	}

	private List<Object> readJSONArray(DocumentContext doc, JsonPathElement elmt, Object obj, Field f)
			throws ReflectiveOperationException {
		Object arry = readJSONAsArray(doc, elmt);
		int arrayLength = jsonProvider.length(arry);
		Class<?> entityClz = resolveFieldClass(f);
		List<Object> lst = new ArrayList<>(arrayLength);
		for (int i = 0; i < arrayLength; i++) {
			DocumentContext curDoc = JsonPath.parse(jsonProvider.getArrayIndex(arry, i));
			Object item = null;
			if (null == entityClz) {
				// For generic collection return json object
				item = curDoc.json();
			} else if (ClassUtils.isAssignable(entityClz, String.class)) {
				item = curDoc.jsonString();
			} else if (null != ConstructorUtils.getAccessibleConstructor(entityClz)) {
				item = eval(curDoc, ConstructorUtils.invokeExactConstructor(entityClz));
			} else {
				String errorMsg = String.format("Json path field %s:[%s] can't not be evaluated",
						f.getName(), f.getType());
				logger.debug(errorMsg);
				if (elmt.required()) {
					throw new RuntimeException(errorMsg);
				}
			}

			if (null != item) {
				lst.add(item);
			}
		}
		return lst;
	}

	private Object readJSONAsArray(DocumentContext doc, JsonPathElement elmt) throws ReflectiveOperationException {
		Object arry = null;
		Object res = readValue(doc, elmt);
		if (jsonProvider.isArray(res)) {
			arry = res;
		} else {
			// covert to array if resp is single
			logger.debug(String.format(
					"Consider changing Collection annotated field[%s] to single object since json response is not an array!",
					elmt));
			arry = configuration.jsonProvider().createArray();
			jsonProvider.setArrayIndex(arry, 0, res);
		}
		return arry;
	}

	private Class<?> resolveFieldClass(Field f) {
		// Figure out entity type
		Class<?> fieldClz = null;
		if (f.getType().isArray()) {
			// f.getClass is good for array
			fieldClz = f.getType().getComponentType();
		} else if (Collection.class.isAssignableFrom(f.getType()) && f.getGenericType() instanceof ParameterizedType) {
			// get collection generic type if exists
			Type entityType = ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
			try {
				fieldClz = Class.forName(TypeUtils.toString(entityType));
			} catch (ClassNotFoundException e) {
				// No class? take it as weak type, return json object
			}
		}
		return fieldClz;
	}

	private void evalLangField(DocumentContext doc, JsonPathElement elmt, Object obj, Field f)
			throws ReflectiveOperationException {
		Object value = readValue(doc, elmt);
		FieldUtils.writeField(f, obj, value, true);
	}

	private Object readValue(DocumentContext doc, JsonPathElement elmt) throws ReflectiveOperationException {
		Object val = null;
		try {
			val = doc.read(elmt.value());
			if (!elmt.interpreter().equals(EmptyInterpreter.class)) {
				val = ConstructorUtils.invokeExactConstructor(elmt.interpreter()).interpret(val);
			}
		} catch (JsonPathException e) {
			logger.debug(e.getMessage());
			if (elmt.required()) {
				throw e;
			}
		}

		return val;
	}

	private DocumentContext readValueAsDoc(DocumentContext doc, JsonPathElement jpElmt)
			throws ReflectiveOperationException {
		return JsonPath.parse(readValue(doc, jpElmt));
	}
}
