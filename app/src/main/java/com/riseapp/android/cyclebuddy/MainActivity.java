package com.riseapp.android.cyclebuddy;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.riseapp.android.cyclebuddy.helpers.BottomNavigationHelper;
import com.riseapp.android.cyclebuddy.helpers.CustomTypefaceSpan;
import com.riseapp.android.cyclebuddy.ui.ConversationFragment;
import com.riseapp.android.cyclebuddy.ui.MessageListFragment;
import com.riseapp.android.cyclebuddy.ui.OfferFragment;
import com.riseapp.android.cyclebuddy.ui.RideFragment;
import com.riseapp.android.cyclebuddy.ui.SearchFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RideFragment.OnNavigationItemChanged {

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private static final String CONVO_PUSH_KEY = "convo_push_key";
    private static final String CONVERSATION_UID = "conversation UID";
    private final static String WIDGET_ICON = "widget icon";
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.main_toolbar)
    Toolbar mainToolbar;
    private FragmentManager fragmentManager;
    private String mUsername;
    private String userID;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
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
                    selectedFragment = MessageListFragment.newInstance();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MobileAds.initialize(this, getResources().getString(R.string.admob_initialise));

        setUpActionBar();

        //set up Bottom Navigation
        BottomNavigationHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //inflate initial fragment
        if (savedInstanceState != null) {
            return;
        }

        fragmentManager = getFragmentManager();

        //set up Firebase Authentication
        mUsername = ANONYMOUS;
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mUsername = user.getDisplayName();
                    userID = user.getUid();
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.preference_user_ID), userID);
                    editor.apply();
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

        Fragment fragmentToOpen = null;
        if (getIntent().getExtras() != null) {

            if (getIntent().getStringExtra(CONVO_PUSH_KEY) != null) {
                String convoPushID = getIntent().getStringExtra(CONVO_PUSH_KEY);
                ConversationFragment cf = ConversationFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(CONVERSATION_UID, convoPushID);
                cf.setArguments(bundle);
                fragmentToOpen = cf;
                navigation.setSelectedItemId(R.id.navigation_messages);

            } else if (getIntent().getIntExtra(WIDGET_ICON, 0) != 0) {
                int widgetIconPressed = getIntent().getIntExtra(WIDGET_ICON, 0);
                switch (widgetIconPressed) {
                    case 1:
                        navigation.setSelectedItemId(R.id.navigation_search);
                        fragmentToOpen = SearchFragment.newInstance();
                        break;
                    case 2:
                        navigation.setSelectedItemId(R.id.navigation_offer);
                        fragmentToOpen = OfferFragment.newInstance();
                        break;
                    case 3:
                        navigation.setSelectedItemId(R.id.navigation_messages);
                        fragmentToOpen = MessageListFragment.newInstance();
                        break;
                }
            }

        } else {
            fragmentToOpen = RideFragment.newInstance();
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentToOpen);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_own_profile:
                Intent startProfileActivity = new Intent(this, ViewProfileActivity.class);
                startActivity(startProfileActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuthStateListener != null) {
            mAuth.addAuthStateListener(mAuthStateListener);
        }
        fragmentManager = getFragmentManager();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void changeHighlightedIcon(int menuItemId) {
        View view = navigation.findViewById(menuItemId);
        view.performClick();
    }

    //get result from Authentication UI
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getResources().getString(R.string.signed_in),
                        Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getResources().getString(R.string.sign_in_cancelled),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void setUpActionBar() {
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(R.string.cycle_buddy);
//        getSupportActionBar().setIcon(R.mipmap.ic_cb_icon_tsp);
        //set action bar title with custom font
//        Typeface titleFont = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.roboto_regular));
//        SpannableStringBuilder SS = new SpannableStringBuilder(getResources().getString(R.string.cycle_buddy));
//        SS.setSpan(new CustomTypefaceSpan("", titleFont), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//        SS.setSpan(new ForegroundColorSpan(Color.WHITE), 0, SS.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        getSupportActionBar().setTitle(SS);
    }

}

