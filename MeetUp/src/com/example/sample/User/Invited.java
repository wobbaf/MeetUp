package com.example.sample.User;


import com.example.sample.Utils.Location;

import java.util.List;

/**
 * Created by Piotr on 28.05.2016.
 */
public class Invited extends User {

    private boolean hasConfirmed = false;

    public Invited(boolean hasConfirmed){
    	this.hasConfirmed = hasConfirmed;
    }
    
    public Invited(List<String> idOfFavouritePlaces, String id, Location location) {
        super(idOfFavouritePlaces, id, location);
    }
    
    public boolean hasConfirmed() {
        return hasConfirmed;
    }

    public void setHasConfirmed(boolean hasConfirmed) {
        this.hasConfirmed = hasConfirmed;
    }
}
