package com.example.android.cyclebuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.cyclebuddy.helpers.BottomNavigationHelper;
import com.example.android.cyclebuddy.ui.MessagesFragment;
import com.example.android.cyclebuddy.ui.OfferFragment;
import com.example.android.cyclebuddy.ui.RideFragment;
import com.example.android.cyclebuddy.ui.SearchFragment;
import com.firebase.ui.auth.AuthUI;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RideFragment.OnNavigationItemChanged {

    @BindView(R.id.navigation)BottomNavigationView navigation;
    @BindView(R.id.main_toolbar) Toolbar mainToolbar;
    private FragmentManager fragmentManager;
    private Bundle mReceivedExtras;

    private String mUsername;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mainToolbar);

        //set up Bottom Navigation
        BottomNavigationHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //inflate initial fragment
        if(savedInstanceState != null){
            return;
        }

        if(getIntent().getExtras() != null){
            mReceivedExtras = getIntent().getExtras();
            //TODO: set these extras as arguments to other fragments
        }

        fragmentManager = getFragmentManager();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, RideFragment.newInstance());
        transaction.addToBackStack(null);
        transaction.commit();

        //set up Firebase Authentication
        mUsername = ANONYMOUS;
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //onSignedInInitialize(user.getDisplayName());
                    mUsername = user.getDisplayName();
                } else {
                    //onSignedOutCleanup();
                    mUsername = ANONYMOUS;
                    //start the default Authentication UI for signing in
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.view_own_profile:
                Intent startProfileActivity = new Intent(this, ViewProfileActivity.class);
                startActivity(startProfileActivity);
                //TODO: send an intent data so that the right version of view profile will open
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }    }

    @Override
    protected void onPause() {
        super.onPause();
        mAuth.removeAuthStateListener(mAuthStateListener);
        //detachDatabaseReadListener();
        //mMessageAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    //set up BottomNavigation listener to inflate the necessary fragment
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_ride:
                    selectedFragment = RideFragment.newInstance();
                    break;
                case R.id.navigation_search:
                    selectedFragment = SearchFragment.newInstance();
                    break;
                case R.id.navigation_offer:
                    selectedFragment = OfferFragment.newInstance();
                    break;
                case R.id.navigation_messages:
                    selectedFragment = MessagesFragment.newInstance();
                    break;
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, selectedFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }
    };

    @Override
    public void changeHighlightedIcon(int menuItemId) {
        View view = navigation.findViewById(menuItemId);
        view.performClick();
        //TODO: when pressing back, make sure the correct icon is highlighted
    }

    //get result from Authentication UI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Sign in cancelled.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}

