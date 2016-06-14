package com.example.sample.User;
 
import com.example.sample.Utils.Location;
 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
 
/**
 * Created by Piotr on 28.05.2016.
 */
public class Initiator extends User{
 
 
    private List<String> idsOfInvitedUsers;
    private String placeType; 
    private long time;
 
    public Initiator(List<String> idOfFavouritePlaces, String id, Location location, String placeType,List<String> idsOfInvitedUsers, String time) {
        super(idOfFavouritePlaces, id, location);
        this.placeType = placeType;
        this.idsOfInvitedUsers = idsOfInvitedUsers;
 
 
 
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try
        {
            Date date = simpleDateFormat.parse(time);
 
            this.time = date.getTime() / 1000;
        }
        catch (ParseException ex)
        {
            System.out.println("Exception "+ex);
        }
 
 
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
    public long getTime() {
        return time;
    }
 
    public void setTime(long time) {
        this.time = time;
    }
 
}