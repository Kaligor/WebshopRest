package se.black.webshop.rest.order;

import java.lang.reflect.Type;

import se.black.webshop.rest.wrapper.Resource;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ResourceSerializer implements JsonSerializer<Resource>{

	@Override
	public JsonElement serialize(Resource src, Type typeOfSrc,
			JsonSerializationContext context) {
		JsonObject resource = new JsonObject();
		resource.add("Uri", new JsonPrimitive(src.uri.toString()));
		
		return resource;
	}

}
