package com.example.sample.GoogleMapsApi;
 
import com.example.sample.DistrictsInformation;
import com.example.sample.Utils.Location;
import org.json.JSONArray;
import org.json.JSONObject;
 
import java.util.ArrayList;
import java.util.List;
 
/**
 * Created by Piotr on 29.05.2016.
 */
public class ApiQueries {
 
	public static String getDistrictNameQuery(Location location) {
 
		String districtNameQuery = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location.getLatitude()
				+ "," + location.getLongitude() + "&sensor=false&key=" + DistrictsInformation.locationKey;
 
		String returnName = null;
		try {
			JSONObject json = JsonReader.readJsonFromUrl(districtNameQuery);
			returnName = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components")
					.getJSONObject(2).get("long_name").toString();
		} catch (Exception exp) {
			exp.printStackTrace();
 
			// TODO: 29.05.2016 Do something with it
		}
 
		return returnName;
	}
 
	public static Location getLocalizationOfDistrict(String districtName) {
		String zd = "https://maps.googleapis.com/maps/api/geocode/json?address=" + districtName + "&key="
				+ DistrictsInformation.locationKey;
		Location newLocation = null;
		try {
			JSONObject json = JsonReader.readJsonFromUrl(zd);
			JSONObject locationOfDistrict = (json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry")
					.getJSONObject("location"));
 
			newLocation = new Location(locationOfDistrict.get("lat").toString(),
					locationOfDistrict.get("lng").toString());
 
		} catch (Exception exp) {
			exp.printStackTrace();
		}
 
		return newLocation;
	}
 
	public static List<String> findClosestPlaces(String type, String place, int tresshold) {
		
		String actualPlace;
		String[] possiblePraga = place.split(" ");
		if(possiblePraga.length == 2){
			String[] pragaOption = place.split(" ");
			actualPlace = pragaOption[0] +"_" + pragaOption[1];
			
		}
		else
			actualPlace = place;
		
		
		
		String xd = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + type + "_" + actualPlace
				+ "&key=" + DistrictsInformation.nearbyKey;
 
		List<String> foundClosePlacesId = new ArrayList<>();
		Location newLocation = null;
		try {
			JSONObject json = JsonReader.readJsonFromUrl(xd);
 
			JSONArray jsonArr = json.getJSONArray("results");
			for (int i = 0; i < tresshold && i < jsonArr.length(); i++) {
 
				foundClosePlacesId.add(jsonArr.getJSONObject(i).get("place_id").toString());
 
			}
		} catch (Exception exp) {
			exp.printStackTrace();
		}
 
		return foundClosePlacesId.size() ==0 ?null:foundClosePlacesId;
 
	}
 
	public static int getTravelTimeFromPlaceID(Location Source, String destination) {
		String source = Source.getLatitude() + "," + Source.getLongitude();
 
		String zd = "https://maps.googleapis.com/maps/api/directions/json?traffic_mode=pessimistic&"
				+ "transit_mode=train|bus|subway" + "&origin=" + source + "&destination=place_id:" + destination
				+ "&key=" + DistrictsInformation.apiKey3;
 
		try {
			JSONObject json = JsonReader.readJsonFromUrl(zd);
			return Integer.parseInt(json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0)
					.getJSONObject("duration").get("value").toString());
 
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return -1;
	}
 
}