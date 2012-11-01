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

import se.black.webshop.model.product.Product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class ProductManyJSONProvider implements MessageBodyWriter<List<ProductDataObject>> {

	public final static Gson gson;

	static {
		gson = new GsonBuilder().registerTypeAdapter(Product.class, new ProductJSONConverter()).create();
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		
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
	public long getSize(List<ProductDataObject> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(List<ProductDataObject> productList, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
		
		entityStream.write(gson.toJson(productList).getBytes());		
		
	}

}
