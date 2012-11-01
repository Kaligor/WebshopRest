package se.black.webshop.rest.tests.client;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class JerseyClient {
	public final String LOCALHOST = "http://127.0.0.1:8080/";
	public final String BASE;
	private String uri;
	private Client client;
	
	public JerseyClient(String base) {
		BASE = base;
		this.uri = LOCALHOST+BASE;
		this.client = Client.create();
		this.client.setFollowRedirects(true);
	}
	
	public ClientResponse create(String jsonCreate){
		WebResource resource = client.resource(uri);
		return resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON_TYPE).post(ClientResponse.class, jsonCreate);
	}
	
	public ClientResponse getOne(String identifier){
		WebResource resource = client.resource(uri+"/"+identifier);
		return resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	}
	
	public ClientResponse getAll(String urlQueryString){
		WebResource resource = client.resource(uri+"?"+urlQueryString);
		return resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	}
	
	public ClientResponse update(String identifier, String jsonUpdate){
		WebResource resource = client.resource(uri+"/"+identifier);
		return resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).put(ClientResponse.class, jsonUpdate);
	}
	
	public ClientResponse delete(String identifier){
		WebResource resource = client.resource(uri+"/"+identifier);
		return resource.accept(MediaType.APPLICATION_JSON).delete(ClientResponse.class);
	}
	
	public ClientResponse get(URI location){
		WebResource resource = client.resource(location);
		return resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
	}
}
