package neel.com.jatra.model;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;

public class GetDirectionsData extends AsyncTask<Object,String,String> {


    String directionsData;
    String url;

    String duration;
    String distance;
    Marker mMarker;
    GoogleMap mMap;



    @Override
    protected String doInBackground(Object... objects) {

        url = (String)objects[0];
        mMarker = (Marker) objects[1];
        mMap = (GoogleMap) objects [2];


        DownloadUrl downloadUrl = new DownloadUrl();
        try {

            directionsData = downloadUrl.readUrl(url);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return directionsData;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        String[] directionPolylines;

        HashMap<String,String> directionsList = null;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirectionData(s);
        directionPolylines = parser.parseDirectionDataForPolyline(s);

        Log.i("DIRECTION_DATA",s);
        Log.i("DIRECTION_LIST", String.valueOf(directionsList));


        duration = directionsList.get("duration");
        distance = directionsList.get("distance");

        mMarker.setTitle("Duration = "+duration);
        mMarker.setSnippet("Distance = "+distance);

        displayPolylineDirection(directionPolylines);


    }

    private void displayPolylineDirection(String[] directionPolylines) {

        int count = directionPolylines.length;
        for(int i = 0; i<count;i++){

            PolylineOptions options = new PolylineOptions();
            options.color(Color.GREEN);
            options.width(12);
            options.addAll(PolyUtil.decode(directionPolylines[i]));

            mMap.addPolyline(options);


        }


    }
}
