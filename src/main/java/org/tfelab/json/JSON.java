package org.tfelab.json;

import com.google.gson.*;
import org.tfelab.io.requester.account.AccountWrapper;
import org.tfelab.io.requester.chrome.action.ChromeDriverAction;
import org.tfelab.io.requester.proxy.ProxyWrapper;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Date;

/**
 * JSON 辅助工具类
 */
public class JSON {

	private static GsonBuilder gb = new GsonBuilder()
			.disableHtmlEscaping()
			.registerTypeAdapter(Date.class, new DateSerializer()).setDateFormat(DateFormat.LONG)
			.registerTypeAdapter(Date.class, new DateDeserializer()).setDateFormat(DateFormat.LONG)
			.registerTypeAdapter(Exception.class, new ExceptionSerializer())
			.registerTypeAdapter(Exception.class, new ExceptionDeserializer())
			.registerTypeAdapter(ChromeDriverAction.class, new InterfaceAdapter<ChromeDriverAction>())
			.registerTypeAdapter(AccountWrapper.class, new InterfaceAdapter<AccountWrapper>())
			.registerTypeAdapter(ProxyWrapper.class, new InterfaceAdapter<ProxyWrapper>());

	public static final Gson gson;

	public static final Gson _gson;

	static {
		gson = gb.create();
		_gson = gb.setPrettyPrinting().create();
	}

	public static <T> T fromJson(String json, Type type) {
		return gson.fromJson(json, type);
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		return gson.fromJson(json, clazz);
	}

	public static String toJson(Object obj){
		return gson.toJson(obj);
	}

	public static String toPrettyJson(Object obj){
		return _gson.toJson(obj);
	}

	static class DateDeserializer implements JsonDeserializer<Date> {

	    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
	        return new Date(json.getAsJsonPrimitive().getAsLong());
	    }
	}

	static class DateSerializer implements JsonSerializer<Date> {
	    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
	        return new JsonPrimitive(src.getTime());
	    }
	}

	static class ExceptionDeserializer implements JsonDeserializer<Exception> {
		 
	    public Exception deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
	        return new Exception(json.getAsJsonPrimitive().getAsString());
	    }
	}

	static class ExceptionSerializer implements JsonSerializer<Exception> {
	    public JsonElement serialize(Exception src, Type typeOfSrc, JsonSerializationContext context) {
	        return new JsonPrimitive(src.getMessage());
	    }
	}
}

/**
 * 通用接口序列化适配器
 * @param <T>
 */
final class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
	public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
		final JsonObject wrapper = new JsonObject();
		wrapper.addProperty("type", object.getClass().getName());
		wrapper.add("data", context.serialize(object));
		return wrapper;
	}

	public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
		final JsonObject wrapper = (JsonObject) elem;
		final JsonElement typeName = get(wrapper, "type");
		final JsonElement data = get(wrapper, "data");
		final Type actualType = typeForName(typeName);
		return context.deserialize(data, actualType);
	}

	private Type typeForName(final JsonElement typeElem) {
		try {
			return Class.forName(typeElem.getAsString());
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}

	private JsonElement get(final JsonObject wrapper, String memberName) {
		final JsonElement elem = wrapper.get(memberName);
		if (elem == null) throw new JsonParseException("no '" + memberName + "' member found in what was expected to be an interface wrapper");
		return elem;
	}
}


