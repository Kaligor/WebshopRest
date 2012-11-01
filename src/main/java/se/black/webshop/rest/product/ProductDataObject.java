package se.black.webshop.rest.product;

import nu.xom.Document;
import nu.xom.Element;
import se.black.webshop.model.account.Address;

public class ProductDataObject{

	private String categoryPath;
	private String name;
	private String description;
	private long price;
	private String sku;
	
	@SuppressWarnings("unused")
	private ProductDataObject(){}
	
	public ProductDataObject(String sku, String name, long price, String description, String categoryPath) {
		this.sku = sku; //
		this.categoryPath = categoryPath;
		this.price = price; //
		this.description = description; //
		this.name = name; //
		
	}
	
	public ProductDataObject(Document xmlProduct){
		Element root = xmlProduct.getRootElement();
		
		this.sku = root.getChildElements("sku").get(0).getValue();
		this.name = root.getChildElements("name").get(0).getValue();
		this.price = Long.parseLong(root.getChildElements("price").get(0).getValue());
		this.description = root.getChildElements("description").get(0).getValue();
		this.categoryPath = root.getChildElements("categoryPath").get(0).getValue();
	}
	

	public String getCategoryPath() {
		return categoryPath;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public long getPrice() {
		return price;
	}
	public String getSku(){
		return sku;
	}

}
