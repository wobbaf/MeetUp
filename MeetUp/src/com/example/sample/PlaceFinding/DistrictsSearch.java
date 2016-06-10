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





    public int BestPlace(Initiator initiator, List<Invited> allInvited) {

        boolean flag = false;
        //How far user has to be to neglect his existence
        int distanceThreshold = 3;

        init(initiator,allInvited);


        int[][] usedDistricts = new int[idsOfAllUSers.size()][];


        Boolean theSame = true;
        int tempID = idsOfAllUSers.get(0);
        for(int i =1; i < idsOfAllUSers.size(); i++)
        {
        	if(tempID != idsOfAllUSers.get(i)){
        		theSame = false;
        		break;
        				
        	}
        		
        }
        
        
        if(theSame)
        	return tempID;
        
        
      int i =0;
        for (Integer currDistrict =idsOfAllUSers.get(i); i<idsOfAllUSers.size(); i++) {
            usedDistricts[i] = DistrictsInformation.timeValues[currDistrict];
            
        }
        Location x;


        int bestDistrictID = 0;
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
                bestDistrictID = i;
            }


        }

        return bestDistrictID;
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


    public Stack<String> findClosestPlaceInBestDistrict(int bestDistrictID) {


        double bestMinimum = Double.POSITIVE_INFINITY;

        Stack<String> bestPlaces = new Stack<>();
        String districtFullName = DistrictsInformation.getDistrictFromID(bestDistrictID);

        List<String> placesId = ApiQueries.findClosestPlaces(typeOfPlace, districtFullName, 15 /* how many results */);
        int[] duration = new int[allLocations.size()];

        System.out.println(placesId.toString());
        
        //Find if there is a favourite place among the chosen one
        Set<String> allCommon = new HashSet<>(favouritePlacesId);
        Set<String> allDifferent = new HashSet<>(favouritePlacesId);
        allCommon.retainAll(placesId);
        allDifferent.removeAll(placesId);
        List<String> commonFavourite =new ArrayList<String> (allCommon);
        List<String>distinctFavourite =new ArrayList<String> (allDifferent);

            for (int j = 0; j < distinctFavourite.size() && j < 5; j++) {
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
            for (int j = 0; j < placesId.size(); j++) {
                for (int i = 0; i < allLocations.size(); i++) {
                    duration[i] = (ApiQueries.getTravelTimeFromPlaceID(allLocations.get(i), placesId.get(j)));
                }


                Statistics stat = new Statistics(duration);
                double mean = stat.getMean();
                System.out.println(mean); 
                if (bestMinimum > mean) {
                    bestMinimum = mean;
                    bestPlaces.add(placesId.get(j));
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
