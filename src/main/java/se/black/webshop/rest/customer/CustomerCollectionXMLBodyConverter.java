package se.black.webshop.rest.customer;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import se.black.webshop.rest.product.ProductDataObject;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class CustomerCollectionXMLBodyConverter implements
		MessageBodyWriter<Collection<CustomerDataObject>> {

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
	public void writeTo(Collection<CustomerDataObject> t, Class<?> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {
		
		Element xmlCustomers = new Element("customers");
		
		for (CustomerDataObject customer : t) {
			xmlCustomers.appendChild(convertCustomerToXmlElement(customer));
		}
		
		Document doc = new Document(xmlCustomers);
		
		Serializer serializer = new Serializer(entityStream);
		serializer.write(doc);
		
	}

	
	private Element convertCustomerToXmlElement(CustomerDataObject customer) {
		
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
		
		return xmlCustomer;
		
	}


}
