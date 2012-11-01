package se.black.webshop.rest.order;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ShoppingCartDAOSerializer implements JsonDeserializer<ShoppingCartDAO>{

	@Override
	public ShoppingCartDAO deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject cart = json.getAsJsonObject();
		JsonArray cartlinesJson = cart.get("cartlines").getAsJsonArray();
		Map<String, Integer> cartlines = new HashMap<String, Integer>();
		
		for(int i = 0; i < cartlinesJson.size(); i++){
			String sku = cartlinesJson.get(i).getAsJsonObject().get("sku").getAsString();
			int amount = cartlinesJson.get(i).getAsJsonObject().get("amount").getAsInt();
			cartlines.put(sku, amount);
		}
		
		
		return new ShoppingCartDAO(cartlines);
	}

}
