import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RentalCarsJSONTasks {

	@SuppressWarnings({ "unchecked" })
	public static void main(String[] args) {
		
		JSONParser parser = new JSONParser(); // parser for JSON string
		String JSONString = new String(); // string to store JSON file
		
		try {
			
			URL url = new URL("http://www.rentalcars.com/js/vehicles.json"); // set URL as one given to get JSON file
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // open connection to given URL
			conn.setRequestMethod("GET"); // send GET request
			conn.setRequestProperty("Accept", "application/json"); // accept .json from connection

			if (conn.getResponseCode() != 200) { // if response code from server is not 200
				throw new RuntimeException("Failed : HTTP error code : " // throw exception and print that it has not connected
						+ conn.getResponseCode()); // + give response code
			}

			BufferedReader br = new BufferedReader(new InputStreamReader( // create new buffered reader
				(conn.getInputStream()))); // from the current open connection

			String output; // new string to get output from server
			while ((output = br.readLine()) != null) { // while next line is not empty
				JSONString = JSONString + output; // set the JSON string to include the line
			}

			conn.disconnect(); // disconnect from server

		  } catch (MalformedURLException e) {

			e.printStackTrace(); // catch malformed url exception error and print stack trace

		  } catch (IOException e) {

			e.printStackTrace(); // catch IO exception error and print stack trace

		  }
		try {
			
			JSONObject objectJSON = (JSONObject) parser.parse(JSONString); // parse the JSON string from the URL
			
			JSONObject currentVehicle = new JSONObject(); // create new JSON object to store current vehicle
			JSONObject search = (JSONObject) objectJSON.get("Search"); // get the search JSON object
			
			JSONArray vehicleList = (JSONArray) search.get("VehicleList"); // get the JSON array vehicle list from search
			JSONArray sortedVehicleListPrice = new JSONArray(); // create new array which will contain sorted vehicle list by price
			
			sortedVehicleListPrice = sortJSONArrayAscending((JSONArray) vehicleList.clone(), "price"); // call the sortJSONArrayAscending method on a clone of the initial list with the price as the parameter
			
			Iterator<JSONObject> sortedIterator = sortedVehicleListPrice.iterator(); // create iterator to iterate over sorted array
			int j = 1; // new int for printing numbers
			while(sortedIterator.hasNext()) { // while there is a next in the array
				currentVehicle = sortedIterator.next(); // set current vehicle as the next vehicle in array
				System.out.println(j + ". " + currentVehicle.get("name") + " - " + currentVehicle.get("price")); // print number, name anbd price
				j++; // increment int
			}
			
			System.out.println("\n");
			
			String carType = new String(), carDoorType = new String(), transmission = new String(), airCon = new String(), fuel = new String(), currentSIPP = new String();
			// string to store current cartype, cardoortype, transmission type, aircon and fuel type
			Iterator<JSONObject> sippIterator = vehicleList.iterator(); // iterator to iterate over original vehicle list
			
			j = 1; // int to print numbers again
			while(sippIterator.hasNext()) { // while vehicle list has next member
				currentVehicle = sippIterator.next(); // set current vehicle as next vehicle
				
				currentSIPP = (String) currentVehicle.get("sipp"); // set currentSIPP as the sipp of the current vehicle
				
				carType = getType(currentSIPP.charAt(0)); // get car type
				carDoorType = getDoorType(currentSIPP.charAt(1)); // get door type
				transmission = getTransmission(currentSIPP.charAt(2)); // get transmission type
				fuel = getFuel(currentSIPP.charAt(3)); // get fuel type
				airCon = getAirCon(currentSIPP.charAt(3)); // get aircon
					
				System.out.println(j + ". " + currentVehicle.get("name") + " - " + currentVehicle.get("sipp") + " - " + carType + " - " + carDoorType + " - " + transmission + " - " + fuel + " - " + airCon);
				j++; // print all info and increment number
			}
			
			System.out.println("");
			
			JSONArray sortedVehicleArrayByRatingAscending = sortJSONArrayAscending((JSONArray) vehicleList.clone(), "rating");
			// sort clone of initial array by rating
			List<JSONObject> vehicleListSortedByRating = new ArrayList<JSONObject>(); // make new array list 
			Iterator<JSONObject> iteratorOfSortedVehicleArray = sortedVehicleArrayByRatingAscending.iterator(); // iterator for vehicle array sorted by rating
			
			while(iteratorOfSortedVehicleArray.hasNext()) { // while iterator has next member
				currentVehicle = iteratorOfSortedVehicleArray.next(); // set current vehicle as current vehicle
				vehicleListSortedByRating.add(currentVehicle); // add current vehicle to list sorted by rating
			}
			
			Collections.reverse(vehicleListSortedByRating); // reverse the list
			
			List<JSONObject> topVehicleSortedByRating = new ArrayList<JSONObject>(); // make new list to hold top rated vehicle
			
			topVehicleSortedByRating = getTopVehicleByRating(vehicleListSortedByRating); // call to get the top vehicle
			
			Iterator<JSONObject> iteratorOfSortedVehicleList = topVehicleSortedByRating.iterator(); // iterator for top vehicle by rating array
			int k = 1; // int for printing
			while(iteratorOfSortedVehicleList.hasNext()) { // while iterator of sorted list has next
				currentVehicle = iteratorOfSortedVehicleList.next(); // current vehicle is next member
				currentSIPP = (String) currentVehicle.get("sipp"); // current sipp is current vehicles sipp
				
				System.out.println(k + ". " + currentVehicle.get("name") + " - " + getType(currentSIPP.charAt(0)) + " - " + currentVehicle.get("supplier") + " - " + currentVehicle.get("rating"));
				k++; // print details and increment number
			}
			
			System.out.println("");
			
			JSONArray vehicleArrayWithScore = new JSONArray(); // create new array to store scored vehicles
			
			double vehicleScore; // new double to store vehicle score
			double totalScore; // new double to store total score
			String rating; // new string to hold rating
			double ratingDouble; // new double to hold rating as double
			
			Iterator<JSONObject> vehicleListIterator = vehicleList.iterator(); // new iterator to iterate over vehicle list
			
			while(vehicleListIterator.hasNext()) { // while there is a next vehicle
				vehicleScore = 0; // initialise vehicle score
				currentVehicle = vehicleListIterator.next(); // set current vehicle as next member
				currentSIPP = (String) currentVehicle.get("sipp"); // set current sipp as sipp of current vehicle
				
				if(getTransmission(currentSIPP.charAt(2)) == "Manual"){ // if transmission is manual,
					vehicleScore = vehicleScore + 1; // add 1 to score 
				}
				else{
					vehicleScore = vehicleScore + 5; // else add 5 (since then it is automatic)
				}
				
				if(getAirCon(currentSIPP.charAt(3)) == "AC"){ // if car has AC
					vehicleScore = vehicleScore + 2; // add 2 to score
				}
				
				rating = (String) currentVehicle.get("rating").toString(); // get rating as string
				ratingDouble = Double.parseDouble(rating); // convert string to double
				totalScore = ratingDouble + vehicleScore; // add total score and rating
				
				currentVehicle.put("vehicle score", vehicleScore); // set "vehicle score" of current vehicle to vehicleScore
				currentVehicle.put("total score", totalScore);// set "total score" of current vehicle to totalScore
				
				vehicleArrayWithScore.add(currentVehicle); // add this JSON object (current vehicle) to array
			}
			
			JSONArray vehicleArraySortedByScoreTotalScore = sortJSONArrayAscending((JSONArray) vehicleArrayWithScore.clone(), "total score");
			// sort the array by score
			JSONArray vehicleArrayScoreDescending = new JSONArray(); // make new array to store it descending
			
			for(int i = vehicleArraySortedByScoreTotalScore.size()-1; i >= 0; i--){
				vehicleArrayScoreDescending.add(vehicleArraySortedByScoreTotalScore.get(i)); // add each member of the ascending array into the new array from finish to start
			}
			
			Iterator<JSONObject> scoreIterator = vehicleArrayScoreDescending.iterator(); // iterator of vehicle array stored descending by score
			
			j = 1;
			while(scoreIterator.hasNext()) { // while iterator has next member
				currentVehicle = scoreIterator.next(); // current vehicle is next member
				System.out.println(j + ". " + currentVehicle.get("name") + " - " + currentVehicle.get("vehicle score") + " - " +  currentVehicle.get("rating") + " - " + currentVehicle.get("total score"));
				j++; // print details and increment number
			}
			
		}
		
		catch (Exception e) { // catch any exception
			e.printStackTrace(); // print stack trace
		}
	}
	
	 // Creates a List of JSON Objects which are the highest rated of each vehicle type
	 // return topVehicleSortedByRating, JSONObject List
	private static List<JSONObject> getTopVehicleByRating(List<JSONObject> vehicleListSortedByRating) {
		int[] carTypeCheck = new int[9]; // since there are 9 carTypes, this stores 9 ints to use as checks
		JSONObject currentVehicle = new JSONObject(); // create current vehicle object
		String currentSIPP = new String(); // create current sipp string
		List<JSONObject> topVehicleSortedByRating = new ArrayList<JSONObject>(); // create new array to add the top vehicles into
		
		for(int i = 0; i < vehicleListSortedByRating.size(); i++) { // for every vehicle in array
			currentVehicle = vehicleListSortedByRating.get(i); // get the current one
			currentSIPP = (String) currentVehicle.get("sipp"); // get the current sipp
			
			switch(currentSIPP.charAt(0)) { // switch statement on first letter
			case 'M' :
				if(carTypeCheck[0] != 1) { // make sure no current top rated vehicle of same type
					topVehicleSortedByRating.add(currentVehicle); // add vehicle to list
					carTypeCheck[0] = 1; // set this check to 1
				}
				break;
			case 'E' :
				if(carTypeCheck[1] != 1) {
					topVehicleSortedByRating.add(currentVehicle);
					carTypeCheck[1] = 1;
				}
				break;
			case 'C' :
				if(carTypeCheck[2] != 1) {
					topVehicleSortedByRating.add(currentVehicle);
					carTypeCheck[2] = 1;
				}
				break;
			case 'I' :
				if(carTypeCheck[3] != 1) {
					topVehicleSortedByRating.add(currentVehicle);
					carTypeCheck[3] = 1;
				}
				break;
			case 'S' :
				if(carTypeCheck[4] != 1) {
					topVehicleSortedByRating.add(currentVehicle);
					carTypeCheck[4] = 1;
				}
				break;
			case 'F' :
				if(carTypeCheck[5] != 1) {
					topVehicleSortedByRating.add(currentVehicle);
					carTypeCheck[5] = 1;
				}
				break;
			case 'P' :
				if(carTypeCheck[6] != 1) {
					topVehicleSortedByRating.add(currentVehicle);
					carTypeCheck[6] = 1;
				}
				break;
			case 'L' :
				if(carTypeCheck[7] != 1) {
					topVehicleSortedByRating.add(currentVehicle);
					carTypeCheck[7] = 1;
				}
				break;
			case 'X' :
				if(carTypeCheck[8] != 1) {
					topVehicleSortedByRating.add(currentVehicle);
					carTypeCheck[8] = 1;
				}
				break;
			default :
			}
		}
		return topVehicleSortedByRating; // return array of top rated vehicles
		
	}

	// Gets air con value as string using given char
	private static String getAirCon(char sipp) {
		String airCon = new String();
		if(sipp == 'N') {
			airCon = "no AC";
		}
		else {
			airCon = "AC";
		}
		return airCon;
	}

	// Gets fuel value as string based on given char
	private static String getFuel(char sipp) {
		String fuel = new String();
		if(sipp == 'N') {
			fuel = "Petrol";
		}
		else {
			fuel = "Petrol";
		}
		return fuel;
	}

	// Gets transmission value as string based on given char
	private static String getTransmission(char sipp) {
		String transmission = new String();
		if(sipp == 'M') {
			transmission = "Manual";
		}
		else {
			transmission = "Automatic";
		}
		return transmission;
	}

	// Gets car door type as string based on given char
	private static String getDoorType(char sipp) {
		String carDoorType = new String();
		switch(sipp) {
		case 'B' :
			carDoorType = "2 doors";
			break;
		case 'C' :
			carDoorType = "4 doors";
			break;
		case 'D' :
			carDoorType = "5 doors";
			break;
		case 'W' :
			carDoorType = "Estate";
			break;
		case 'T' :
			carDoorType = "Convertible";
			break;
		case 'F' :
			carDoorType = "SUV";
			break;
		case 'P' :
			carDoorType = "Pick up";
			break;
		case 'V' :
			carDoorType = "Passenger van";
			break;
		default :
			carDoorType = "Not found";
		}
		return carDoorType;
	}

	// Gets car type as string based on given char
		public static String getType(char sipp) {
			String carType = new String();
			switch(sipp) {
			case 'M' :
				carType = "Mini";
				break;
			case 'E' :
				carType = "Economy";
				break;
			case 'C' :
				carType = "Compact";
				break;
			case 'I' :
				carType = "Intermediate";
				break;
			case 'S' :
				carType = "Standard";
				break;
			case 'F' :
				carType = "Full size";
				break;
			case 'P' :
				carType = "Premium";
				break;
			case 'L' :
				carType = "Luxury";
				break;
			case 'X' :
				carType = "Special";
				break;
			default :
				carType = "Not found";
			}
			return carType;
		}

	// Sorts the given JSON array by the given key
	// returns JSON array sorted in ascending order by the key
	@SuppressWarnings("unchecked")
	public static JSONArray sortJSONArrayAscending(JSONArray arrayToSort, String key){
		
		Collections.sort(arrayToSort, new Comparator<JSONObject>() { // call the sort from collections
			@Override
			public int compare(JSONObject a, JSONObject b) { // override compare function
				String strA = "0", strB = "0"; // set new strings
				double valA = 0, valB = 0; // set new doubles
				
				try {
					strA = (String) a.get(key).toString(); // get key A as string
					strB = (String) b.get(key).toString(); // get kety B as string
					valA = Double.parseDouble(strA); // get key A as double
					valB = Double.parseDouble(strB); // get key B as double
				}
				catch (Exception e) {
					e.printStackTrace(); // catch any exceptions and print stack trace
				}
				
				if(valA < valB){
					return -1;
				}
				else if(valA > valB){
					return 1;
				}
				else {
					return 0;
				}
				
			}
		});
		
		return arrayToSort; // return sorted array
	}
}
