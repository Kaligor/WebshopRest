package se.black.webshop.rest.order;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

@Provider
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class CollectionOrderDAOXMLConverter implements MessageBodyWriter<Collection<OrderDAO>>{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		boolean isWritable;
		if (Collection.class.isAssignableFrom(type)
				&& genericType instanceof ParameterizedType) {

			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			Type[] actualTypeArgs = (parameterizedType.getActualTypeArguments());
			isWritable = (actualTypeArgs.length == 1 && actualTypeArgs[0]
					.equals(OrderDAO.class));
		} else {
			isWritable = false;
		}
		return isWritable;
	}

	@Override
	public long getSize(Collection<OrderDAO> t, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(Collection<OrderDAO> orders, Class<?> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {

		Element xmlOrders = new Element("orders");

		for (OrderDAO order : orders) {
			xmlOrders.appendChild(convertToxml(order));
		}

		Document doc = new Document(xmlOrders);

		Serializer serializer = new Serializer(entityStream);
		serializer.write(doc);
		
	}
	
	private Element convertToxml(OrderDAO order){
		Element root = new Element("order");
		root.addAttribute(new Attribute("id", String.valueOf(order.orderno)));
		Element orderno = new Element("orderno");
		Element customer = new Element("customer");
		Element address = new Element("address");
		Element street = new Element("street");
		Element zip = new Element("zip");
		Element city = new Element("city");
		Element username = new Element("username");
		Element orderlines = new Element("orderlines");
		
		orderno.appendChild(String.valueOf(order.orderno));
		username.appendChild(order.username);
		street.appendChild(order.street);
		zip.appendChild(order.zip);
		city.appendChild(order.city);
		address.appendChild(street);
		address.appendChild(zip);
		address.appendChild(city);
		customer.appendChild(address);
		customer.appendChild(username);
		
		for(OrderLineDAO line : order.orderlines){
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
		
		return root;
	}

}
