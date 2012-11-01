package se.black.webshop.rest.order;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import se.black.webshop.model.order.OrderLine;
import se.black.webshop.model.order.OrderedCustomer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class OrderDAOSerializer implements JsonSerializer<OrderDAO>, JsonDeserializer<OrderDAO> {

	@Override
	public JsonElement serialize(OrderDAO src, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject order = new JsonObject();
		JsonObject customer = new JsonObject();
		JsonObject address = new JsonObject();
		JsonArray orderlines = new JsonArray();
		JsonObject orderline = null;
		JsonObject product = null;
		
		address.add("street", new JsonPrimitive(src.street));
		address.add("zip", new JsonPrimitive(src.zip));
		address.add("city", new JsonPrimitive(src.city));
		
		customer.add("username", new JsonPrimitive(src.username));
		customer.add("deliveryAddress", address);

		for(OrderLineDAO line : src.orderlines){
			String sku = line.sku;
			String name = line.name;
			Long price = line.price;
			String description = line.description;
			String category = line.category;
			product = new JsonObject();
			product.add("sku", new JsonPrimitive(sku));
			product.add("name", new JsonPrimitive(name));
			product.add("price", new JsonPrimitive(price));
			product.add("description", new JsonPrimitive(description));
			product.add("categoryName", new JsonPrimitive(category));
			
			orderline = new JsonObject();
			orderline.add("product", product);
			orderline.add("amount", new JsonPrimitive(line.amount));
			
			orderlines.add(orderline);
		}
		
		order.addProperty("orderno", src.orderno);
		order.add("customer", customer);
		order.add("orderlines", orderlines);
		return order;
	}

	@Override
	public OrderDAO deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			Long orderno = jsonObject.get("orderno").getAsLong();
			Collection<OrderLine> orderlines = new ArrayList<OrderLine>();
			JsonArray lines = jsonObject.get("orderlines").getAsJsonArray();
			
			for(int i = 0; i < lines.size(); i++){
				orderlines.add(new Gson().fromJson(lines.get(i), OrderLine.class));
			}
			
			OrderedCustomer customer = new Gson().fromJson(jsonObject.get("customer"), OrderedCustomer.class);
			
			return new OrderDAO(orderno, customer, orderlines);
	}
}
