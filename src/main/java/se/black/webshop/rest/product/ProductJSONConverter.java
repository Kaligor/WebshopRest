package se.black.webshop.rest.product;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ProductJSONConverter implements JsonSerializer<ProductDataObject>, JsonDeserializer<ProductDataObject> {
	/**
	 * @Id
	 * @GeneratedValue(strategy = GenerationType.AUTO) private long Id;
	 * @Column(unique = true) private String sku; private String name; private
	 *                long price; private String description;
	 * @ManyToOne(fetch = FetchType.EAGER) private Category category;
	 * @ElementCollection(fetch = FetchType.EAGER)
	 * @CollectionTable(name = "product_attributes_table", joinColumns =
	 *                       @JoinColumn(name = "product_id"))
	 * @OrderColumn private Set<Attribute> attributes;
	 */

	@Override
	public ProductDataObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

		final JsonObject productAsJson = json.getAsJsonObject();

		String sku = productAsJson.get("sku").getAsString();
		String name = productAsJson.get("name").getAsString();
		Long price = productAsJson.get("price").getAsLong();
		String description = productAsJson.get("description").getAsString();
		String categoryPath = productAsJson.get("categoryPath").getAsString();
		return new ProductDataObject(sku, name, price, description, categoryPath);

	}

	@Override
	public JsonElement serialize(ProductDataObject src, Type typeOfSrc, JsonSerializationContext context) {

		final JsonObject productAsJson = new JsonObject();

		productAsJson.addProperty("sku", src.getSku());
		productAsJson.addProperty("name", src.getName());
		productAsJson.addProperty("price", src.getPrice());
		productAsJson.addProperty("description", src.getDescription());
		productAsJson.addProperty("categoryPath", src.getCategoryPath());
		return productAsJson;
	}

}
