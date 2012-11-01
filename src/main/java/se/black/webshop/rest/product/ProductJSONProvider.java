package se.black.webshop.rest.product;

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
public class ProductJSONProvider implements MessageBodyReader<ProductDataObject>, MessageBodyWriter<ProductDataObject>{

	public final static Gson gson;
	
	static{
		gson = new GsonBuilder().registerTypeAdapter(ProductDataObject.class, new ProductJSONConverter()).create();
	}
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return ProductDataObject.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(ProductDataObject t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(ProductDataObject product, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {
		entityStream.write(gson.toJson(product).getBytes());
		
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return ProductDataObject.class.isAssignableFrom(type);
	}

	@Override
	public ProductDataObject readFrom(Class<ProductDataObject> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream) throws IOException, WebApplicationException {
		
		
		return gson.fromJson(new InputStreamReader(entityStream), ProductDataObject.class);
	}

}
