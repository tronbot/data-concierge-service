package io.tronbot.json;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Predicate;

import io.tronbot.json.JsonPathField.EmptyDeserializer;

/**
 * @author <a href="mailto:juanyong.zhang@gmail.com">Juanyong Zhang</a>
 * @date Feb 13, 2017
 */
public class JsonPathReflector {
	private final Configuration configuration;

	public JsonPathReflector() {
		super();
		this.configuration = Configuration.defaultConfiguration();
	}

	public JsonPathReflector(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	public <T> T from(String json, T obj) {
		return eval(JsonPath.parse(json, configuration), obj);
	}

	public static <T> T read(String json, String jsonPath, Predicate... filters) {
		return JsonPath.read(json, jsonPath, filters);
	}

	private <T> T eval(DocumentContext doc, T obj) {
		// Handle for java lang type fields
		FieldUtils.getAllFieldsList(obj.getClass()).stream()
				.filter(f -> null != f.getAnnotation(JsonPathField.class)
						&& Package.getPackage("java.lang").equals(f.getType().getPackage()))
				.forEach(f -> {
					JsonPathField jpath = f.getAnnotation(JsonPathField.class);
					try {
						Object value = readValue(doc, jpath);
						FieldUtils.writeField(f, obj, value, true);
					} catch (ReflectiveOperationException e) {
						e.printStackTrace();
					} catch (InvalidPathException e) {
						if(jpath.required()){
							throw e;
						}
					}
				});

		return obj;
	}

	private Object readValue(DocumentContext doc, JsonPathField jpath) throws ReflectiveOperationException {
		Object val = doc.read(jpath.value());
		if (!jpath.deserializer().equals(EmptyDeserializer.class)) {
			val = ConstructorUtils.invokeConstructor(jpath.deserializer()).transfer(val);
		}
		return val;
	}
}
