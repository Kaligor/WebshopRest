package se.black.webshop.rest.category;

import java.util.ArrayList;
import java.util.List;

import nu.xom.Document;
import nu.xom.Element;

import se.black.webshop.model.product.Category;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class CategoryDAO {
	
	private String name;
	private String path;
	private List<CategoryDAO> children;
	
	public CategoryDAO(String name, String path, List<CategoryDAO> children) {
		this.name = name.toLowerCase();
		this.path = path;
		this.children = children;
	}
	
	public CategoryDAO(JsonElement jsonElement) {
		
		JsonObject jsonCategory = jsonElement.getAsJsonObject();
		this.name = jsonCategory.get("name").getAsString();
		
	}
	
	
	public CategoryDAO(Document xmlCategory) {
		Element root = xmlCategory.getRootElement();
		this.name = root.getChildElements("name").get(0).getValue();
	}
	
	
	public CategoryDAO(Category category) {
		this.name = category.getName().toLowerCase();
		this.path = category.getPath();
		this.children = new ArrayList<CategoryDAO>();
		for (Category child : category.getChildren()) {
			this.children.add(new CategoryDAO(child));
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}

	public List<CategoryDAO> getChildren() {
		return children;
	}

}
