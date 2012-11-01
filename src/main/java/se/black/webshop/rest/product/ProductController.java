package se.black.webshop.rest.product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.product.Category;
import se.black.webshop.model.product.JPAProductManager;
import se.black.webshop.model.product.Product;
import se.black.webshop.model.product.ProductManager;
import se.black.webshop.rest.util.Filterer;


@Path("/product")
public class ProductController {

	@Context
	UriInfo uriInfo;
	ProductManager productManager = new JPAProductManager();
	
	private Filterer<Product> productFilterer = new Filterer<Product>() {

		@Override
		protected Map<String, List<String>> convertObjectToMap(Product product) {
			Map<String, List<String>> productMap = new HashMap<String, List<String>>();
			
			productMap.put("sku", Arrays.asList(product.getSku()));
			productMap.put("name", Arrays.asList(product.getName()));
			productMap.put("price", Arrays.asList(Long.toString(product.getPrice())));
			productMap.put("description", Arrays.asList(product.getDescription()));
			productMap.put("category_id", Arrays.asList(Long.toString(product.getCategory().getID())));
			return productMap;
		}
		
	};
	
	@GET
	@Path("{sku}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getProductBySku(@PathParam("sku") String sku) throws DatasourceException, DuplicateEntryException, NoSuchEntryException{
		
		Product product = productManager.getProductBySku(sku);
		ProductDataObject productDataObject = new ProductDataObject(product.getSku(), product.getName(), product.getPrice(), product.getDescription(), product.getCategory().getPath());
		return Response.ok(productDataObject).build();
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response createProduct(final ProductDataObject productDataObject) throws DatasourceException, DuplicateEntryException, NoSuchEntryException{
		
		Category category = Category.getCategory(productDataObject.getCategoryPath());
		
		Product product = productManager.createProduct(
				productDataObject.getSku(), 
				productDataObject.getName(),
				productDataObject.getPrice(),
				productDataObject.getDescription(),
				category);
		return Response.created(UriBuilder.fromPath(product.getSku()).build()).build();
	}
	
	@PUT
	@Path("{sku}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response updateProductBySku(@PathParam("sku") String sku, final ProductDataObject productDataObject) throws DatasourceException, NoSuchEntryException{

		Product product = productManager.getProductBySku(sku);
		Category category = Category.getCategory(productDataObject.getCategoryPath());
		

		product = product.update(
				productDataObject.getName(), 
				productDataObject.getPrice(), 
				productDataObject.getDescription(),
				category);
		
		 
		
		productManager.updateProduct(product);
		 
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("{sku}")
	public Response deleteProductBySku(@PathParam("sku") String sku) throws DatasourceException, NoSuchEntryException{
		
		Product product = productManager.getProductBySku(sku);
		productManager.deleteProduct(product);
		return Response.status(Status.NO_CONTENT).build();	
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getAllProducts() throws DatasourceException{

		Collection<Product> allProducts = productManager.getAllProducts();
		
		MultivaluedMap<String,String> parameters = uriInfo.getQueryParameters();
		
		List<Product> filteredProducts = productFilterer.filter(parameters, allProducts);
		List<ProductDataObject> productDataObjects = new ArrayList<ProductDataObject>();
		
		for(Product product: filteredProducts){
			ProductDataObject productDataObject = new ProductDataObject(
					product.getSku(),
					product.getName(),
					product.getPrice(),
					product.getDescription(),
					product.getCategory().getPath());
			productDataObjects.add(productDataObject);
		}
		
		 GenericEntity<Collection<ProductDataObject>> entity =
				    new GenericEntity<Collection<ProductDataObject>>(productDataObjects) {};
		
		return Response.ok(entity).build();
	}
	
	@GET
	@Path("/bycategory/{path:.*}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getByCategory(@PathParam("path") String path) throws NoSuchEntryException, DatasourceException {
		
		Category category = Category.getCategory("/" + path);
		Collection<Product> productsInCategory = productManager.getProductsByCategory(category);
		
		MultivaluedMap<String,String> parameters = uriInfo.getQueryParameters();
		
		List<Product> filteredProducts = productFilterer.filter(parameters, productsInCategory);
		List<ProductDataObject> productDataObjects = new ArrayList<ProductDataObject>();
		
		for(Product product: filteredProducts){
			ProductDataObject productDataObject = new ProductDataObject(
					product.getSku(),
					product.getName(),
					product.getPrice(),
					product.getDescription(),
					product.getCategory().getPath());
			productDataObjects.add(productDataObject);
		}
		
		 GenericEntity<Collection<ProductDataObject>> entity =
				    new GenericEntity<Collection<ProductDataObject>>(productDataObjects) {};
		
		return Response.ok(entity).build();
		
	}
	
}
