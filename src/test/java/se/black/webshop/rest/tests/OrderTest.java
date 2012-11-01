package se.black.webshop.rest.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.black.webshop.rest.tests.client.JerseyClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

public class OrderTest {
	
	
	private JerseyClient customerClient = new JerseyClient("WebshopRestAPI/customer");
	private JerseyClient orderClient = new JerseyClient("WebshopRestAPI/order");
	private JerseyClient productClient = new JerseyClient("WebshopRestAPI/product");
	
	@Test
	public void canCRUDOrder() throws URISyntaxException {
		
		try{
		
		Gson gson = new Gson();
//		Creating customer
		JsonObject jsonCustomer = new JsonObject();
		jsonCustomer.addProperty("username", "anca01");
		jsonCustomer.addProperty("password", "secret");
		JsonObject jsonAddress = new JsonObject();
		jsonAddress.addProperty("street", "rollerbladesvägen");
		jsonAddress.addProperty("city", "stockholm");
		jsonAddress.addProperty("zip", "68291");
		jsonCustomer.add("address", jsonAddress);
		customerClient.create(jsonCustomer.toString());
		
//		Creating product
		JsonObject jsonSill = new JsonObject();
		jsonSill.addProperty("name", "Sill");
		jsonSill.addProperty("sku", "FI-SI-1");
		jsonSill.addProperty("price", Long.toString(100L));
		jsonSill.addProperty("description", "God naringsrik fisk");
		jsonSill.addProperty("categoryPath", "/");
		ClientResponse response1 = productClient.create(jsonSill.toString());

		
		JsonObject jsonLax = new JsonObject();
		jsonLax.addProperty("name", "Lax");
		jsonLax.addProperty("sku", "FI-LA-1");
		jsonLax.addProperty("price", Long.toString(100L));
		jsonLax.addProperty("description", "God naringsrik fisk");
		jsonLax.addProperty("categoryPath", "/");
		ClientResponse response2 = productClient.create(jsonLax.toString());

		JsonObject jsonReka = new JsonObject();
		jsonReka.addProperty("name", "Reka");
		jsonReka.addProperty("sku", "FI-RE-1");
		jsonReka.addProperty("price", Long.toString(100L));
		jsonReka.addProperty("description", "God naringsrik reka");
		jsonReka.addProperty("categoryPath", "/");
		ClientResponse response3 = productClient.create(jsonReka.toString());
		
		assertEquals(Status.CREATED, response1.getClientResponseStatus());
		assertEquals(Status.CREATED, response2.getClientResponseStatus());
		assertEquals(Status.CREATED, response3.getClientResponseStatus());
		
//		Getting customer
		String customer = customerClient.getOne("anca01").getEntity(String.class);
		
		JsonElement elementCustomer = gson.fromJson(customer, JsonElement.class);

//		Adding necessary password-field to customer
		elementCustomer.getAsJsonObject().addProperty("password", "secret");

//		Creating shoppingcart in json-format
		JsonObject cart = new JsonObject();
		
//		Shoppingcart has cartlines which holds sku and amount
		JsonArray cartlines = new JsonArray();
		JsonObject cartline = new JsonObject();
		cartline.addProperty("sku", "FI-SI-1");
		cartline.addProperty("amount", "3");
		cartlines.add(cartline);
		
		cartline = new JsonObject();
		cartline.addProperty("sku", "FI-LA-1");
		cartline.addProperty("amount", "1");
		cartlines.add(cartline);
		cart.add("cartlines", cartlines);
		
		
//		Adding the created json-objects to the CreateOrder-object		
		JsonObject jsonCreateOrder = new JsonObject();
		jsonCreateOrder.add("customer", elementCustomer);
		jsonCreateOrder.add("cart", cart);
		
		
//		Creating order
		ClientResponse create = orderClient.create(jsonCreateOrder.toString());
		assertEquals(Status.CREATED, create.getClientResponseStatus());
		
		ClientResponse createdOrderResponse = orderClient.get(create.getLocation());
		String createdOrder = createdOrderResponse.getEntity(String.class);
		JsonObject createdOrderJson = gson.fromJson(createdOrder, JsonElement.class).getAsJsonObject();

//		-----------------
		URI location = create.getLocation();
		ClientResponse readResponse = orderClient.get(location);
		
//		ASSERT READ AND READ
		assertEquals(Long.parseLong(location.toString().substring(location.toString().lastIndexOf("/")+1)), createdOrderJson.get("orderno").getAsLong());
		 
		
//		------------------
		String entity = readResponse.getEntity(String.class);
		JsonElement fromStorage = gson.fromJson(entity, JsonElement.class);

		JsonArray orderlines = fromStorage.getAsJsonObject().get("orderlines").getAsJsonArray();
		orderlines.get(0).getAsJsonObject().addProperty("amount", 50);
		
		fromStorage.getAsJsonObject().add("orderlines", orderlines);
		orderClient.update(location.toString().substring(location.toString().lastIndexOf("/")+1), fromStorage.toString());
		
		entity = orderClient.get(location).getEntity(String.class);
		fromStorage = gson.fromJson(entity, JsonElement.class);
		orderlines = fromStorage.getAsJsonObject().get("orderlines").getAsJsonArray();
		Integer newAmount = null;
		for(int i = 0; i < orderlines.size(); i++){
			if(orderlines.get(i).getAsJsonObject().get("amount").getAsInt() == 50){
				newAmount = 50;
			}
		}
		
//		ASSERT QUERY BY PRODUCT SKU GET
		
		//creating a new cart:
		JsonObject qCart = new JsonObject();
		
//		adding product sku to cart
		JsonArray qCartlines = new JsonArray();
		JsonObject qCartline = new JsonObject();
		JsonObject qCartlineLax = new JsonObject();

		qCartline.addProperty("sku", "FI-RE-1");
		qCartline.addProperty("amount", "1");
		qCartlines.add(qCartline);
		 

		
		qCartlineLax.addProperty("sku", "FI-LA-1");
		qCartlineLax.addProperty("amount", "1");
		qCartlines.add(qCartlineLax);
		 
		
		qCart.add("cartlines", qCartlines);
		
		//creating a new order
		JsonObject qJsonCreateOrder = new JsonObject();
		qJsonCreateOrder.add("customer", elementCustomer);
		qJsonCreateOrder.add("cart", qCart);
		 
		 
		
		//Creating order
		ClientResponse qCreate = orderClient.create(qJsonCreateOrder.toString());
		assertEquals(Status.CREATED, qCreate.getClientResponseStatus());
		
		//getting order
		
		

		JsonArray qFromStorageList = getFilteredOrders("productsku=FI-RE-1");
		
		List<String> skusFromFilteredOrders = new ArrayList<String>();
		
		for(JsonElement jsonOrder : qFromStorageList){
						
			JsonArray orderLineArray = jsonOrder.getAsJsonObject().get("orderlines").getAsJsonArray();
			for(JsonElement orderLine : orderLineArray){
				skusFromFilteredOrders.add(orderLine.getAsJsonObject().get("product").getAsJsonObject().get("sku").getAsString());
			}
			
		}
		
		 
		 
		assertTrue(skusFromFilteredOrders.contains("FI-RE-1"));
		assertTrue(!skusFromFilteredOrders.contains("FI-SI-1"));
		
		
//		ASSERT UPDATE TODO:
		
		assertEquals(new Integer(50), newAmount);
//		------------------
		orderClient.delete(location.toString().substring(location.toString().lastIndexOf("/")+1));
		orderClient.delete(location.toString().substring(location.toString().lastIndexOf("/")+0));
		customerClient.delete("anca01");
		productClient.delete("FI-SI-1");
		productClient.delete("FI-LA-1");
		productClient.delete("FI-RE-1");
//		ASSERT DELETE TODO:
		
		ClientResponse getResponseStatus = orderClient.get(location);
		assertEquals(Status.NOT_FOUND, getResponseStatus.getClientResponseStatus());
		} finally{
			
			for(long i = 0; i < 30; i++){
				orderClient.delete(Long.toString(i));
			}
			
			customerClient.delete("anca01");
			productClient.delete("FI-SI-1");
			productClient.delete("FI-LA-1");
			productClient.delete("FI-RE-1");
			
		}
	}
	
	private JsonArray getFilteredOrders(String queryString) {
		String ordersFromService = orderClient.getAll(queryString).getEntity(String.class);
		return new Gson().fromJson(ordersFromService, JsonElement.class).getAsJsonArray();
	}

}
