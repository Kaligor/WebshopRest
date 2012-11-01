package se.black.webshop.rest.order;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import se.black.webshop.rest.wrapper.Resource;

@Provider
public class ResourceConverter implements MessageBodyWriter<Resource>{

	private static Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(Resource.class, new ResourceSerializer()).create();
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return Resource.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(Resource t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(Resource resource, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {

		entityStream.write(gson.toJson(resource).getBytes());
	}

}
