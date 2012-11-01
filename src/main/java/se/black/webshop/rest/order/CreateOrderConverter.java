package se.black.webshop.rest.order;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import se.black.webshop.rest.customer.CustomerDataObject;
import se.black.webshop.rest.customer.CustomerGsonConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class CreateOrderConverter implements MessageBodyReader<CreateOrderDAO>{

	private Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(CustomerDataObject.class, new CustomerGsonConverter()).registerTypeAdapter(ShoppingCartDAO.class, new ShoppingCartDAOSerializer()).create();
	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return CreateOrderDAO.class.isAssignableFrom(type);
	}

	@Override
	public CreateOrderDAO readFrom(Class<CreateOrderDAO> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		return gson.fromJson(new InputStreamReader(entityStream), CreateOrderDAO.class);
	}

}
