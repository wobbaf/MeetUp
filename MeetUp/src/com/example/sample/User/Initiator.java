package com.example.sample.User;

import com.example.sample.Utils.Location;

import java.util.List;

/**
 * Created by Piotr on 28.05.2016.
 */
public class Initiator extends User{


    private List<String> idsOfInvitedUsers;
    private String placeType; 

    public Initiator(List<String> idOfFavouritePlaces, String id, Location location, String placeType,List<String> idsOfInvitedUsers) {
        super(idOfFavouritePlaces, id, location);
        this.placeType = placeType;
        this.idsOfInvitedUsers = idsOfInvitedUsers;
    }


    public List<String> getIdsOfInvitedUsers() {
        return idsOfInvitedUsers;
    }

    public void setIdsOfInvitedUsers(List<String> idsOfInvitedUsers) {
        this.idsOfInvitedUsers = idsOfInvitedUsers;
    }
    public String getPlaceType() {
        return placeType;
    }

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

}
