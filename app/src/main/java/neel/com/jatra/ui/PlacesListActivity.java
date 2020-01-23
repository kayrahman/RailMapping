package neel.com.jatra.ui;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import neel.com.jatra.AccountSetupActivity;
import neel.com.jatra.MainActivity;
import neel.com.jatra.R;
import neel.com.jatra.adapter.PopularPlaceRecyclerAdapter;
import neel.com.jatra.model.OnPopularPlaceItemClick;
import neel.com.jatra.model.PopularPlace;

import static neel.com.jatra.helperUtils.FirebaseConstants.DATABASE_MOST_POPULAR_PLACES;
import static neel.com.jatra.ui.PlaceDetailActivity.LIST_ITEM_KEY;

public class PlacesListActivity extends AppCompatActivity implements OnPopularPlaceItemClick {

    private RecyclerView mMostVisitedPlaceRv;
    PopularPlaceRecyclerAdapter mPopularPlaceRecyclerAdapter;
    private ArrayList<PopularPlace> mPopularPlaces = new ArrayList<>();

    private OnPopularPlaceItemClick mPopularPlaceItemClick;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_maps:

                    startActivity(new Intent(PlacesListActivity.this, MapsActivity.class));

                    return true;

                case R.id.navigation_places:


                    return true;

                case R.id.navigation_profile:

                    startActivity(new Intent(PlacesListActivity.this, AccountSetupActivity.class));

                    return true;

                case R.id.navigation_planner:

                    startActivity(new Intent(PlacesListActivity.this, MainActivity.class));

                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_list);

        mPopularPlaceItemClick = this;


        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);




        mMostVisitedPlaceRv = findViewById(R.id.rv_ac_most_visited_places);

        mPopularPlaceRecyclerAdapter = new PopularPlaceRecyclerAdapter(mPopularPlaces,this,mPopularPlaceItemClick);
        mMostVisitedPlaceRv.setAdapter(mPopularPlaceRecyclerAdapter);
        // mMostVisitedPlaceRv.setNestedScrollingEnabled(false);
        mMostVisitedPlaceRv.setLayoutManager(new LinearLayoutManager(PlacesListActivity.this));
        mMostVisitedPlaceRv.setHasFixedSize(true);
        populateMostVisitedPlacesRecyclerView();

    }


    private void populateMostVisitedPlacesRecyclerView() {



        DatabaseReference mostPopularRef = FirebaseDatabase.getInstance().getReference().child(DATABASE_MOST_POPULAR_PLACES);
        mostPopularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.getChildrenCount()>0){

                        for(DataSnapshot placeSnapShot : dataSnapshot.getChildren()) {

                            PopularPlace popularPlace = placeSnapShot.getValue(PopularPlace.class);
                            mPopularPlaces.add(popularPlace);

                            mPopularPlaceRecyclerAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPopularPlaceItemClick(int position) {
        Intent intent = new Intent(PlacesListActivity.this,PlaceDetailActivity.class);
        //intent.putExtra(LIST_ITEM_KEY,position);
        intent.putExtra(LIST_ITEM_KEY, mPopularPlaces.get(position));
        Log.i("INTENT_EXTRA",mPopularPlaces.get(position).toString());
        startActivity(intent);

    }
}
