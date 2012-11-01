package se.black.webshop.rest.customer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerJSONBodyConverter implements
		MessageBodyReader<CustomerDataObject>,
		MessageBodyWriter<CustomerDataObject>{
	
	private Gson gson;
	
	public CustomerJSONBodyConverter() {
		gson = new GsonBuilder().registerTypeAdapter(CustomerDataObject.class, new CustomerGsonConverter()).create();
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(CustomerDataObject.class);
	}

	@Override
	public long getSize(CustomerDataObject t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	// konverterar till JSON
	@Override
	public void writeTo(CustomerDataObject customer, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {

			entityStream.write(gson.toJson(customer).getBytes());
		
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(CustomerDataObject.class);
	}
	
	// konverterar till JAVA
	@Override
	public CustomerDataObject readFrom(Class<CustomerDataObject> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {

		return gson.fromJson(new InputStreamReader(entityStream), CustomerDataObject.class);
	}

}
