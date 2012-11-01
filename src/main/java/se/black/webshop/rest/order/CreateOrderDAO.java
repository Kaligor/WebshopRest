package se.black.webshop.rest.order;

import se.black.webshop.rest.customer.CustomerDataObject;

public class CreateOrderDAO {

	public final ShoppingCartDAO cart;
	public final CustomerDataObject customer;
	
	public CreateOrderDAO(ShoppingCartDAO cart, CustomerDataObject customer) {
		this.cart = cart;
		this.customer = customer;
	}
}
