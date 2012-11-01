package se.black.webshop.rest.customer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@Provider
public class CustomerCollectionJSONBodyConverter implements 
										MessageBodyWriter<Collection<CustomerDataObject>>{
	
	private Gson gson;
	
	public CustomerCollectionJSONBodyConverter() {
		gson = new GsonBuilder().registerTypeAdapter(CustomerDataObject.class, new CustomerGsonConverter()).create();
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		
		boolean isWritable;
		if (Collection.class.isAssignableFrom(type)
				&& genericType instanceof ParameterizedType) {

			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			Type[] actualTypeArgs = (parameterizedType.getActualTypeArguments());
			isWritable = (actualTypeArgs.length == 1 && actualTypeArgs[0]
					.equals(CustomerDataObject.class));
		} else {
			isWritable = false;
		}

		return isWritable;
		
	}

	@Override
	public long getSize(Collection<CustomerDataObject> t, Class<?> type,
			Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(Collection<CustomerDataObject> customers, Class<?> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		
		entityStream.write(gson.toJson(customers).getBytes());
		
	}

}
