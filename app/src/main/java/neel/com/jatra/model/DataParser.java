package neel.com.jatra.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {



    private HashMap<String,String> getPlace(JSONObject googlePlacesJson){

        HashMap<String,String> googlePlacesMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        try {

        if(!googlePlacesJson.isNull("name")){
            placeName = googlePlacesJson.getString("name");
            }
            if(!googlePlacesJson.isNull("vicinity")){
            vicinity = googlePlacesJson.getString("vicinity");
            }

            latitude = googlePlacesJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlacesJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlacesJson.getString("reference");

            googlePlacesMap.put("place_name",placeName);
            googlePlacesMap.put("vicinity",vicinity);
            googlePlacesMap.put("latitude",latitude);
            googlePlacesMap.put("longitude",longitude);
            googlePlacesMap.put("reference",reference);




        }catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlacesMap;


    }


    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray){

        int count = jsonArray.length();

        List<HashMap<String,String>> placesList = new ArrayList<>();
        HashMap<String,String> placeMap = null;

        for(int i = 0; i<count ; i++){

            try {
                placeMap = getPlace((JSONObject)jsonArray.get(i));
                placesList.add(placeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return placesList;

    }


    public List<HashMap<String,String>> parse (String jsonData){

        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }


       return getPlaces(jsonArray);

    }

    public HashMap<String,String> parseDirectionData(String jsonData){

        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            //jsonArray = jsonObject.getJSONArray("routes");
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return getDuration(jsonArray);


    }

    public String[] parseDirectionDataForPolyline(String jsonData){

        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            //jsonArray = jsonObject.getJSONArray("routes");
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return getPaths(jsonArray);


    }

    private HashMap<String,String> getDuration(JSONArray directionsJsonArray){

        String duration = "";
        String distance = "";

        HashMap<String,String> googleDirections = new HashMap<>();

        Log.i("DIRECTIONS_JSON",directionsJsonArray.toString());

       try {
            duration = directionsJsonArray.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = directionsJsonArray.getJSONObject(0).getJSONObject("distance").getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        googleDirections.put("duration",duration);
        googleDirections.put("distance",distance);


        return googleDirections;


    }


    private String[] getPaths(JSONArray googleStepsJson){

        int count = googleStepsJson.length();
        String[] polylines = new String[count];

        for(int i=0;i<count;i++){
            try {
                polylines [i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return polylines;

    }

    private String getPath(JSONObject googlePathJson){

        String polyline = "";
        try {

            polyline = googlePathJson.getJSONObject("polyline").getString("points");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return polyline;

    }





}
