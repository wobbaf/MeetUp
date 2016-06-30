package com.example.sample.User;

import com.example.sample.DistrictsInformation;
import com.example.sample.GoogleMapsApi.ApiQueries;
import com.example.sample.Utils.Location;


import java.util.List;

/**
 * Created by Piotr on 28.05.2016.
 */
public class User {

    private List<String> idOfFavouritePlaces;
    private String id;
    private Location location;
   
    private Integer distirictID;
    private String distirictName;





    public Integer getDistrictID() {
        return distirictID;
    }

    private void setDistirictID(Integer distirictID) {
        this.distirictID = distirictID;
    }

    public String getDistirictName() {
        return distirictName;
    }

    private void setDistirictName(String distirictName) {
        this.distirictName = distirictName;
    }


    public User(List<String> idOfFavouritePlaces, String id, Location  location) {
        this.idOfFavouritePlaces = idOfFavouritePlaces;
        this.id = id;
        
        if(location == null)
        	System.out.println("User location is null");
        else{
            setDistirictName(ApiQueries.getDistrictNameQuery(location));


            Boolean found = false;

        for(int i = 0; i < DistrictsInformation.dictionaryNames.length; i++)
        {
            if(getDistirictName().equals(DistrictsInformation.dictionaryNames[i])){
                setDistirictID(i);
                found = true;
            }      
        }

        if(!found)
        System.out.print("Error: Couldn't find matching district name while creating user. The name is: " + getDistirictName());


        this.location = location;
        }
       
    }




    public List<String> getIdOfFavouritePlaces() {
        return idOfFavouritePlaces;
    }

    public void setIdOfFavouritePlaces(List<String> idOfFavouritePlaces) {
        this.idOfFavouritePlaces = idOfFavouritePlaces;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


}
