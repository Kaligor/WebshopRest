package se.black.webshop.rest.customer;

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

import se.black.webshop.model.account.AccountManager;
import se.black.webshop.model.account.CustomerAccount;
import se.black.webshop.model.account.JPAAccountManager;
import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.ManagerException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.rest.product.ProductDataObject;
import se.black.webshop.rest.util.Filterer;



@Path("customer/")
public class CustomerController {
	
	@Context
	private UriInfo uriInfo;
	private AccountManager accountManager = new JPAAccountManager();
	
	private Filterer<CustomerAccount> customerFilterer = new Filterer<CustomerAccount>() {

		@Override
		protected Map<String, List<String>> convertObjectToMap(CustomerAccount customer) {
			
			Map<String, List<String>> customerMap = new HashMap<String, List<String>>();
			
			//TODO: gor et utilmetod som fixar in en String o returnerar en List<String>
			customerMap.put("username", Arrays.asList(customer.getUsername().toLowerCase()));
			customerMap.put("city", Arrays.asList(customer.getAddress().getCity().toLowerCase()));
			customerMap.put("street", Arrays.asList(customer.getAddress().getStreet().toLowerCase()));
			customerMap.put("zip", Arrays.asList(customer.getAddress().getZip().toLowerCase()));
			
			return customerMap;
		}
	};
	
	
	@GET
	@Path("/{username}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getSingleCustomer(@PathParam("username") String username) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		return Response.ok(new CustomerDataObject(accountManager.getCustomer(username))).build();
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getCustomers() throws DatasourceException, ManagerException {
		
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		Collection<CustomerAccount> allCustomers = accountManager.getAllCustomers();
		
		allCustomers = customerFilterer.filter(queryParams, allCustomers);
		
		Collection<CustomerDataObject> allCustomersAsDAO = new ArrayList<CustomerDataObject>();
		for (CustomerAccount customer : allCustomers) {
			allCustomersAsDAO.add(new CustomerDataObject(customer));
		}
		
		 GenericEntity<Collection<CustomerDataObject>> entity =
				    new GenericEntity<Collection<CustomerDataObject>>(allCustomersAsDAO) {};
		
		return Response.ok(entity).build();
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response createCustomer(CustomerDataObject customer) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		
		CustomerAccount newCustomer = accountManager.createCustomer(customer.getUsername(), 
				customer.getPassword(),
				customer.getAddress().getStreet(), 
				customer.getAddress().getCity(), 
				customer.getAddress().getZip());
		
		return Response.created(UriBuilder.fromPath(newCustomer.getUsername()).build()).build();
	}
	
	@PUT
	@Path("/{username}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response updateCustomer(@PathParam("username") String username, CustomerDataObject customer) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException {
		
		if (!username.toLowerCase().equals(customer.getUsername().toLowerCase())) {
			return Response.status(Status.BAD_REQUEST).entity("{\"errormessage\":\"username in url and json doesn't match\"}").build();
		}
		
		CustomerAccount oldCustomer = accountManager.getCustomer(username);
		CustomerAccount updatedCustomer = oldCustomer.update(customer.getPassword(), customer.getAddress());
		accountManager.updateCustomer(updatedCustomer);

		return Response.status(Status.NO_CONTENT).build();
		
	}
	
	@DELETE
	@Path("/{username}")
	public Response deleteCustomer(@PathParam("username") String username) throws DuplicateEntryException, ManagerException {
		accountManager.deleteCustomer(username);
		return Response.status(Status.NO_CONTENT).build();
	}
	

}
