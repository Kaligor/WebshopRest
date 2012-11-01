package se.black.webshop.rest.tests;

import static org.junit.Assert.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.core.MediaType;

import org.junit.Test;

import se.black.webshop.rest.tests.client.JerseyClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.ClientResponse.Status;

public class ProductTests {
	
	private JerseyClient productClient = new JerseyClient("WebshopRestAPI/product");
	private JerseyClient categoryClient = new JerseyClient("WebshopRestAPI/category/");
	
	@Test
	public void canGetManyAndFilterWithQuery() {
		insertProduct("sill", "fisk-1", 100L, "finafisken", "/");
		insertProduct("sill", "fisk-2", 100L, "fulafisken", "/");
		insertProduct("lax", "fisk-3", 200L, "finafisken", "/");
		insertProduct("lax", "fisk-4", 100L, "fulafisken", "/");
		insertProduct("sik", "fisk-5", 350L, "dyrafisken", "/");
		insertProduct("sik", "fisk-6", 100L, "finafisken", "/");
		
		
		JsonArray fromStorage = getFilteredProducts("name=sill");
		assertEquals(2, fromStorage.size());
		assertEquals("finafisken", fromStorage.get(0).getAsJsonObject().get("description").getAsString());
		assertEquals("fulafisken", fromStorage.get(1).getAsJsonObject().get("description").getAsString());
		
		//getta alla, ska va 6 st:
		fromStorage = getFilteredProducts("");
		assertEquals(6, fromStorage.size());
		
		//ska finnas bara en lax m priset 200
		fromStorage = getFilteredProducts("name=lax&price=200");
		assertEquals(1, fromStorage.size());
		//dess beskrivning ska va "fina fisken"
		assertEquals("finafisken", fromStorage.get(0).getAsJsonObject().get("description").getAsString());
		
		fromStorage = getFilteredProducts("description=finafisken&price=100");
		//ska finnas 2 fina fisken m pris 100
		assertEquals(2, fromStorage.size());
		//den forsta ska heta "sill"
		assertTrue(fromStorage.get(0).getAsJsonObject().get("name").getAsString().equals("sill"));
		//den andra ska heta "sik"
		assertTrue(fromStorage.get(1).getAsJsonObject().get("name").getAsString().equals("sik"));
				
		//fulafisken +lax ska ha priset 100
		fromStorage = getFilteredProducts("name=lax&description=fulafisken&price=100");
		assertEquals(1, fromStorage.size());
		assertTrue(fromStorage.get(0).getAsJsonObject().get("sku").getAsString().equals("fisk-4"));
		
		productClient.delete("fisk-1");
		productClient.delete("fisk-2");
		productClient.delete("fisk-3");
		productClient.delete("fisk-4");
		productClient.delete("fisk-5");
		productClient.delete("fisk-6");
		
		
	}
	
	@Test
	public void testCreateGetPutDeleteOnOneProduct(){
		
		//TODO: assertEquals pa responserna. responserna hamtar man pa client.metod().
		
		//create
		
		JsonObject jsonProduct = new JsonObject();
		
		jsonProduct.addProperty("name", "Sill");
		jsonProduct.addProperty("sku", "fisk-1");
		jsonProduct.addProperty("price", Long.toString(100L));
		jsonProduct.addProperty("description", "God naringsrik fisk");
		jsonProduct.addProperty("categoryPath", "/");
		
		 
		
		 
		ClientResponse response = productClient.create(jsonProduct.toString());
		 
		 
		assertEquals(Status.CREATED, response.getClientResponseStatus());
		
		ClientResponse response2 = productClient.create(jsonProduct.toString());
		assertEquals(Status.CONFLICT, response2.getClientResponseStatus());
		
		//get
		ClientResponse getResponse1 = productClient.getOne("fisk-1");
		String stringJsonProductFromService = getResponse1.getEntity(String.class);
		
		JsonObject fromStorage = new Gson().fromJson(stringJsonProductFromService, JsonElement.class).getAsJsonObject();
		
		assertEquals(jsonProduct.get("name").getAsString(), fromStorage.get("name").getAsString());
		assertEquals(jsonProduct.get("sku").getAsString(), fromStorage.get("sku").getAsString());
		assertEquals(jsonProduct.get("price").getAsString(), fromStorage.get("price").getAsString());
		assertEquals(jsonProduct.get("description").getAsString(), fromStorage.get("description").getAsString());
		
		assertEquals(jsonProduct.get("categoryPath").getAsString(), fromStorage.get("categoryPath").getAsString());


		assertEquals(Status.OK, getResponse1.getClientResponseStatus());

		ClientResponse getResponse2 = productClient.getOne("FINNS_EJ");
		assertEquals(Status.NOT_FOUND, getResponse2.getClientResponseStatus());
		
//		//put
		jsonProduct.addProperty("name", "name-CHANGED");
		jsonProduct.addProperty("price", 242L);
		jsonProduct.addProperty("description", "description-CHANGED");
		jsonProduct.addProperty("categoryPath", "/");

		ClientResponse putResponse1 = productClient.update(jsonProduct.get("sku").getAsString(), jsonProduct.toString());
		
		assertEquals(Status.NO_CONTENT, putResponse1.getClientResponseStatus());
		
		stringJsonProductFromService = productClient.getOne(jsonProduct.get("sku").getAsString()).getEntity(String.class);
		JsonObject updatedFromStorage = new Gson().fromJson(stringJsonProductFromService, JsonElement.class).getAsJsonObject();
		
		 
		 
		
		assertEquals(jsonProduct.get("name").getAsString(), updatedFromStorage.get("name").getAsString());
		assertEquals(jsonProduct.get("sku").getAsString(), updatedFromStorage.get("sku").getAsString());
		assertEquals(jsonProduct.get("price").getAsString(), updatedFromStorage.get("price").getAsString());
		assertEquals(jsonProduct.get("description").getAsString(), updatedFromStorage.get("description").getAsString());
		assertEquals(jsonProduct.get("categoryPath").getAsString(),updatedFromStorage.get("categoryPath").getAsString());
		 
		
		ClientResponse putResponse2 = productClient.update("FINNS_EJ", jsonProduct.toString());
		
		assertEquals(Status.NO_CONTENT, putResponse1.getClientResponseStatus());
		assertEquals(Status.NOT_FOUND, putResponse2.getClientResponseStatus());
		
		//delete
		productClient.delete(updatedFromStorage.get("sku").getAsString());
		ClientResponse deleteResponse1 = productClient.getOne("anca01");
		assertEquals(Status.NOT_FOUND, deleteResponse1.getClientResponseStatus());
		
	}

	@Test
	public void canHandleLoadsOfRequest() {
		
		Runnable r = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < 2; i++) {
					String randomSku = new Double(Math.random()).toString();
					insertProduct("hej", randomSku, 100L, "ojojhejsan", "/");
					productClient.delete(randomSku);
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
	public void canCreateGetAndDeleteCategory() {
		
		ClientResponse postResponse = categoryClient.create("{\"name\":\"newcategory\"}");
		assertEquals(Status.CREATED, postResponse.getClientResponseStatus());
		
		Client client = Client.create();
		client.setFollowRedirects(true);
		WebResource resource = client.resource("http://localhost:8080/WebshopRestAPI/category/newcategory/");
		ClientResponse getResponse = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		
		assertEquals(Status.OK, getResponse.getClientResponseStatus());
		assertTrue(getResponse.getEntity(String.class).contains("newcategory"));
		
		resource = client.resource("http://localhost:8080/WebshopRestAPI/category/newcategory/");
		ClientResponse deleteResponse = resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
		
		resource = client.resource("http://localhost:8080/WebshopRestAPI/category/newcategory/");
		ClientResponse notFoundResponse = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		
		assertEquals(Status.NOT_FOUND, notFoundResponse.getClientResponseStatus());
	}
	
	@Test
	public void canGetProductsBasedOnCategory() {
		


		
	}
	
	private void insertProduct(String name, String sku, Long price, String description, String categoryPath) {
		JsonObject jsonProduct = new JsonObject();

		jsonProduct.addProperty("name", name);
		jsonProduct.addProperty("sku", sku);
		jsonProduct.addProperty("price", price);
		jsonProduct.addProperty("description", description);
		jsonProduct.addProperty("categoryPath", categoryPath);
		ClientResponse response = productClient.create(jsonProduct.toString());
		assertEquals(Status.CREATED, response.getClientResponseStatus());
	}

	
	private JsonArray getFilteredProducts(String queryString) {
		String productsFromService = productClient.getAll(queryString).getEntity(String.class);
		return new Gson().fromJson(productsFromService, JsonElement.class).getAsJsonArray();
	}
	
	
}
