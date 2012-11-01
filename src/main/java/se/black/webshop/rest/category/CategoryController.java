package se.black.webshop.rest.category;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.product.Category;
 

@Path("/category")
public class CategoryController {
	
	@Context 
	UriInfo uriInfo;

	// special method for getting root + children
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getRoot() throws NoSuchEntryException {
		CategoryDAO category = new CategoryDAO(Category.ROOT);
		return Response.ok(category).build();
	}
	
	@GET
	@Path("{path:.*}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getCategory(@PathParam("path") String path) throws NoSuchEntryException {
		path = "/"+path;
		CategoryDAO category = new CategoryDAO(Category.getCategory(path));
		return Response.ok(category).build();
	}
	
	
	@POST
	@Path("{path: .*}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response createCategory(@PathParam("path") String path, CategoryDAO newCategory) throws NoSuchEntryException, DuplicateEntryException {
		path = "/"+path;
		Category parent = Category.getCategory(path);
		parent.createSubcategory(newCategory.getName());
		
		
		return Response.created(UriBuilder.fromPath("/" + newCategory.getName()).build()).build();
	}

	// special method for creating sub-children of root
	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response createCategoryOnRoot(CategoryDAO newCategory) throws NoSuchEntryException, DuplicateEntryException {
		Category.ROOT.createSubcategory(newCategory.getName());
		
		return Response.created(UriBuilder.fromPath("/" + newCategory.getName()).build()).build();
	}
	
	
	@DELETE
	@Path("{path:.*}")
	public Response deleteCategory(@PathParam("path") String path) throws NoSuchEntryException, DuplicateEntryException, DatasourceException {
		Category categoryToRemove = Category.getCategory("/" + path);
		Category categoryParent = categoryToRemove.getParent();
		categoryParent.removeChild(categoryToRemove);
		
		return Response.status(Status.NO_CONTENT).build();
	}
	
	
	
	

}
