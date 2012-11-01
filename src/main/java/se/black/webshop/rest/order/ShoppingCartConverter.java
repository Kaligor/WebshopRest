package se.black.webshop.rest.order;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ShoppingCartConverter implements MessageBodyReader<ShoppingCartDAO>{

	private static Gson gson = new GsonBuilder().serializeNulls().create();
	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		System.err.println(type);
		return ShoppingCartDAO.class.isAssignableFrom(type);
	}

	@Override
	public ShoppingCartDAO readFrom(Class<ShoppingCartDAO> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		return gson.fromJson(new InputStreamReader(entityStream), ShoppingCartDAO.class);
	}


}
