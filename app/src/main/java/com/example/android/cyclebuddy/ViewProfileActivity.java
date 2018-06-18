package com.example.android.cyclebuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.cyclebuddy.model.UserProfile;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_toolbar) Toolbar profileToolbar;
    @BindView(R.id.view_profile_image_view) ImageView profileImageView;
    @BindView(R.id.name_text_view) TextView nameTv;
    @BindView(R.id.buddy_type_text_view) TextView buddyTypeTv;
    @BindView(R.id.years_cycling_text_view) TextView yearsCyclingTv;
    @BindView(R.id.cycling_frequency_text_view) TextView cyclingFreqeuncyTv;
    @BindView(R.id.bio_text_view) TextView miniBioTv;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private UserProfile mUserProfile;
    private String mSharedPrefUserID;
    private String mPictureUUID;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(profileToolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        //get userID and photo UUID from shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefUserID = mSharedPreferences.getString(getString(R.string.preference_user_ID),
                "unsuccessful");

        //get reference to user's section of database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = mFirebaseDatabase.getReference("Users").child(mSharedPrefUserID);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();

        downloadImage();

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
                    downloadImage();
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
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void downloadImage(){
        //download the saved image
        mPictureUUID = mSharedPreferences.getString(getString(R.string.preference_photo_UUID),
                "null");
        String imagePath = "images/"+ mPictureUUID;
        StorageReference downloadRef = mStorageReference.child(imagePath);

        // Load the image using Glide
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(downloadRef)
                .into(profileImageView);
    }
}
