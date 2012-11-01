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
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import se.black.webshop.rest.customer.CustomerDataObject;

@Provider
@Consumes(MediaType.APPLICATION_XML)
public class CreateOrderXMLConverter implements MessageBodyReader<CreateOrderDAO>{

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return CreateOrderDAO.class.isAssignableFrom(type);
				
	}

	@Override
	public CreateOrderDAO readFrom(Class<CreateOrderDAO> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {

		
		Builder builder = new Builder();
		
		Document doc = null;
		try {
			doc = builder.build(entityStream);
		} catch (ValidityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new CreateOrderDAO(new ShoppingCartDAO(doc), new CustomerDataObject(doc));
	}

}
