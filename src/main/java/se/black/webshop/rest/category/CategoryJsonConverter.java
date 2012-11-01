package se.black.webshop.rest.category;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CategoryJsonConverter implements JsonSerializer<CategoryDAO>,
		JsonDeserializer<CategoryDAO> {

	@Override
	public CategoryDAO deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		
		return new CategoryDAO(json);
	}

	@Override
	public JsonElement serialize(CategoryDAO src, Type typeOfSrc,
			JsonSerializationContext context) {
		
		Gson gson = new GsonBuilder().registerTypeAdapter(CategoryDAO.class, this).create();
		
		final JsonObject categoryAsJson = new JsonObject();
		
		categoryAsJson.addProperty("name", src.getName());
		categoryAsJson.addProperty("path", src.getPath());

		JsonArray childrenAsJson = new JsonArray();
		for (CategoryDAO child : src.getChildren()) {
			childrenAsJson.add(gson.toJsonTree(child, CategoryDAO.class));
		}
		
		categoryAsJson.add("children", childrenAsJson);
		return categoryAsJson;
	}

}
