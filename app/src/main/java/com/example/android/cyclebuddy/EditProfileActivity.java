package com.example.android.cyclebuddy;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {

    //TODO: enable writing to database
    //TODO: add changes listener with "Discard" dialogue

    //TODO: either open with a Uri (editing existing profile), or no Uri (new profile)
    //TODO: add setOnTouchListeners to all edit texts and buttons


    @BindView(R.id.edit_profile_toolbar) Toolbar editProfileToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        setSupportActionBar(editProfileToolbar);

        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);}
    }
}
