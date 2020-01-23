package neel.com.railmappingkl.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import neel.com.railmappingkl.AccountSetupActivity;
import neel.com.railmappingkl.MainActivity;
import neel.com.railmappingkl.R;
import neel.com.railmappingkl.model.PopularPlace;

public class PlaceDetailActivity extends AppCompatActivity {

    public static final String LIST_ITEM_KEY = "key";

    DatabaseReference mPopularPlaceRef;
    private ImageView mImageView;
    private TextView mTitleTv,mDescTv;

    TextView mIntroductionTv;
    TextView mRecommendationTv;
    TextView mGuideLineTv;
    TextView mBudgetTv;
    TextView mEstimatedTimeTv;

    private VideoView mPlaceVideoView;
    private ImageView mPlayVideoBtn;
    private TextView mVideoCurrentTimeTv, mVideoDurationTimerTv;
    private ProgressBar mVideoProgressBar;
    private ProgressBar mVideoBufferProgressBar;

    private Uri mVideoUri;
    private boolean isVideoPlaying =false;

    private int mVideoCurrentTime = 0;
    private int mVideoDurationTime = 0;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_maps:

                    startActivity(new Intent(PlaceDetailActivity.this,MapsActivity.class));

                    return true;

                case R.id.navigation_places:


                    return true;
                case R.id.navigation_profile:

                    startActivity(new Intent(PlaceDetailActivity.this, AccountSetupActivity.class));

                    return true;
                case R.id.navigation_planner:

                    startActivity(new Intent(PlaceDetailActivity.this, MainActivity.class));

                    return true;
            }
            return false;
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        mImageView= findViewById(R.id.iv_ac_place_detail);
        mTitleTv = findViewById(R.id.tv_ac_place_detail_title);
       // mDescTv = findViewById(R.id.tv_ac_place_detail_desc);

        mIntroductionTv = findViewById(R.id.tv_ac_place_detail_introduction);
        mRecommendationTv = findViewById(R.id.tv_ac_place_detail_recommendation);
        mGuideLineTv = findViewById(R.id.tv_ac_place_detail_guideline);
        mBudgetTv = findViewById(R.id.tv_ac_place_detail_budget);
        mEstimatedTimeTv =findViewById(R.id.tv_ac_place_detail_estimated_time);


        //

        mPlaceVideoView = findViewById(R.id.vdo_view_ac_place_detail);
        mPlayVideoBtn = findViewById(R.id.iv_ac_place_detail_play_vdo);
        mVideoCurrentTimeTv = findViewById(R.id.tv_ac_place_detail_vdo_current_time);
        mVideoDurationTimerTv = findViewById(R.id.tv_ac_place_detail_vdo_duration);
        mVideoProgressBar = findViewById(R.id.pb_ac_place_detail_video_progress);
        mVideoProgressBar.setMax(100);
        mVideoBufferProgressBar = findViewById(R.id.pb_ac_place_detail_buffer_progress);


        if(getIntent() != null) {

            PopularPlace popularPlace = (PopularPlace) getIntent().getExtras().getParcelable(LIST_ITEM_KEY);

           // Log.i("INTENT_EXTRA",popularPlace.getPhoto_utl());


            mTitleTv.setText(popularPlace.getTitle());
//            mDescTv.setText(popularPlace.getDescription());

            mIntroductionTv.setText(popularPlace.getIntroduction());
            mRecommendationTv.setText(popularPlace.getRecommendation());
            mGuideLineTv.setText(popularPlace.getGuideline());
            mBudgetTv.setText(popularPlace.getBudget());
            mEstimatedTimeTv.setText(popularPlace.getEstimated_time());


            String video_url = popularPlace.getVideo_url();

            mVideoUri = Uri.parse(video_url);

            startVideo(mVideoUri);
            new VideoProgession().execute();


            Log.i("video_url",video_url);

            Picasso.with(this)
                    .load(popularPlace.getPhoto_utl())
                    .into(mImageView);

            /***************VIDEO VIEW LISTENERS**********************/

            mPlayVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isVideoPlaying){
                        isVideoPlaying = false;
                        mPlaceVideoView.pause();
                        mPlayVideoBtn.setImageResource(R.drawable.ic_play_btn);

                    }else{
                        isVideoPlaying = true;
                        mPlaceVideoView.start();
                        mPlayVideoBtn.setImageResource(R.drawable.ic_pause_btn);
                    }
                }
            });


            mPlaceVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                    if(i == mediaPlayer.MEDIA_INFO_BUFFERING_START){
                        mVideoBufferProgressBar.setVisibility(View.VISIBLE);
                    }else if(i == mediaPlayer.MEDIA_INFO_BUFFERING_END){
                        mVideoBufferProgressBar.setVisibility(View.INVISIBLE);

                    }

                    return false;
                }
            });


            mPlaceVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {

                    mVideoDurationTime = mediaPlayer.getDuration()/1000;
                    String duration = String.format("%02d:%02d",mVideoDurationTime/60,mVideoDurationTime%60);
                    mVideoDurationTimerTv.setText(duration);

                }
            });

            mPlaceVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    mPlaceVideoView.start();

                }
            });

            /***************VIDEO VIEW LISTENERS**********************/


        }



        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);




    }

    private void startVideo(Uri videoUri) {

        mPlaceVideoView.setVideoURI(videoUri);
        mPlaceVideoView.requestFocus();
        mPlaceVideoView.start();
        isVideoPlaying = true;
        mPlayVideoBtn.setImageResource(R.drawable.ic_pause_btn);


    }


    @Override
    protected void onStop() {
        super.onStop();

        isVideoPlaying =false;

    }

    public class VideoProgession extends AsyncTask<Void,Integer,Void>{


        @Override
        protected Void doInBackground(Void... voids) {


            do {

                if (isVideoPlaying) {

                    mVideoCurrentTime = mPlaceVideoView.getCurrentPosition() / 1000;
                    publishProgress(mVideoCurrentTime);
                }


                }
                while (mVideoProgressBar.getProgress() <= 100) ;


                return null;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            try{
                int current_percentage = mVideoCurrentTime*100/mVideoDurationTime;
                mVideoProgressBar.setProgress(current_percentage);

                String current_string = String.format("%02d:%02d",values[0]/60,values[0]%60);
                mVideoCurrentTimeTv.setText(current_string);




            }catch (Exception e){
                e.printStackTrace();
            }




        }
    }


}
