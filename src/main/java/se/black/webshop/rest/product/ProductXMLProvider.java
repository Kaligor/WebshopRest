package se.black.webshop.rest.product;

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

import se.black.webshop.rest.customer.CustomerDataObject;
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
public class ProductXMLProvider implements MessageBodyReader<ProductDataObject>, MessageBodyWriter<ProductDataObject> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return ProductDataObject.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(ProductDataObject t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(ProductDataObject product, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

		Element xmlProduct = new Element("product");
		xmlProduct.addAttribute(new Attribute("id", product.getSku()));

		Element sku = new Element("sku");
		sku.appendChild(product.getSku());
		xmlProduct.appendChild(sku);

		Element name = new Element("name");
		name.appendChild(product.getName());
		xmlProduct.appendChild(name);

		Element price = new Element("price");
		price.appendChild(String.valueOf(product.getPrice()));
		xmlProduct.appendChild(price);

		Element description = new Element("description");
		description.appendChild(product.getDescription());
		xmlProduct.appendChild(description);

		Element categoryPath = new Element("categoryPath");
		categoryPath.appendChild(product.getCategoryPath());
		xmlProduct.appendChild(categoryPath);

		Document doc = new Document(xmlProduct);

		Serializer serializer = new Serializer(entityStream);
		serializer.write(doc);
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return ProductDataObject.class.isAssignableFrom(type);
	}

	@Override
	public ProductDataObject readFrom(Class<ProductDataObject> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {

		Builder builder = new Builder();
		Document doc = null;

		try {
			doc = builder.build(entityStream);
		} catch (ValidityException e) {
			throw new BadXMLException(e.getMessage());
		} catch (ParsingException e) {
			throw new BadXMLException(e.getMessage());
		}
		
		return new ProductDataObject(doc);
	}

}
