package com.example.android.cyclebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_toolbar) Toolbar profileToolbar;
    @BindView(R.id.view_profile_image_view) ImageView profileImageView;
    @BindView(R.id.name_text_view) TextView nameTv;
    @BindView(R.id.buddy_type_text_view) TextView buddyTypeTv;
    @BindView(R.id.bio_text_view) TextView miniBioTv;


    //TODO: enable reading from database
    private FirebaseDatabase mFirebaseDatabase;
    private String mUserID;

    public static final String USER_ID = "user ID";
    public static final String USER_ID_TO_EDIT = "user ID to edit";

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

        Intent intent = getIntent();
        mUserID = intent.getStringExtra(USER_ID);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference mRef = mFirebaseDatabase.getReference("Users").child("user");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userValue = String.valueOf(dataSnapshot.getValue());
                nameTv.setText(userValue);
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
                startEditProfile.putExtra(USER_ID_TO_EDIT, mUserID);
                startActivity(startEditProfile);
                return true;

            case R.id.sign_out:

                AuthUI.getInstance().signOut(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
