package se.black.webshop.rest.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

public abstract class Filterer<T> {
	
	protected abstract Map<String, List<String>> convertObjectToMap(T objectToConvert);
	
	public List<T> filter(MultivaluedMap<String, String> parameters, Collection<T> collectionToFilter) {
		if(parameters.containsKey("range")){
			Collection<T> tempList = new ArrayList<T>();
			tempList.addAll(collectionToFilter);
			List<String> rangeParams = parameters.get("range");
			
			for(String rangeParam : rangeParams){
				collectionToFilter = filterByParameter(parameters.get(rangeParam), rangeParam, collectionToFilter);
				parameters.remove(rangeParam);
			}
			
			parameters.remove("range");
			 
			return filterAbsolute(parameters, collectionToFilter);
		} else {
			return filterAbsolute(parameters, collectionToFilter);
		}
		
	}
	
	private List<T> filterAbsolute(MultivaluedMap<String, String> parameters, Collection<T> collectionToFilter){
		for(String parameter : parameters.keySet()){
			collectionToFilter = filterByParameter(parameter, parameters.get(parameter), collectionToFilter);
		}
			
		return new ArrayList<T>(collectionToFilter);
	}
	
	private List<T> filterByParameter(String parameterKey, List<String> parameterValues, Collection<T> listToFilter) {
		List<T> tempList = new ArrayList<T>();
		for(T element : listToFilter){
			Map<String, List<String>> elementAsMap = convertObjectToMap(element);
			
			for(String value : elementAsMap.get(parameterKey)){
				if(parameterValues.contains(value)){
					tempList.add(element);
				}
			}	
		}
		return tempList;
	}

	private List<T> filterByParameter(List<String> paramValues, String rangeParam, Collection<T> collectionToFilter){
		List<T> tempList = new ArrayList<T>();

		String lowest = "zzzzzzzzzzzzzz";
		String highest = "";
		Double lowestNum = Double.MAX_VALUE;
		Double highestNum = Double.MIN_VALUE;
		int valCounter = 0;
		 
		 
		
		for(String value : paramValues){
			if(value.matches(Regex.DIGITS_ONLY)){
				 
				valCounter++;
				Double tempVal = Double.parseDouble(value);
				if(tempVal <= lowestNum){
					lowestNum = tempVal;
					lowest = value;
				}
				if(tempVal >= highestNum){
					highestNum = tempVal;
					highest = value;
				}
			} else {
				 
				if(value.compareToIgnoreCase(lowest) <= 0){
					lowest = value;
				}
				if(value.compareToIgnoreCase(highest) >= 0){
					highest = value;
				}
			}
		}
		
		for(T element : collectionToFilter){
			Map<String, List<String>> objectAsMap = convertObjectToMap(element);
			List<String> objectValues = objectAsMap.get(rangeParam);
			 
			for(String objectValue : objectValues){
				if(paramValues.size() == valCounter && objectValue.matches(Regex.DIGITS_ONLY)){ //Only digits in the values
					 
					Double compareNum = Double.parseDouble(objectValue);
					if(lowestNum == highestNum){
						if(compareNum >= lowestNum){
							 
							tempList.add(element);
						}
					} else if(compareNum >= lowestNum && compareNum <= highestNum){
						 
						tempList.add(element);
					}
				} else {
					 
					if(lowest.equals(highest)){
						if(objectValue.compareTo(lowest) >= 0){
							 
							tempList.add(element);
						}
					} else if(objectValue.compareToIgnoreCase(lowest) >= 0 && objectValue.compareToIgnoreCase(highest) <= 0){
						 
						tempList.add(element);
					}
				}
			}
			 
		} 
		
		return tempList;
	}


}