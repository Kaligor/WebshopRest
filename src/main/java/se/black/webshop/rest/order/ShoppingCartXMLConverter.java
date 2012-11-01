package se.black.webshop.rest.order;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import nu.xom.Builder;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

@Provider
@Consumes(MediaType.APPLICATION_XML)
public class ShoppingCartXMLConverter implements MessageBodyReader<ShoppingCartDAO>{

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return ShoppingCartDAO.class.isAssignableFrom(type);
	}

	@Override
	public ShoppingCartDAO readFrom(Class<ShoppingCartDAO> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {

		Builder builder = new Builder();
		try {
			return new ShoppingCartDAO(builder.build(entityStream));
		} catch (ValidityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
