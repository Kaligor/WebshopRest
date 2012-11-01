package se.black.webshop.rest.customer;

import java.io.BufferedReader;
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

import se.black.webshop.rest.exception.BadXMLException;

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;

@Provider
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class CustomerXMLBodyConverter implements
		MessageBodyReader<CustomerDataObject>,
		MessageBodyWriter<CustomerDataObject>{

	// ------- läser FRÅN xml ---------
	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return CustomerDataObject.class.isAssignableFrom(type);
	}

	@Override
	public CustomerDataObject readFrom(Class<CustomerDataObject> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		
		Builder builder = new Builder();
		Document doc = null;

		try {
			doc = builder.build(entityStream);
		} catch (ValidityException e) {
			throw new BadXMLException(e.getMessage());
		} catch (ParsingException e) {
			throw new BadXMLException(e.getMessage());
		}
		
		return new CustomerDataObject(doc);
		
	}

	// ----- konverterar TILL xml ------
	
	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return CustomerDataObject.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(CustomerDataObject t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(CustomerDataObject customer, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		
		Element xmlCustomer = new Element("customer");
		xmlCustomer.addAttribute(new Attribute("id", customer.getUsername()));
		
		Element username = new Element("username");
		username.appendChild(customer.getUsername());
		xmlCustomer.appendChild(username);
		
		Element address = new Element("address");
		
		Element street = new Element("street");
		street.appendChild(customer.getAddress().getStreet());
		address.appendChild(street);

		Element zip = new Element("zip");
		zip.appendChild(customer.getAddress().getZip());
		address.appendChild(zip);

		Element city = new Element("city");
		city.appendChild(customer.getAddress().getCity());
		address.appendChild(city);
		
		xmlCustomer.appendChild(address);
		
		Document doc = new Document(xmlCustomer);
		
		Serializer serializer = new Serializer(entityStream);
		serializer.write(doc);
		
		
	}
	
	

}
