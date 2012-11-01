package se.black.webshop.rest.product;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

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
public class ProductManyXMLProvider implements
		MessageBodyWriter<List<ProductDataObject>> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {

		boolean isWritable;
		if (Collection.class.isAssignableFrom(type)
				&& genericType instanceof ParameterizedType) {

			ParameterizedType parameterizedType = (ParameterizedType) genericType;
			Type[] actualTypeArgs = (parameterizedType.getActualTypeArguments());
			isWritable = (actualTypeArgs.length == 1 && actualTypeArgs[0]
					.equals(ProductDataObject.class));
		} else {
			isWritable = false;
		}
		
		return isWritable;

	}

	@Override
	public long getSize(List<ProductDataObject> t, Class<?> type,
			Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(List<ProductDataObject> productList, Class<?> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException,
			WebApplicationException {

		Element xmlProducts = new Element("products");

		for (ProductDataObject product : productList) {
			xmlProducts.appendChild(convertProductDataObjectToXml(product));
		}

		Document doc = new Document(xmlProducts);

		Serializer serializer = new Serializer(entityStream);
		serializer.write(doc);

	}

	private Element convertProductDataObjectToXml(ProductDataObject product) {

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

		return xmlProduct;
	}

}
