package com.example.sample.User;


import com.example.sample.Utils.Location;

import java.util.List;

/**
 * Created by Piotr on 28.05.2016.
 */
public class Invited extends User {

  
	  private boolean hasConfirmed = false;
	    
    
    public Invited(List<String> idOfFavouritePlaces, String id, Location location,boolean hasConfirmed) {
    	 super(idOfFavouritePlaces, id, location);
    	if(location == null)
    		System.out.println("No location");
    	
    	this.hasConfirmed = hasConfirmed;
       
    }
    
    public boolean hasConfirmed() {
        return hasConfirmed;
    }

    public void setHasConfirmed(boolean hasConfirmed) {
        this.hasConfirmed = hasConfirmed;
    }
}
