package neel.com.railmappingkl.ui;



//TODO: DYNAMIC LOCATION
//TODO: SHOW LIST OF NEARBY RAIL STATIONS


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import neel.com.railmappingkl.AccountSetupActivity;
import neel.com.railmappingkl.LoginActivity;
import neel.com.railmappingkl.MainActivity;
import neel.com.railmappingkl.R;
import neel.com.railmappingkl.adapter.CustomInfoWindowAdapter;
import neel.com.railmappingkl.adapter.NearbyStationsListRecyclerAdapter;
import neel.com.railmappingkl.adapter.PlaceAutoCompleteAdapter;
import neel.com.railmappingkl.model.GetDirectionsData;
import neel.com.railmappingkl.model.NearbyPlacesData;
import neel.com.railmappingkl.model.PlaceInfo;
import neel.com.railmappingkl.model.StationInfo;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener{


   public static int PLACE_PICKER_REQUEST = 1;


    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mListener;


    public static final String PLACES_API_KEY = "AIzaSyB5VwopHdjBAlzz8M1kLV7gmIlQnZ_P2xw";


    private GoogleMap mMap;
    View mapView;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastLocation = new Location("");

    private int PROXIMITY_RADIUS = 50000;
    double latitude,longitude;

    private AutoCompleteTextView mSearchTxt;
    private ImageView mPLaceInfoImageview;
    private ImageView mPlacePickerImageview;
    private Button mNearbyStationsBtn;
    PlaceAutoCompleteAdapter mPlaceAutoCompleteAdapter;

    private GoogleApiClient mGoogleApiClient;
    private GeoDataClient mGeoDataClient;
    private LatLngBounds mLatLngBounds;
    private PlaceInfo mPlaceInfo;
    private Marker mMarker;
    private Object dataTransfer[];

    private ArrayList<StationInfo> mStationInfos = new ArrayList<>();


    private BottomSheetBehavior mBottomSheetBehavior;

    private RecyclerView mStationsListRv;
    NearbyStationsListRecyclerAdapter mNearbyStationsListRecyclerAdapter;

    private Dialog mGpsProviderAlertDialog;

    private ExpandableRelativeLayout mExpandableRelativeLayout;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_maps:

                    return true;

                case R.id.navigation_places:

                    startActivity(new Intent(MapsActivity.this,PlacesListActivity.class));

                    return true;
                case R.id.navigation_profile:

                    startActivity(new Intent(MapsActivity.this, AccountSetupActivity.class));

                    return true;
                case R.id.navigation_planner:

                    startActivity(new Intent(MapsActivity.this, MainActivity.class));

                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mAuth = FirebaseAuth.getInstance();
        mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(MapsActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        };


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,this)
                .build();



        mGeoDataClient = Places.getGeoDataClient(this, null);
        mLatLngBounds = new LatLngBounds(new LatLng(-40,-168),new LatLng(71,136));

        mGpsProviderAlertDialog = new Dialog(this);


        mSearchTxt = findViewById(R.id.at_ac_maps_search_location);
        mPlaceAutoCompleteAdapter = new PlaceAutoCompleteAdapter(MapsActivity.this,mGeoDataClient,mLatLngBounds,null);

        mSearchTxt.setAdapter(mPlaceAutoCompleteAdapter);
        mSearchTxt.setOnItemClickListener(mAutoCompleteListener);
        mSearchTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchTxt.setText("");
            }
        });
        mPLaceInfoImageview = findViewById(R.id.iv_ac_maps_place_info);
        mPlacePickerImageview = findViewById(R.id.iv_ac_maps_nearby_place_picker);
        mNearbyStationsBtn = findViewById(R.id.btn_ac_maps_nearby_stations);

        mExpandableRelativeLayout = findViewById(R.id.rl_ac_maps_expandable_layout);



        mPLaceInfoImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    if(mMarker.isInfoWindowShown()){
                        mMarker.hideInfoWindow();
                    }else{
                        mMarker.showInfoWindow();

                    }

                }catch (NullPointerException e){

                }

            }
        });


        mPlacePickerImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //PLACEPICKER


                  PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MapsActivity.this), PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });


//        View bottom_sheet_stations = findViewById(R.id.bottom_sheet_stations);
//        mBottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_stations);

        mStationsListRv = findViewById(R.id.rv_ac_maps_station_lists);
        mStationsListRv.setNestedScrollingEnabled(false);

        mNearbyStationsListRecyclerAdapter = new NearbyStationsListRecyclerAdapter(mStationInfos);
        Log.i("STATIONS_SIZE", String.valueOf(mStationInfos.size()));
        mStationsListRv.setAdapter(mNearbyStationsListRecyclerAdapter);
        mStationsListRv.setLayoutManager(new LinearLayoutManager(MapsActivity.this));
        mStationsListRv.setHasFixedSize(true);


        /*
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState){

                    case BottomSheetBehavior.STATE_COLLAPSED:
                       // station_name.setText("Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        //station_name.setText("DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                       // station_name.setText("EXPANDING");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                       // station_name.setText("HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                       // station_name.setText("SETTLING");
                        break;


                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });*/

        hideSoftKetBoard();



        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        mNearbyStationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mExpandableRelativeLayout.toggle();
                
            }
        });


    }




    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace( this,data);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient,place.getId());

                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    }

    private void getNearbyStations() {

        mMap.clear();

        String hospital = "subway_station|train_station|lrt_station|ktm_station|bus_station";
        String url = getUrl(latitude,longitude,hospital);
        Log.d("LAT_GET_NEARBY",String.valueOf(latitude));
        dataTransfer = new Object[3];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = mStationInfos;

        NearbyPlacesData nearbyPlacesData = new NearbyPlacesData(mNearbyStationsListRecyclerAdapter);
        nearbyPlacesData.execute(dataTransfer);



    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isGpsEnabled || isNetworkEnabled) {

            if (mapView != null &&
                    mapView.findViewById(Integer.parseInt("1")) != null) {
                // Get the button view
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                // and next place it, on bottom right (as Google Maps app)
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                        locationButton.getLayoutParams();
                // position on right bottom
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 30, 200);
            }


            //..........................LOCATION BUTTON REALLIGN END...........................//


            mLocationRequest = LocationRequest.create();
//        mLocationRequest.setInterval(100*1000);
//        mLocationRequest.setFastestInterval(100*1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                } else {

                    checkLocationPermission();
                }
            }

            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallBack, Looper.myLooper());

            mMap.setMyLocationEnabled(true);
        }else{

            showGpsDisabledDialog();
        }




    }

    LocationCallback mLocationCallBack = new LocationCallback(){

        @Override
        public void onLocationResult(LocationResult locationResult) {

            for(Location location : locationResult.getLocations()){


                mLastLocation = location;

                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.d("LOCATION",location.toString());
                Log.d("LAT_LNG",String.valueOf(latitude));

                getNearbyStations();

                //  mLocTxt.setText(location.toString());
                if(mMap != null) {

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));


                }


            }


        }
    };

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("Permission Message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapsActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                            }
                        })
                        .create()
                        .show();

            }


            else{

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);


            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallBack, Looper.myLooper());

                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }


    }


    private String getUrl(double latitude,double logitude,String nearbyPlace){

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");

        googlePlaceUrl.append("location="+latitude+","+logitude);
        googlePlaceUrl.append("&radius="+PROXIMITY_RADIUS);
        googlePlaceUrl.append("&type="+nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+PLACES_API_KEY);


        Log.i("LAT_LNG",String.valueOf(latitude));

        return googlePlaceUrl.toString();



    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mListener);
    }



    private void hideSoftKetBoard(){

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }



/**
 * autocomplete listener
 * */

private AdapterView.OnItemClickListener mAutoCompleteListener = new AdapterView.OnItemClickListener(){


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


        final AutocompletePrediction item = mPlaceAutoCompleteAdapter.getItem(i);
        String placeId = item.getPlaceId();

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient,placeId);

        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

    }
};

private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
    @Override
    public void onResult(@NonNull PlaceBuffer places) {


        mSearchTxt.clearFocus();
        hideSoftKetBoard();



        if(!places.getStatus().isSuccess()){

            Toast.makeText(MapsActivity.this, "Query not successful", Toast.LENGTH_SHORT).show();

            places.release();
            return;
        }


        try{

        Place place = places.get(0);

        mPlaceInfo = new PlaceInfo();
        mPlaceInfo.setName(place.getName().toString());
            Log.i("PLACENAME",place.getName().toString());
        mPlaceInfo.setAddress(place.getAddress().toString());
        mPlaceInfo.setId(place.getId());
        mPlaceInfo.setLatlng(place.getLatLng());
      //  mPlaceInfo.setAttributions(place.getAttributions().toString());
        mPlaceInfo.setPhoneNumber(place.getPhoneNumber().toString());
        mPlaceInfo.setWebsiteUri(place.getWebsiteUri());
        mPlaceInfo.setRating(place.getRating());

        Log.i("PLACEINFO",mPlaceInfo.toString());


        }catch (NullPointerException e){

            Log.i("NULL_POINTER",e.getMessage());

        }


        moveCamera(mPlaceInfo);


        places.release();


    }
};


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {





    }



    private void moveCamera(PlaceInfo placeInfo){

        mMap.moveCamera(CameraUpdateFactory.newLatLng(mPlaceInfo.getLatlng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        mMap.clear();
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapsActivity.this));

        if(placeInfo != null){

            try{

               LatLng placeLatLng = placeInfo.getLatlng();

                String snippet = "Address: "+placeInfo.getAddress()+"\n"+
            "Phone Number: "+placeInfo.getPhoneNumber()+"\n"+
                    "Website: "+placeInfo.getWebsiteUri()+"\n"+
             "Price Rating: "+placeInfo.getRating()+"\n";

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(mPlaceInfo.getLatlng())
                        .title(placeInfo.getName())
                        .snippet(snippet);

               mMarker = mMap.addMarker(markerOptions);

               /***
                * SHOW DIRECTIONS POLYLINE AND DURATION HERE
                * **/
               showDirectionAndDuration(placeLatLng);

                hideSoftKetBoard();





            }catch (NullPointerException e){


            }

        }else{

            mMap.addMarker(new MarkerOptions().position(mPlaceInfo.getLatlng()));

        }

    }

    private void showDirectionAndDuration(LatLng placeLatLng) {

        String url = getDirectionUrl(placeLatLng);

        GetDirectionsData directionsData = new GetDirectionsData();
        dataTransfer = new Object[3];
        dataTransfer[0] = url;
        dataTransfer[1] = mMarker;
        dataTransfer[2] = mMap;


        directionsData.execute(dataTransfer);


    }

    private String getDirectionUrl(LatLng placeLatLng) {

        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");

        googleDirectionUrl.append("origin="+latitude+","+longitude);
        googleDirectionUrl.append("&destination="+placeLatLng.latitude+","+placeLatLng.longitude);
        googleDirectionUrl.append("&mode=driving");
        googleDirectionUrl.append("&key="+PLACES_API_KEY);


        Log.i("LAT_LNG",String.valueOf(latitude));

        return googleDirectionUrl.toString();


    }


    private void showGpsDisabledDialog() {


        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        TextView mGotItBtn;

        mGpsProviderAlertDialog.setContentView(R.layout.gps_disabled_alert_dialog);
        mGotItBtn = (TextView)mGpsProviderAlertDialog.findViewById(R.id.dialog_gps_disabled_got_it_btn);

        mGotItBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(action));

            }
        });

        mGpsProviderAlertDialog.setTitle("TURN ON LOCATION");
        mGpsProviderAlertDialog.show();


      /*  final AlertDialog.Builder builder =
                new AlertDialog.Builder(RidersActivity.this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String title = "LOCATION NOT FOUND";
        final String message = "Please turn on Location Services."
                + "For the best experience,"
                + "Please set it to High Accuracy.";


        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("GOT IT",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(action));
                                d.dismiss();
                            }
                        });

        builder.create().show();*/
    }



}



























