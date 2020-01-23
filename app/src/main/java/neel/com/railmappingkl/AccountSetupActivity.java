package neel.com.railmappingkl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import neel.com.railmappingkl.ui.MapsActivity;
import neel.com.railmappingkl.ui.PlacesListActivity;

public class AccountSetupActivity extends AppCompatActivity {

    private Uri mImageUri = null;
    private CircleImageView mDisplayImageBtn;
    private EditText mUserName;
    private EditText mCountryEt;
    private EditText mCityEt;
    private EditText mUserPhone;
    private Button mSubmitBtn;
    private Button mLogoutBtn;

    private StorageReference mStorage;
    private DatabaseReference mDatabaseUsers;

    private ProgressDialog mDialog;

    private static int GALLERY_REQUEST=1;


    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private  byte[] thumb_byte ;

    public static final String PROFILE_PREFERENCES = "ProPref" ;

    private SharedPreferences sharedpreferences;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_maps:

                    startActivity(new Intent(AccountSetupActivity.this, MapsActivity.class));

                    return true;

                case R.id.navigation_places:

                    startActivity(new Intent(AccountSetupActivity.this,PlacesListActivity.class));

                    return true;

                case R.id.navigation_profile:

                    return true;

                case R.id.navigation_planner:

                    startActivity(new Intent(AccountSetupActivity.this, MainActivity.class));

                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);

        // INITIALIZING
        mDialog= new ProgressDialog(this);


        // INITIALIZING

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabaseUsers= FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());


        // INITIALIZING USER FIELDS

        mDisplayImageBtn=(CircleImageView) findViewById(R.id.acc_set_up_image_btn);
        mUserName=(EditText) findViewById(R.id.acc_set_up_name_et);
        mCountryEt = (EditText)findViewById(R.id.acc_set_up_country_et);
        mCityEt=(EditText)findViewById(R.id.acc_set_up_city_et);
        mUserPhone=(EditText)findViewById(R.id.acc_set_up_phone_et);



        mSubmitBtn=(Button)findViewById(R.id.acc_set_up_submit);
        mLogoutBtn=(Button)findViewById(R.id.acc_set_up_logout);



        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPostingUserDetail();
            }
        });
        mLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth.signOut();

            }
        });


        mDisplayImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
            }
        });


        fetchDataFromDatabase();


        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);


          }

    private void fetchDataFromDatabase() {


        mDatabaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    if(dataSnapshot.hasChild("name")){
                        String name = dataSnapshot.child("name").getValue().toString();
                        mUserName.setText(name);

                    }
                    if(dataSnapshot.hasChild("country")){
                        String country = dataSnapshot.child("country").getValue().toString();
                        mCountryEt.setText(country);

                    }
                    if(dataSnapshot.hasChild("city")){
                        String city = dataSnapshot.child("city").getValue().toString();
                        mCityEt.setText(city);

                    }

                    if(dataSnapshot.hasChild("phone")){
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        mUserPhone.setText(phone);

                    }

                    if(dataSnapshot.hasChild("image")){
                        String image = dataSnapshot.child("image").getValue().toString();

                        Picasso.with(AccountSetupActivity.this)
                                .load(image)
                                .placeholder(R.drawable.male)
                                .into(mDisplayImageBtn);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSubmitBtn.requestFocus();


    }

    private void startPostingUserDetail() {

        mDialog.setMessage("Posting....");

        final String user_name = mUserName.getText().toString().trim();
        final String country= mCountryEt.getText().toString().trim();
        final String city =mCityEt.getText().toString().trim();
        final String user_phone = mUserPhone.getText().toString().trim();


        if(!TextUtils.isEmpty(user_name) && !TextUtils.isEmpty(country) && !TextUtils.isEmpty(city) && !TextUtils.isEmpty(user_phone) && mImageUri != null) {

            mDialog.show();

            final StorageReference filepath = mStorage.child("User_Images").child(mImageUri.getLastPathSegment());

            Task<Uri> urlTask = filepath.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        String image = downloadUri.toString();

                        Map<String,Object> user_info = new HashMap<>();

                        user_info.put("name",user_name);
                        user_info.put("country",country);
                        user_info.put("city",city);
                        user_info.put("phone",user_phone);
                        user_info.put("image",image);



                        mDatabaseUsers.updateChildren(user_info).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                mDialog.dismiss();

                                Intent intent = new Intent(AccountSetupActivity.this, MapsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });


                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }else{

            Toast.makeText(AccountSetupActivity.this,"Fill in the blanks",Toast.LENGTH_LONG).show();

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK ){

            if(data == null){
                Toast.makeText(AccountSetupActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }else {
                mImageUri = data.getData();


                CropImage.activity(mImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .setMinCropWindowSize(500,500)
                        .start(this);

            }

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                mDisplayImageBtn.setImageURI(resultUri);

                File thumb_filePath = new File(resultUri.getPath());


                try {
                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .compressToBitmap(thumb_filePath);


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    thumb_byte = baos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }





























}
