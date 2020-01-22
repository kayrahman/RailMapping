package neel.com.railmappingkl.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import neel.com.railmappingkl.AccountSetupActivity;
import neel.com.railmappingkl.MainActivity;
import neel.com.railmappingkl.R;

public class RouteStepsActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_maps:

                    startActivity(new Intent(RouteStepsActivity.this, MapsActivity.class));

                    return true;

                case R.id.navigation_places:

                    startActivity(new Intent(RouteStepsActivity.this,PlacesListActivity.class));

                    return true;

                case R.id.navigation_profile:

                    startActivity(new Intent(RouteStepsActivity.this, AccountSetupActivity.class));

                    return true;

                case R.id.navigation_planner:

                    startActivity(new Intent(RouteStepsActivity.this, MainActivity.class));

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_steps);


        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

    }
}
