package se.black.webshop.rest.order;

import java.util.ArrayList;
import java.util.Collection;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import se.black.webshop.model.order.Order;
import se.black.webshop.model.order.OrderLine;
import se.black.webshop.model.order.OrderedCustomer;

public class OrderDAO{

	public final Long orderno;
	public final String username;
	public final String street;
	public final String zip;
	public final String city;
	public final Collection<OrderLineDAO> orderlines;

	OrderDAO(Long orderno, OrderedCustomer orderingCustomer, Collection<OrderLine> orderlines) {
		this.orderno = orderno;
		this.username = orderingCustomer.getUsername();
		this.street = orderingCustomer.getDeliveryAddress().getStreet();
		this.zip = orderingCustomer.getDeliveryAddress().getZip();
		this.city = orderingCustomer.getDeliveryAddress().getCity();
		this.orderlines = new ArrayList<OrderLineDAO>();
		
		for(OrderLine line : orderlines){
			this.orderlines.add(new OrderLineDAO(line.getAmount(), line.getProduct().getSku(), line.getProduct().getName(), line.getProduct().getPrice(), line.getProduct().getDescription(), line.getProduct().getCategoryName()));
		}
	}

	public OrderDAO(Order order) {
		this.orderno = order.getOrderNo();
		this.username = order.getCustomer().getUsername();
		this.street = order.getCustomer().getDeliveryAddress().getStreet();
		this.zip = order.getCustomer().getDeliveryAddress().getZip();
		this.city = order.getCustomer().getDeliveryAddress().getCity();
		this.orderlines = new ArrayList<OrderLineDAO>();
		
		for(OrderLine line : order.getOrderLines()){
			this.orderlines.add(new OrderLineDAO(line.getAmount(), line.getProduct().getSku(), line.getProduct().getName(), line.getProduct().getPrice(), line.getProduct().getDescription(), line.getProduct().getCategoryName()));
		}
	}
	
	
	public OrderDAO(Document doc) {
		Element root = doc.getRootElement();
		
		Element customer = root.getChildElements("customer").get(0);
		Element username = customer.getChildElements("username").get(0);
		Element address = customer.getChildElements("address").get(0);
		Element street = address.getChildElements("street").get(0);
		Element zip = address.getChildElements("zip").get(0);
		Element city = address.getChildElements("city").get(0);
		
		Element orderno = root.getChildElements("orderno").get(0);
		Element xmlOrderlines = root.getChildElements("orderlines").get(0);
		Elements childLines = xmlOrderlines.getChildElements();

		this.orderlines = new ArrayList<OrderLineDAO>();
		for(int i = 0; i < childLines.size(); i++){
			Element line = childLines.get(i);
			Element product = line.getChildElements("product").get(0);
			Element amount = line.getChildElements("amount").get(0);
			
			Element sku = product.getChildElements("sku").get(0);
			Element name = product.getChildElements("name").get(0);
			Element price = product.getChildElements("price").get(0);
			Element descr = product.getChildElements("description").get(0);
			Element categ = product.getChildElements("category").get(0);
			this.orderlines.add(new OrderLineDAO(Integer.parseInt(amount.getValue()), sku.getValue(), name.getValue(), Long.parseLong(price.getValue()), descr.getValue(), categ.getValue()));
		}
		
		this.orderno = Long.parseLong(orderno.getValue());
		this.username = username.getValue();
		this.street = street.getValue();
		this.zip = zip.getValue();
		this.city = city.getValue();
		
	}
}
