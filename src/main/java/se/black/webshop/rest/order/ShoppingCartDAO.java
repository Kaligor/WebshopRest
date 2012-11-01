package se.black.webshop.rest.order;

import java.util.HashMap;
import java.util.Map;

import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

public class ShoppingCartDAO {

	public Map<String, Integer> cartlines = new HashMap<String, Integer>();

	public ShoppingCartDAO(Map<String, Integer> cartlines) {
		this.cartlines = cartlines;
	}

	public ShoppingCartDAO(Document doc) {
		Element root = doc.getRootElement();
		
		Element cart = root.getChildElements("cart").get(0);
		
		Element cartlines = cart.getChildElements("cartlines").get(0);
		Elements childlines = cartlines.getChildElements();
		
		for(int i = 0; i < childlines.size(); i++){
			Element line = childlines.get(i);
			Element sku = line.getChildElements("sku").get(0);
			Element amount = line.getChildElements("amount").get(0);
			this.cartlines.put(sku.getValue(), Integer.parseInt(amount.getValue()));
			
		}
		
	
	}
	
	
}
