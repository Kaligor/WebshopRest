package se.black.webshop.rest.tests;

import java.util.ArrayList;
import java.util.List;

public class TestUser {
	
	private String firstname;
	private String lastname;
	
	private List<String> orderedProdSkus = new ArrayList<String>();
	
	public TestUser(String firstname, String lastname, List<String> ids) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.orderedProdSkus = ids;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Override
	public String toString() {
		return "UserTest [firstname=" + firstname + ", lastname=" + lastname
				+ " orderedProductSkus="+orderedProdSkus +"]";
	}

	public List<String> getOrderedProdSkus() {
		return orderedProdSkus;
	}

	public void setOrderedProdSkus(List<String> orderedProdSkus) {
		this.orderedProdSkus = orderedProdSkus;
	}
	
	
	

}
