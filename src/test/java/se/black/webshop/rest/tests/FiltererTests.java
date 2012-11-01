package se.black.webshop.rest.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Test;

import se.black.webshop.rest.util.Filterer;

import com.sun.jersey.core.util.MultivaluedMapImpl;

public class FiltererTests {

	@Test
	public void canFilterUsers() {
		
		Filterer<TestUser> userFilterer = new Filterer<TestUser>() {
			
			@Override
			protected Map<String, List<String>> convertObjectToMap(
					TestUser objectToConvert) {
				
				Map<String, List<String>> userMap = new HashMap<String, List<String>>();
				
				List<String> firstNameList = new ArrayList<String>();
				firstNameList.add(objectToConvert.getFirstname());
				userMap.put("firstname", firstNameList);
				
				
				List<String> lastNameList = new ArrayList<String>();
				lastNameList.add(objectToConvert.getLastname());
				userMap.put("lastname",lastNameList);
				userMap.put("productsku", objectToConvert.getOrderedProdSkus());
				return userMap;
			}
			
		};
		
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("firstname", "Pelle");
		queryParams.add("firstname", "John");
		queryParams.add("lastname", "Norum");
		queryParams.add("productsku", "sill1");
		
		List<String> pellesIds = new ArrayList<String>();
		pellesIds.add("sill1");
		
		List<String> bengtsIds = new ArrayList<String>();
		bengtsIds.add("sill2");
		
		List<String> johnsIds = new ArrayList<String>();
		johnsIds.add("sill1");
		
		
		List<TestUser> users = new LinkedList<TestUser>();
		users.add(new TestUser("Pelle", "Persson", pellesIds));
		users.add(new TestUser("Bengt", "Persson", bengtsIds));
		users.add(new TestUser("Bengt", "Andersson", bengtsIds));
		users.add(new TestUser("John", "Norum", johnsIds));
		
		 
		users = userFilterer.filter(queryParams, users);
		 
		assertTrue(users.get(0).getOrderedProdSkus().contains("sill1"));
		assertEquals("John", users.get(0).getFirstname());
		assertEquals(1, users.size());
	}

	@Test
	public void canFilterRange(){
		
		Filterer<TestUser> filter = new Filterer<TestUser>() {

			@Override
			protected Map<String, List<String>> convertObjectToMap(
					TestUser objectToConvert) {
				Map<String, List<String>> object = new HashMap<String, List<String>>();
				List<String> firstname = new ArrayList<String>();
				List<String> lastname = new ArrayList<String>();
				List<String> number = new ArrayList<String>();
				
				firstname.add(objectToConvert.getFirstname());
				lastname.add(objectToConvert.getLastname());
				number.addAll(objectToConvert.getOrderedProdSkus());
				
				object.put("firstname", firstname);
				object.put("lastname", lastname);
				object.put("number", number);
				return object;
			}
		};

	
		TestUser anders = new TestUser("Anders", "Andersson", Arrays.asList("50"));
		TestUser bengt = new TestUser("Bengt", "Bengtsson", Arrays.asList("200"));
		TestUser carl = new TestUser("Carl", "Carlsson", Arrays.asList("100"));
		TestUser david = new TestUser("David", "Davidsson", Arrays.asList("150"));
		TestUser erik = new TestUser("Erik", "Eriksson", Arrays.asList("75"));
		TestUser fredrik = new TestUser("Fredrik", "Fredriksson", Arrays.asList("300"));
		TestUser gustav = new TestUser("Gustav", "Gustavsson", Arrays.asList("500"));
		TestUser henrik = new TestUser("Henrik", "Henriksson", Arrays.asList("1000"));
		
		Collection<TestUser> users = new ArrayList<TestUser>();
		users.add(anders);
		users.add(bengt);
		users.add(carl);
		users.add(david);
		users.add(erik);
		users.add(fredrik);
		users.add(gustav);
		users.add(henrik);
		/*
		 * Can filter on 2 range-params
		 * Can filter on 1 range-param
		 * Can filter on 2 range-params and absolute params
		 * Can filter on 1 range-param and absolute params
		 */
		MultivaluedMap<String, String> parameters = new MultivaluedMapImpl();
//		parameters.add("firstname", "Carl");
		parameters.add("firstname", "Anders");
		parameters.add("firstname", "Fredrik");
		parameters.add("number", "100");
		parameters.add("number", "500");
		parameters.add("range", "number");
//		parameters.add("range", "firstname");
		
		List<TestUser> filteredusers = filter.filter(parameters, users);
		
		 
	
	
	
	}

}
