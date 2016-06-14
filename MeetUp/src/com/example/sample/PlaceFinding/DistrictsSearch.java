package com.example.sample.PlaceFinding;
 
import com.example.sample.DistrictsInformation;
import com.example.sample.GoogleMapsApi.ApiQueries;
import com.example.sample.Utils.Location;
import com.example.sample.User.Initiator;
import com.example.sample.User.Invited;
 
import java.util.*;
import java.util.stream.Collectors;
 
/**
 * Created by Piotr on 28.05.2016.
 */
public class DistrictsSearch {
 
 
    private List<Location> allLocations;
    private List<Integer> idsOfAllUSers;
    private String typeOfPlace ="";
    private Set<String> favouritePlacesId;
 
    public DistrictsSearch()
    {
        idsOfAllUSers = new ArrayList<>();
        idsOfAllUSers = new ArrayList<>();
    }
 
 
 
 
 
    public Stack<Integer> BestPlace(Initiator initiator, List<Invited> allInvited) {
 
        boolean flag = false;
        //How far user has to be to neglect his existence
        int distanceThreshold = 3;
        Stack<Integer> bestPlacesID = new Stack<Integer>();
 
        init(initiator,allInvited);
 
 
        int[][] usedDistricts = new int[idsOfAllUSers.size()][];
 
 
        Boolean theSame = true;
        int tempID = idsOfAllUSers.get(0);
        for(int i =1; i < idsOfAllUSers.size(); i++)
        {
        	if(idsOfAllUSers.get(i)!= null && tempID != idsOfAllUSers.get(i)){
        		theSame = false;
        		break;
        				
        	}
        		
        }
        
        
        if(theSame) {
            bestPlacesID.push(tempID);
            return bestPlacesID;
        }
        
        
      int i =0;
        for (Integer currDistrict =idsOfAllUSers.get(i); i<idsOfAllUSers.size(); i++) {
            usedDistricts[i] = DistrictsInformation.timeValues[currDistrict];
            
        }
 
        double bestMinimum = Double.POSITIVE_INFINITY;
        double currBest;
 
        int[] checkedDist = new int[usedDistricts.length];
 
        for (i = 0; i < usedDistricts[0].length; i++) {
            for (int j = 0; j < usedDistricts.length; j++) {
                checkedDist[j] = usedDistricts[j][i];
 
            }
 
            Statistics stat = new Statistics(checkedDist);
            double median = stat.median();
 
 
            //remove to far users
            for (int j = 0; j < checkedDist.length; j++) {
                if (checkedDist[j] > median * distanceThreshold) {
                    removeElement(checkedDist, j);
                    flag = true;
                }
            }
 
            if (flag)
                stat = new Statistics(checkedDist);
 
            currBest = stat.getMean() + stat.getMean() * stat.getVariance();
 
            if (bestMinimum > currBest) {
                bestMinimum = currBest;
                bestPlacesID.push(i);
            }
 
 
        }
 
        return bestPlacesID;
    }
 
private void init(Initiator initiator, List<Invited> allInvited)
{
    List<Location> allLocations = new ArrayList<>();
    allLocations.add(initiator.getLocation());
    allLocations.addAll(allInvited.stream().map(Invited::getLocation).collect(Collectors.toList()));
 
    this.typeOfPlace = initiator.getPlaceType();
    this.allLocations = allLocations;
 
 
    List<Integer> usedDistrictsId = new ArrayList<>();
    usedDistrictsId.add(initiator.getDistrictID());
    usedDistrictsId.addAll(allInvited.stream().distinct().map(Invited::getDistrictID).collect(Collectors.toList()));
 
    this.idsOfAllUSers = usedDistrictsId;
 
 
    List<String> bestPlacesIdWithDuplicates = new ArrayList<>();
    if(initiator.getIdOfFavouritePlaces() != null)
    bestPlacesIdWithDuplicates.addAll(initiator.getIdOfFavouritePlaces());
 
    for(Invited inv : allInvited){
    	if(inv.getIdOfFavouritePlaces()!=null)
        bestPlacesIdWithDuplicates.addAll(inv.getIdOfFavouritePlaces());
    }
    favouritePlacesId = new HashSet<>(bestPlacesIdWithDuplicates);
 
    this.idsOfAllUSers = usedDistrictsId;
 
}
 
 
    public Stack<String> findClosestPlaceInBestDistrict(Stack<Integer> bestDistrictID, long [] timeTravelForInitiator) {
 
 
        double bestMinimum = Double.POSITIVE_INFINITY;
 
        Stack<String> bestPlaces = new Stack<>();
        List<String> placesId = new ArrayList<>();
        for(int i =0; bestDistrictID.elementAt(i)!= null && i< bestDistrictID.size() ; i++) {
        	
        	 placesId = ApiQueries.findClosestPlaces(typeOfPlace, DistrictsInformation.getDistrictFromID(bestDistrictID.pop()), 15 /* how many results */);
        
            if(placesId != null && placesId.size()!=0)
                break;
        }
 
        
        
        
        if(placesId == null || placesId.size() == 0)
        {
        	System.out.println("Nic nie znaleziono");
            return null;
        }
 
        
        System.out.println(placesId.toString());
        
        
        int[] duration = new int[allLocations.size()];
 
       // System.out.println(placesId.toString());
        
        //Find if there is a favorite place among the chosen one
        Set<String> allCommon = new HashSet<>(placesId);
        Set<String> allDifferent = new HashSet<>(placesId);
 
 
        allDifferent.removeAll(favouritePlacesId);
        allCommon.retainAll(favouritePlacesId);
 
 
        List<String> commonFavourite =new ArrayList<> (allCommon);
        List<String>distinctFavourite =new ArrayList<String> (allDifferent);
 
            for (int j = 0; j < allDifferent.size() && j < 5; j++) {
                for (int i = 0; i < allLocations.size(); i++) {
                	
                    duration[i] = (ApiQueries.getTravelTimeFromPlaceID(allLocations.get(i), distinctFavourite.get(j)));
                }
 
                Statistics stat = new Statistics(duration);
                double mean = stat.getMean();
                if(bestMinimum > mean)
                {
                    bestMinimum = mean;
                    bestPlaces.add(placesId.get(j));
                }
            }
 
        bestMinimum = Double.POSITIVE_INFINITY;
 
        if (placesId.size() > 0) {
            for (int j = 0; j < commonFavourite.size(); j++) {
                for (int i = 0; i < allLocations.size(); i++) {
                    duration[i] = (ApiQueries.getTravelTimeFromPlaceID(allLocations.get(i), commonFavourite.get(j)));
                }
 
 
                Statistics stat = new Statistics(duration);
                double mean = stat.getMean();
 
                if (bestMinimum > mean) {
                    bestMinimum = mean;
                    bestPlaces.add(placesId.get(j));
                    timeTravelForInitiator[0] = duration[0];
                }
 
            }
        }
 
        //Stack Best Places consists of the best pubs to go to.
        return bestPlaces;
 
    }
 
 
    private void removeElement(int[] a, int del) {
        System.arraycopy(a,del+1,a,del,a.length-1-del);
    }
 
 
    private class Statistics
    {
        int[] data;
        int size;
 
        public Statistics(int[] data)
        {
            this.data = data;
            size = data.length;
        }
 
        double getMean()
        {
            double sum = 0.0;
            for(double a : data)
                sum += a;
            return sum/size;
        }
 
        double getVariance()
        {
            double mean = getMean();
            double temp = 0;
            for(double a :data)
                temp += (mean-a)*(mean-a);
            return temp/size;
        }
 
        double getStdDev()
        {
            return Math.sqrt(getVariance());
        }
 
        public double median()
        {
            Arrays.sort(data);
 
            if (data.length % 2 == 0)
            {
                return (data[(data.length / 2) - 1] + data[data.length / 2]) / 2.0;
            }
            else
            {
                return data[data.length / 2];
            }
        }
    }
 
 
 
}