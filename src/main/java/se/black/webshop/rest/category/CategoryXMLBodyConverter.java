package se.black.webshop.rest.category;

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
public class CategoryXMLBodyConverter implements MessageBodyWriter<CategoryDAO>, MessageBodyReader<CategoryDAO> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type.isAssignableFrom(CategoryDAO.class);
	}

	// -- Konverterar TILL xml ------
	
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
		
		Element xmlCategory = convertCategoryToXml(category);
		
		Document doc = new Document(xmlCategory);
		Serializer serializer = new Serializer(entityStream);
		serializer.write(doc);
		
	}
	
	private Element convertCategoryToXml(CategoryDAO category) {
		
		Element xmlCategory = new Element("category");
		xmlCategory.addAttribute(new Attribute("id", category.getPath()));
		
		Element name = new Element("name");
		name.appendChild(category.getName());
		xmlCategory.appendChild(name);
		
		Element path = new Element("path");
		path.appendChild(category.getPath());
		xmlCategory.appendChild(path);

		Element children = new Element("children");
		
		for (CategoryDAO child : category.getChildren()) {
			children.appendChild(convertCategoryToXml(child));
		}
		
		xmlCategory.appendChild(children);
		return xmlCategory;
	}

	// ----- Konverterar FRÅN xml --------
	
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

		Builder builder = new Builder();
		Document doc = null;

		try {
			doc = builder.build(entityStream);
		} catch (ValidityException e) {
			throw new BadXMLException(e.getMessage());
		} catch (ParsingException e) {
			throw new BadXMLException(e.getMessage());
		}
		
		return new CategoryDAO(doc);
	}
}
