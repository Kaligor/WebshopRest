package se.black.webshop.rest.tests;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import se.black.webshop.rest.tests.client.JerseyClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

public class CustomerTests {
	
	private JerseyClient client = new JerseyClient("WebshopRestAPI/customer");

	
	@Test
	public void canCreateAndReadAndUpdateAndDeleteCustomer() {
		
		JsonObject jsonCustomer = new JsonObject();
		jsonCustomer.addProperty("username", "anca01");
		jsonCustomer.addProperty("password", "secret");
		
		JsonObject jsonAddress = new JsonObject();
		jsonAddress.addProperty("street", "rollerbladesvägen");
		jsonAddress.addProperty("city", "stockholm");
		jsonAddress.addProperty("zip", "68291");
		
		jsonCustomer.add("address", jsonAddress);
		
		ClientResponse response = client.create(jsonCustomer.toString());
		assertEquals(Status.CREATED, response.getClientResponseStatus());
		
		ClientResponse response2 = client.create(jsonCustomer.toString());
		assertEquals(409, response2.getStatus());
		
		String jsonCustomerFromService = client.getOne("anca01").getEntity(String.class);
		
		JsonObject fromStorage = new Gson().fromJson(jsonCustomerFromService, JsonElement.class).getAsJsonObject();
		
		assertEquals("anca01", fromStorage.get("username").getAsString());
		assertFalse(fromStorage.has("password"));
		assertEquals("rollerbladesvägen", fromStorage.get("address").getAsJsonObject().get("street").getAsString());
		assertEquals("stockholm", fromStorage.get("address").getAsJsonObject().get("city").getAsString());
		assertEquals("68291", fromStorage.get("address").getAsJsonObject().get("zip").getAsString());
		
		fromStorage.addProperty("password", "super-secret");
		fromStorage.get("address").getAsJsonObject().addProperty("street", "sveavägen");
		
		ClientResponse response3 = client.update("anca01", fromStorage.toString());
		assertEquals(Status.NO_CONTENT, response3.getClientResponseStatus());
		
		ClientResponse responseBadUpdate = client.update("anca01", "blalalalal|||\\öö");
		assertEquals(400, responseBadUpdate.getStatus());
		
		jsonCustomerFromService = client.getOne("anca01").getEntity(String.class);
		fromStorage = new Gson().fromJson(jsonCustomerFromService, JsonElement.class).getAsJsonObject();
		
		assertEquals("sveavägen", fromStorage.get("address").getAsJsonObject().get("street").getAsString());
		
		client.delete("anca01");
		
		ClientResponse response4 = client.getOne("anca01");
		assertEquals(404, response4.getStatus());
		
		ClientResponse response5 = client.delete("anca01");
		assertEquals(404, response5.getStatus());
		
	}
	
	@Test
	public void canHandleLoadsOfRequest() {
		
		Runnable r = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 2; i++) {
					String randomUsername = new Double(Math.random()).toString();
					insertCustomer(randomUsername, "secret", "rollerbladesvägen", "sthlm", "12345");
					client.delete(randomUsername);
				}
			}
			
		};
		int noOfThreads = 65;
		
		ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);
        
        for (int i = 1; i < noOfThreads; i++) {
                executor.execute(r);
        }
        executor.shutdown();
        
        while(!executor.isTerminated()) {
                
        }
		
	}
	
	@Test
	public void canGetManyAndFilterWithQuery() {
		insertCustomer("anca01", "secret", "rollerbladesvägen", "sthlm", "12345");
		insertCustomer("BENNY", "secret", "johanneshovsgatan", "sthlm", "54321");
		insertCustomer("orvar", "secret", "rollerbladesvägen", "skövde", "123456");
		insertCustomer("rutger", "secret", "kurtolssongatan", "sveg", "12345");
		insertCustomer("kurt", "secret", "johanneshovsgatan", "sthlm", "12345");

		
		JsonArray fromStorage = getFilteredCustomers("street=kurtolssongatan");
		assertEquals(1, fromStorage.size());
		assertEquals("rutger", fromStorage.get(0).getAsJsonObject().get("username").getAsString());
		
		fromStorage = getFilteredCustomers("city=sthlm&zip=12345");
		assertEquals(2, fromStorage.size());
		assertEquals("anca01", fromStorage.get(0).getAsJsonObject().get("username").getAsString());
		
		fromStorage = getFilteredCustomers("username=benny&username=orvar");
		assertEquals(2, fromStorage.size());
		assertTrue(fromStorage.get(0).getAsJsonObject().get("username").getAsString().equals("benny") ||
				fromStorage.get(0).getAsJsonObject().get("username").getAsString().equals("orvar"));
		
		fromStorage = getFilteredCustomers("city=sthlm&zip=123456");
		assertEquals(0, fromStorage.size());
		
		fromStorage = getFilteredCustomers("street=johanneshovsgatan&street=kurtolssongatan&zip=12345");
		assertEquals(2, fromStorage.size());
		assertTrue(fromStorage.get(0).getAsJsonObject().get("username").getAsString().equals("rutger") ||
				fromStorage.get(0).getAsJsonObject().get("username").getAsString().equals("kurt"));
		
		client.delete("anca01");
		client.delete("benny");
		client.delete("orvar");
		client.delete("rutger");
		client.delete("kurt");
	}
	
	private JsonArray getFilteredCustomers(String queryString) {
		String customersFromService = client.getAll(queryString).getEntity(String.class);
		return new Gson().fromJson(customersFromService, JsonElement.class).getAsJsonArray();
	}
	
	private void insertCustomer(String username, String password, String street, String city, String zip) {
		JsonObject jsonCustomer = new JsonObject();
		jsonCustomer.addProperty("username", username);
		jsonCustomer.addProperty("password", password);
		
		JsonObject jsonAddress = new JsonObject();
		jsonAddress.addProperty("street", street);
		jsonAddress.addProperty("city", city);
		jsonAddress.addProperty("zip", zip);
		
		jsonCustomer.add("address", jsonAddress);
		
		ClientResponse response = client.create(jsonCustomer.toString());
		assertEquals(Status.CREATED, response.getClientResponseStatus());
	}

}
