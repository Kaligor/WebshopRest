package se.black.webshop.rest.customer;

import java.lang.reflect.Type;

import se.black.webshop.model.account.Address;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CustomerGsonConverter implements
		JsonSerializer<CustomerDataObject>,
		JsonDeserializer<CustomerDataObject> {

	@Override
	public CustomerDataObject deserialize(JsonElement j, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		JsonObject json = j.getAsJsonObject();
		JsonObject jsonAddress = j.getAsJsonObject().get("address").getAsJsonObject();
		return new CustomerDataObject(json.get("username").getAsString(), 
				  						json.get("password").getAsString(),
										new Address(jsonAddress.get("street").getAsString(), 
													jsonAddress.get("city").getAsString(), 
													jsonAddress.get("zip").getAsString())
										);
	}

	@Override
	public JsonElement serialize(CustomerDataObject customer, Type typeOfSrc,
			JsonSerializationContext context) {
		
		JsonObject jsonCustomer = new JsonObject();
		jsonCustomer.addProperty("username", customer.getUsername());
		
		JsonObject jsonAddress = new JsonObject();
		jsonAddress.addProperty("street", customer.getAddress().getStreet());
		jsonAddress.addProperty("city", customer.getAddress().getCity());
		jsonAddress.addProperty("zip", customer.getAddress().getZip());
		
		jsonCustomer.add("address", jsonAddress);
		
		return jsonCustomer;
		
	}

}
