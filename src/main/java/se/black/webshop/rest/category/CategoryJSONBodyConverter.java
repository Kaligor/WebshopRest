package se.black.webshop.rest.category;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Provider
public class CategoryJSONBodyConverter implements
		MessageBodyWriter<CategoryDAO>, 
		MessageBodyReader<CategoryDAO> {

	private Gson gson = new GsonBuilder().registerTypeAdapter(CategoryDAO.class, new CategoryJsonConverter()).create();
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(CategoryDAO.class);
	}

//	----- WRITER ----- //	
	@Override
	public long getSize(CategoryDAO t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(CategoryDAO category, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		
		entityStream.write(gson.toJson(category).getBytes());
		
	}

//	------- 	READER -------- //
	
	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(CategoryDAO.class);
	}

	@Override
	public CategoryDAO readFrom(Class<CategoryDAO> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		
		return gson.fromJson(new InputStreamReader(entityStream), CategoryDAO.class);
		
	}

}
