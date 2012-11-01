package se.black.webshop.rest.order;

import java.io.IOException;
import java.io.InputStream;
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

import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;
import nu.xom.Serializer;
import nu.xom.ValidityException;
import se.black.webshop.rest.exception.BadXMLException;


@Provider
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class OrderDAOXMLConverter implements MessageBodyReader<OrderDAO>, MessageBodyWriter<OrderDAO>{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return OrderDAO.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(OrderDAO t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(OrderDAO t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
			WebApplicationException {
		
		Element root = new Element("order");
		root.addAttribute(new Attribute("id", String.valueOf(t.orderno)));
		Element orderno = new Element("orderno");
		Element customer = new Element("customer");
		Element address = new Element("address");
		Element street = new Element("street");
		Element zip = new Element("zip");
		Element city = new Element("city");
		Element username = new Element("username");
		Element orderlines = new Element("orderlines");
		
		orderno.appendChild(String.valueOf(t.orderno));
		username.appendChild(t.username);
		street.appendChild(t.street);
		zip.appendChild(t.zip);
		city.appendChild(t.city);
		address.appendChild(street);
		address.appendChild(zip);
		address.appendChild(city);
		customer.appendChild(address);
		customer.appendChild(username);
		
		for(OrderLineDAO line : t.orderlines){
			Element xmlline = new Element("orderline");
			Element product = new Element("product");
			Element amount = new Element("amount");
			Element sku = new Element("sku");
			Element name = new Element("name");
			Element price = new Element("price");
			Element descr = new Element("description");
			Element categ = new Element("category");
			
			sku.appendChild(line.sku);
			name.appendChild(line.name);
			price.appendChild(String.valueOf(line.price));
			descr.appendChild(line.description);
			categ.appendChild(line.category);
			amount.appendChild(String.valueOf(line.amount));
			
			product.appendChild(sku);
			product.appendChild(name);
			product.appendChild(price);
			product.appendChild(descr);
			product.appendChild(categ);
			
			xmlline.appendChild(product);
			xmlline.appendChild(amount);
			
			orderlines.appendChild(xmlline);
		}
		root.appendChild(customer);
		root.appendChild(orderlines);
		root.appendChild(orderno);
		
		Document doc = new Document(root);
		Serializer serializer = new Serializer(entityStream);
		serializer.write(doc);
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return OrderDAO.class.isAssignableFrom(type);
	}

	@Override
	public OrderDAO readFrom(Class<OrderDAO> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
			WebApplicationException {
		try{
			Builder builder = new Builder();
			Document doc = builder.build(entityStream);
			System.out.println(doc.toXML());
			return new OrderDAO(doc);
		} catch (ValidityException e) {
			throw new BadXMLException(e.getMessage());
		} catch (ParsingException e) {
			throw new BadXMLException(e.getMessage());
		}
	}

}
