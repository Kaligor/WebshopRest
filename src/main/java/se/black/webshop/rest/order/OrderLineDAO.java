package se.black.webshop.rest.order;


public class OrderLineDAO {

	public final Integer amount;
	public final String sku;
	public final String name;
	public final Long price;
	public final String description;
	public final String category;

	public OrderLineDAO(Integer amount, String sku, String name, Long price, String description, String category) {
		this.amount = amount;
		this.sku = sku;
		this.name = name;
		this.price = price;
		this.description = description;
		this.category = category;
	}
	
}
