package com.example.android.cyclebuddy;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.cyclebuddy.helpers.ContextPasser;
import com.example.android.cyclebuddy.model.OfferedRoute;
import com.example.android.cyclebuddy.model.UserProfile;
import com.example.android.cyclebuddy.ui.MessagesFragment;
import com.example.android.cyclebuddy.ui.SearchListFragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ViewProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_toolbar) Toolbar profileToolbar;
    @BindView(R.id.view_profile_image_view) ImageView profileImageView;
    @BindView(R.id.name_text_view) TextView nameTv;
    @BindView(R.id.buddy_type_text_view) TextView buddyTypeTv;
    @BindView(R.id.years_cycling_text_view) TextView yearsCyclingTv;
    @BindView(R.id.cycling_frequency_text_view) TextView cyclingFreqeuncyTv;
    @BindView(R.id.bio_text_view) TextView miniBioTv;
    @BindView(R.id.message_button) Button messageButton;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mRef;
    private StorageReference mStorageReference;
    private UserProfile mUserProfile;
    private String mSharedPrefUserID;
    private String mPictureUUID;
    private SharedPreferences mSharedPreferences;

    private OfferedRoute mSelectedRoute;
    private static final String PASSED_BUNDLE = "passed bundle";
    private static final String SELECTED_ROUTE = "selectedRoute";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(Html.fromHtml("<font color='#FFFFFF'> View Profile </font>"));
        setContentView(R.layout.activity_view_profile);
        ButterKnife.bind(this);

        setSupportActionBar(profileToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);

            //TODO: when opened from Search Splash, back button should lead back to the search list fragment
        }

        //get userID and photo UUID from shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Bundle routeBundle = getIntent().getBundleExtra(PASSED_BUNDLE);
        if(routeBundle != null) {
            mSelectedRoute = routeBundle.getParcelable(SELECTED_ROUTE);
            mSharedPrefUserID = mSelectedRoute.getUserID();
            enableMessageButton();
            } else {
            mSharedPrefUserID = mSharedPreferences.getString(getString(R.string.preference_user_ID),
                    "unsuccessful");
        }

        //get reference to user's section of database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference("Users").child(mSharedPrefUserID);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("images").child(mSharedPrefUserID);

        //download all other values
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserProfile = dataSnapshot.getValue(UserProfile.class);
                //set data to views
                if (mUserProfile != null) {
                    nameTv.setText(mUserProfile.getUser());
                    buddyTypeTv.setText(mUserProfile.getBuddyType());
                    yearsCyclingTv.setText(mUserProfile.getYearsCycling());
                    cyclingFreqeuncyTv.setText(mUserProfile.getCyclingFrequency());
                    miniBioTv.setText(mUserProfile.getMiniBio());
                    if(mUserProfile.getPhotoUrl() == null||mUserProfile.getPhotoUrl().isEmpty()){
                        // Load the image using Glide
                        Timber.v("No photo saved yet");
                     } else {
                        mPictureUUID = mUserProfile.getPhotoUrl();
                        downloadImage(mPictureUUID);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_profile:
                Intent startEditProfile = new Intent(this, EditProfileActivity.class);
                startActivity(startEditProfile);
                return true;
            case R.id.sign_out:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                startActivity(new Intent(ViewProfileActivity.this, MainActivity.class));
                                finish();
                        }
                     });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO: for signing in and out, two different users
        //get userID and photo UUID from shared preferences
        mSharedPrefUserID = mSharedPreferences.getString(getString(R.string.preference_user_ID),
                "unsuccessful");

        //download all other values
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserProfile = dataSnapshot.getValue(UserProfile.class);
                //set data to views
                if (mUserProfile != null) {
                    nameTv.setText(mUserProfile.getUser());
                    buddyTypeTv.setText(mUserProfile.getBuddyType());
                    yearsCyclingTv.setText(mUserProfile.getYearsCycling());
                    cyclingFreqeuncyTv.setText(mUserProfile.getCyclingFrequency());
                    miniBioTv.setText(mUserProfile.getMiniBio());
                    if(mUserProfile.getPhotoUrl() == null||mUserProfile.getPhotoUrl().isEmpty()){
                        Timber.v("No photo saved yet");
                    } else {
                        mPictureUUID = mUserProfile.getPhotoUrl();
                        downloadImage(mPictureUUID);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadImage(String pictureUUID){
        //download the saved image
        StorageReference downloadRef = mStorageReference.child(pictureUUID);

        // Load the image using Glide
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(downloadRef)
                .placeholder(R.drawable.ic_add_a_photo)
                .into(profileImageView);
    }

    public void enableMessageButton(){
        messageButton.setVisibility(View.VISIBLE);
    }
}
