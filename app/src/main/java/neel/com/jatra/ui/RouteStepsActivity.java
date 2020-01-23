package neel.com.jatra.ui;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import neel.com.jatra.AccountSetupActivity;
import neel.com.jatra.MainActivity;
import neel.com.jatra.R;

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
