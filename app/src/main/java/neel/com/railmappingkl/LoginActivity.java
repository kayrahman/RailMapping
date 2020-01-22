package neel.com.railmappingkl;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import neel.com.railmappingkl.ui.MapsActivity;

import static neel.com.railmappingkl.helperUtils.FirebaseConstants.DATABASE_USER;

public class LoginActivity extends AppCompatActivity {

    private VideoView mVideoView;

    private EditText mEmailEt;
    private EditText mPasswordEt;
    private Button mLoginBtn;
    private Button mRegisterBtn;

    ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child(DATABASE_USER);

        mEmailEt = findViewById(R.id.et_ac_login_email);
        mPasswordEt = findViewById(R.id.et_ac_login_password);
        mLoginBtn = findViewById(R.id.btn_ac_login);
        mProgressBar = findViewById(R.id.pro_login_loading);

        mVideoView = findViewById(R.id.vv_ac_login);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.intro_vedio);
        mVideoView.setVideoURI(uri);
        mVideoView.start();

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signinWithEmailPass();

            }
        });


        mRegisterBtn = findViewById(R.id.btn_ac_login_register);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startRegistering();


            }
        });


    }


        private void signinWithEmailPass() {

            String email=mEmailEt.getText().toString().trim();
            String pass=mPasswordEt.getText().toString().trim();

            if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {

                mProgressBar.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                           // checkUserExist();



                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            String user_id = mAuth.getCurrentUser().getUid();

                            mUserRef.child(user_id).child("device_token").setValue(deviceToken);
                            mUserRef.child(user_id).child("email").setValue(mAuth.getCurrentUser().getEmail());

                          //  updateDeviceTokenDatabase(deviceToken);

                            Intent setupIntent = new Intent(LoginActivity.this, MapsActivity.class);
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(setupIntent);
                            finish();



                            mProgressBar.setVisibility(View.INVISIBLE);

                        } else {

                            // Toast.makeText(LoginActivity.this, "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();


                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(LoginActivity.this, "Check email and password", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }else{

                Toast.makeText(this, "Fields are empty", Toast.LENGTH_SHORT).show();
            }
        }








    private void startRegistering() {

        String email = mEmailEt.getText().toString().trim();
        String pass = mPasswordEt   .getText().toString().trim();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass)) {

            mProgressBar.setVisibility(View.VISIBLE);

            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {


                        final String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference ref = mUserRef.child(user_id);

                        final String device_token = FirebaseInstanceId.getInstance().getToken();

                        HashMap<String, String> userDetail = new HashMap<String, String>();
                        userDetail.put("device_token", device_token);
                        userDetail.put("email", mAuth.getCurrentUser().getEmail());


                        ref.setValue(userDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {



                                    mProgressBar.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(LoginActivity.this, AccountSetupActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }

                        });


                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(LoginActivity.this, "User with this email already exist.", Toast.LENGTH_SHORT).show();
                        } else {


                            Toast.makeText(LoginActivity.this, "Something's wrong", Toast.LENGTH_LONG).show();

                        }

                        mProgressBar.setVisibility(View.INVISIBLE);
                    }


                }
            });
        }

        else {

            mProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this, "Check email and password", Toast.LENGTH_SHORT).show();



        }

    }


}
