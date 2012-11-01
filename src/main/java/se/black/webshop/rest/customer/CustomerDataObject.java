package se.black.webshop.rest.customer;

import nu.xom.Document;
import nu.xom.Element;
import se.black.webshop.model.account.Address;
import se.black.webshop.model.account.CustomerAccount;

public class CustomerDataObject {
	
	private String username;
	private String password;
	private Address address;
	
	CustomerDataObject(String username, String password, Address address) {
		this.username = username;
		this.password = password;
		this.address = address;
	}
	
	CustomerDataObject(String username, Address address) {
		this.username = username;
		this.address = address;
	}
	
	CustomerDataObject(CustomerAccount customer) {
		this.username = customer.getUsername();
		this.address = customer.getAddress();
	}
	
	public CustomerDataObject(Document xmlCustomer) {
		Element root = xmlCustomer.getRootElement();
		this.username = root.getChildElements("username").get(0).getValue();
		this.password = root.getChildElements("password").get(0).getValue();
		
		Element address = root.getChildElements("address").get(0);
		String street = address.getChildElements("street").get(0).getValue();
		String zip = address.getChildElements("zip").get(0).getValue();
		String city = address.getChildElements("city").get(0).getValue();
		this.address = new Address(street, city, zip);
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public Address getAddress() {
		return address;
	}
	
}
