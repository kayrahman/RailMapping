package neel.com.railmappingkl.model;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import neel.com.railmappingkl.R;
import neel.com.railmappingkl.adapter.NearbyStationsListRecyclerAdapter;

public class NearbyPlacesData extends AsyncTask<Object,String,String> {


    String googlePlacesData;
    GoogleMap mMap;
    String url;
    ArrayList<StationInfo> mStations;
    NearbyStationsListRecyclerAdapter adapter;

    public NearbyPlacesData(NearbyStationsListRecyclerAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected String doInBackground(Object... objects) {

        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        mStations = (ArrayList) objects[2];


        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlacesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return googlePlacesData;
    }

    private void showNearbyPlaces(List<HashMap<String,String>> nearbyPlacesList){

        for (int i = 0; i<nearbyPlacesList.size();i++){

            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googlePlace = nearbyPlacesList.get(i);

            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");

            double lat = Double.parseDouble(googlePlace.get("latitude"));
            double lng = Double.parseDouble(googlePlace.get("longitude"));

            LatLng latLng = new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName+":"+vicinity);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_train));


            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            double startLat = 3.0220;
            double startLng = 101.7055;

            float[] results = new float[5];
            Location.distanceBetween(startLat,startLng,lat,lng,results);

            Log.i("DISTANCE_TO_STATION", String.valueOf(results[0]));

            StationInfo stationInfo = new StationInfo(placeName,results[0]);
            mStations.add(stationInfo);

          // adapter.notifyDataSetChanged();



            Log.i("GOOGLE_LAT",placeName);



            }



        }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        List<HashMap<String,String>> nearbyPlaceList = null;
        DataParser parser = new DataParser();
        nearbyPlaceList = parser.parse(s);

        showNearbyPlaces(nearbyPlaceList);


        Log.i("NEARBY_PLACES", String.valueOf(nearbyPlaceList.size()));



    }
}
