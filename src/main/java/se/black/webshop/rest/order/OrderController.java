package se.black.webshop.rest.order;

import java.lang.reflect.Method;
import java.net.URI;
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
import javax.ws.rs.core.UriInfo;

import se.black.webshop.model.account.AccountManager;
import se.black.webshop.model.account.CustomerAccount;
import se.black.webshop.model.account.JPAAccountManager;
import se.black.webshop.model.exception.DatasourceException;
import se.black.webshop.model.exception.DuplicateEntryException;
import se.black.webshop.model.exception.ManagerException;
import se.black.webshop.model.exception.NoSuchEntryException;
import se.black.webshop.model.order.JPAOrderManager;
import se.black.webshop.model.order.Order;
import se.black.webshop.model.order.OrderLine;
import se.black.webshop.model.order.OrderManager;
import se.black.webshop.model.product.JPAProductManager;
import se.black.webshop.model.product.Product;
import se.black.webshop.model.product.ProductManager;
import se.black.webshop.model.shoppingcart.ShoppingCart;
import se.black.webshop.rest.customer.CustomerDataObject;
import se.black.webshop.rest.util.Filterer;
import se.black.webshop.rest.wrapper.Resource;

@Path("/order")
public class OrderController {

	@Context
	private UriInfo uriInfo;
	private static OrderManager orderManager = new JPAOrderManager();
	private static AccountManager accountManager = new JPAAccountManager();
	private static ProductManager productManager = new JPAProductManager();
	private static Filterer<Order> orderFilter = new Filterer<Order>() {

		@Override
		protected Map<String, List<String>> convertObjectToMap(Order objectToConvert) {
			HashMap<String, List<String>> orderMap = new HashMap<String, List<String>>();
			orderMap.put("orderdate", Arrays.asList(String.valueOf(objectToConvert.getOrderDate().getTime())));
			orderMap.put("totalprice", Arrays.asList(objectToConvert.getTotalPrice().toString()));
			orderMap.put("username", Arrays.asList(objectToConvert.getCustomer().getUsername()));
			orderMap.put("orderno", Arrays.asList(objectToConvert.getOrderNo().toString()));

			List<String> productAmounts = new ArrayList<String>();
			for(OrderLine orderLine : objectToConvert.getOrderLines()){
				productAmounts.add(orderLine.getAmount().toString());
			}
			orderMap.put("productamount", productAmounts);
			
			List<String> productPrices = new ArrayList<String>();
			for(OrderLine orderLine : objectToConvert.getOrderLines()){
				productPrices.add(orderLine.getProduct().getPrice().toString());
			}
			orderMap.put("productprice", productPrices);

			List<String> productSkus = new ArrayList<String>();
			for(OrderLine orderLine : objectToConvert.getOrderLines()){
				productSkus.add(orderLine.getProduct().getSku());
			}
			orderMap.put("productsku", productSkus);
			
			return orderMap;
		}
	}; 
	
	/* JSON to create order
	 * {
  "cart" : {
    "cartlines" : {"sku":"2"}
  },
  "customer" : {
    "address" : {"street":"gatan", "zip":"123", "city":"staden"},
    "username" : "Kalle",
    "password" : "pass"
  }
}
	 */
	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response createOrder(CreateOrderDAO createOrder) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException, SecurityException, NoSuchMethodException{
		CustomerDataObject customer = createOrder.customer;
		ShoppingCartDAO shoppingCart = createOrder.cart;
		
		CustomerAccount account = accountManager.getCustomer(customer.getUsername());
		ShoppingCart cart = new ShoppingCart();
		
		for(String sku : shoppingCart.cartlines.keySet()){
			Product product = productManager.getProductBySku(sku);
			Integer amount = shoppingCart.cartlines.get(sku);
			cart.addProduct(product, amount); 
		}
		Order order = orderManager.createOrder(cart, account);
		Method getOrder = OrderController.class.getMethod("getOrder", Long.class);
		return Response.created(uriInfo.getAbsolutePathBuilder().path(getOrder).build(order.getOrderNo().toString())).build();
	}
	
	@GET
	@Path("{orderno}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getOrder(@PathParam("orderno") Long orderNo) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException{
		OrderDAO order = new OrderDAO(orderManager.getOrderByOrderNo(orderNo));
		return Response.ok(order).build();
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getAllOrders() throws SecurityException, NoSuchMethodException{
		Collection<Order> allOrders = orderManager.getAllOrders();
		List<Resource> resourceURIs = new ArrayList<Resource>();
		Collection<OrderDAO> returnOrders = new ArrayList<OrderDAO>();
		
		MultivaluedMap<String,String> parameters = uriInfo.getQueryParameters();
		allOrders = orderFilter.filter(parameters, allOrders);
		
		List<String> location = parameters.get("location");
		
		boolean returnLocations;
		if(location == null){
			returnLocations = false;
		} else{
			returnLocations = location.contains("true");
		}		

		for(Order order : allOrders){
			if(returnLocations){
				Method getOrder = OrderController.class.getMethod("getOrder", Long.class);
				URI uri = uriInfo.getAbsolutePathBuilder().path(getOrder).build(order.getOrderNo().toString());
				resourceURIs.add(new Resource(uri));
			} else {
				returnOrders.add(new OrderDAO(order));
			}
		}
		if(returnLocations){
			return Response.ok(new GenericEntity<List<Resource>>(resourceURIs){}).build();
		} else {
			
			return Response.ok(new GenericEntity<Collection<OrderDAO>>(returnOrders) {}).build();
		}
	}
	
	@PUT
	@Path("{orderno}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response updateOrder(@PathParam("orderno") Long orderNo, OrderDAO order) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException{
		Order oldOrder = orderManager.getOrderByOrderNo(orderNo);
		Order semiupdatedOrder = oldOrder.updateAddress(order.street, order.zip, order.city);
		Collection<OrderLine> newOrderLines = new ArrayList<OrderLine>();
		
		for(OrderLineDAO line : order.orderlines){
			OrderLine oldLine = semiupdatedOrder.getOrderlineWithProduct(productManager.getProductBySku(line.sku));
			newOrderLines.add(oldLine.update(oldLine.getProduct(), line.amount));
		}

		Order updatedOrder = semiupdatedOrder.update(semiupdatedOrder.getCustomer(), newOrderLines);
		
		orderManager.updateOrder(updatedOrder);
		return Response.status(Status.NO_CONTENT).build();
	}
	
	@DELETE
	@Path("{orderno}")
	public Response deleteOrder(@PathParam("orderno") Long orderNo) throws DuplicateEntryException, NoSuchEntryException, DatasourceException, ManagerException{
		Order order = orderManager.getOrderByOrderNo(orderNo);
		orderManager.deleteOrder(order);
		return Response.status(Status.NO_CONTENT).build();
	}
}
