package neel.com.jatra;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import neel.com.jatra.ui.MapsActivity;
import neel.com.jatra.ui.PlacesListActivity;
import neel.com.jatra.ui.RouteStepsActivity;

import static neel.com.jatra.helperUtils.FirebaseConstants.DATABASE_USER;

public class MainActivity extends AppCompatActivity {

   Button mGobtn;
    private FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_maps:

                    startActivity(new Intent(MainActivity.this, MapsActivity.class));

                    return true;

                case R.id.navigation_places:

                    startActivity(new Intent(MainActivity.this,PlacesListActivity.class));

                    return true;

                case R.id.navigation_profile:

                    startActivity(new Intent(MainActivity.this, AccountSetupActivity.class));

                    return true;

                case R.id.navigation_planner:


                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mGobtn = findViewById(R.id.btn_ac_planner_go);
        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

       Menu menu = navigationView.getMenu();
       MenuItem menuItem = menu.getItem(1);
       menuItem.setChecked(true);

       mGobtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               startActivity(new Intent(MainActivity.this, RouteStepsActivity.class));

           }
       });


       checkIfUserExist();

    }

    private void checkIfUserExist() {

        if (mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(DATABASE_USER);

            userRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        if (!dataSnapshot.hasChild("name")) {

                            startActivity(new Intent(MainActivity.this, AccountSetupActivity.class));

                        }

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

}















