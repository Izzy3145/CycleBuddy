package com.example.android.cyclebuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.cyclebuddy.helpers.CircularImageTransform;
import com.example.android.cyclebuddy.helpers.Constants;
import com.example.android.cyclebuddy.model.Message;
import com.example.android.cyclebuddy.model.MessageSummary;
import com.example.android.cyclebuddy.model.OfferedRoute;
import com.example.android.cyclebuddy.model.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ViewProfileActivity extends AppCompatActivity {

    @BindView(R.id.profile_toolbar)
    Toolbar profileToolbar;
    @BindView(R.id.view_profile_image_view)
    ImageView profileImageView;
    @BindView(R.id.name_text_view)
    TextView nameTv;
    @BindView(R.id.buddy_type_text_view)
    TextView buddyTypeTv;
    @BindView(R.id.years_cycling_text_view)
    TextView yearsCyclingTv;
    @BindView(R.id.cycling_frequency_text_view)
    TextView cyclingFrequencyTv;
    @BindView(R.id.bio_text_view)
    TextView miniBioTv;
    @BindView(R.id.bio_text_view_header)
    TextView miniBioHeaderTv;
    @BindView(R.id.message_button)
    Button messageButton;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mUsersDatabaseRef;
    private StorageReference mStorageReference;
    private UserProfile mUserProfile;
    private String mSharedPrefUserID;
    private String mPictureUUID;
    private MessageSummary messageSummary;
    private SharedPreferences mSharedPreferences;

    private OfferedRoute mSelectedRoute;
    private static final String PASSED_BUNDLE = "passed bundle";
    private static final String SELECTED_ROUTE = "selectedRoute";
    private static final String CONVO_PUSH_KEY = "convo_push_key";
    private static final String NO_ENTRY = "empty";


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
            }

        //get userID and photo UUID from shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //get intents
        Bundle routeBundle = getIntent().getBundleExtra(PASSED_BUNDLE);
        if (routeBundle != null) {
            mSelectedRoute = routeBundle.getParcelable(SELECTED_ROUTE);
            //if this activity was started from a search result, the userID used to create this activity
            //is that of the person who has offered the route
            mSharedPrefUserID = mSelectedRoute.getUserID();
            enableMessageButton(mSharedPrefUserID);
        } else {
            mSharedPrefUserID = mSharedPreferences.getString(getString(R.string.preference_user_ID),
                    "unsuccessful");
        }

        //initialise member variables
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersDatabaseRef = mFirebaseDatabase.getReference(Constants.USERS_PATH).child(mSharedPrefUserID);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child(Constants.IMAGES_PATH).child(mSharedPrefUserID);
        messageSummary = new MessageSummary("","","");

        //download all other values
        mUsersDatabaseRef.addValueEventListener(profileDataListener);
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
        mSharedPrefUserID = mSharedPreferences.getString(getString(R.string.preference_user_ID), "unsuccessful");
        //download all other values
        mUsersDatabaseRef.addValueEventListener(profileDataListener);
    }

    private void downloadImage(String pictureUUID) {
        //download the saved image
        StorageReference downloadRef = mStorageReference.child(pictureUUID);
        // Load the image using Glide
        Glide.with(this)
                .using(new FirebaseImageLoader())
                .load(downloadRef)
                .placeholder(R.drawable.ic_add_a_photo)
                .transform(new CircularImageTransform(ViewProfileActivity.this))
                .into(profileImageView);
    }

    private void enableMessageButton(final String buddyUserID) {
        //if another user's profile is being viewed, enable the send button
        messageButton.setVisibility(View.VISIBLE);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create new conversation and get pushKey
                String newConvoPushKey = createNewConversation(buddyUserID);
                //send pushKey to Convo fragment, via the MainActivity
                Intent backToMainIntent = new Intent(ViewProfileActivity.this, MainActivity.class);
                backToMainIntent.putExtra(CONVO_PUSH_KEY, newConvoPushKey);
                startActivity(backToMainIntent);
            }
        });
    }

    private String createNewConversation(String buddyID){
        final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference convoRef = mFirebaseDatabase.getReference(Constants.CONVERSATIONS_PATH);
        final DatabaseReference pushRef = convoRef.push();
        final String pushKey = pushRef.getKey();

        //add message Summary to the conversations branch of the database, using unique pushID
        messageSummary.setConvoUID(pushKey);
        messageSummary.setBuddyOneID(currentUserID);
        messageSummary.setBuddyTwoID(buddyID);
        HashMap<String, Object> convoItemMap = new HashMap<String, Object>();
        HashMap<String,Object> convoObject = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(messageSummary, Map.class);
        convoItemMap.put("/" + pushKey, convoObject);
        convoRef.updateChildren(convoItemMap);

        //also send this hashmap to the current user's profile section of the database
        convoItemMap = new HashMap<String, Object>();
        convoItemMap.put("/chats/" + pushKey, convoObject);
        final DatabaseReference mCurrentUserRef = mFirebaseDatabase.getReference(Constants.USERS_PATH).child(currentUserID);
        final DatabaseReference mBuddyRef = mFirebaseDatabase.getReference(Constants.USERS_PATH).child(buddyID);
        mCurrentUserRef.updateChildren(convoItemMap);
        mBuddyRef.updateChildren(convoItemMap);

        //create a new branch within the messages branch of the database, using unique pushID
        Message emptyMessage = new Message(currentUserID, "Hey Buddy", "");
        final DatabaseReference newSetOfMessagesRef = mFirebaseDatabase.getReference()
                .child(Constants.MESSAGES_PATH).child(pushKey);
        final DatabaseReference msgPush = newSetOfMessagesRef.push();
        final String msgPushKey = msgPush.getKey();
        newSetOfMessagesRef.child(msgPushKey).setValue(emptyMessage);

        return pushKey;
    }

    private String getSummaryText(String dbString) {
        if (dbString.equals(getResources().getString(R.string.be_a_buddy))) {
            return getResources().getString(R.string.vp_want_cycle_buddy);
        } else if (dbString.equals(getResources().getString(R.string.need_a_buddy))) {
            return getResources().getString(R.string.vp_need_cycle_buddy);
        } else if (dbString.equals(getResources().getString(R.string.both))) {
            return getResources().getString(R.string.vp_both_want_need);
        } else if (dbString.equals(getResources().getString(R.string.never))) {
            return getResources().getString(R.string.vp_never_cycled_in_london);
        } else if (dbString.equals(getResources().getString(R.string.less_than_year))) {
            return getResources().getString(R.string.vp_cycling_less_than_year);
        } else if (dbString.equals(getResources().getString(R.string.year_or_so))) {
            return getResources().getString(R.string.vp_cycling_year_or_so);
        } else if (dbString.equals(getResources().getString(R.string.few_years))) {
            return getResources().getString(R.string.vp_cycling_few_years);
        } else if (dbString.equals(getResources().getString(R.string.very_long))) {
            return getResources().getString(R.string.vp_cycling_long_time);
        } else if (dbString.equals(getResources().getString(R.string.everyday))) {
            return getResources().getString(R.string.vp_everyday);
        } else if (dbString.equals(getResources().getString(R.string.several_days))) {
            return getResources().getString(R.string.vp_several_days);
        } else if (dbString.equals(getResources().getString(R.string.once_week))) {
            return getResources().getString(R.string.vp_once_week);
        } else if (dbString.equals(getResources().getString(R.string.once_month))) {
            return getResources().getString(R.string.vp_once_month);
        } else if (dbString.equals(getResources().getString(R.string.seldom_cycle))) {
            return getResources().getString(R.string.vp_seldom_cycle);
        } else {
            return NO_ENTRY;
        }
    }

    //method for setting up UI based on userProfile information
    ValueEventListener profileDataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            mUserProfile = dataSnapshot.getValue(UserProfile.class);
            //set data to views
            if (mUserProfile != null) {
                //set views to stored data
                nameTv.setText(mUserProfile.getUser());
                if(getSummaryText(mUserProfile.getBuddyType()).equals(NO_ENTRY)){
                    buddyTypeTv.setVisibility(View.GONE);
                } else {
                    buddyTypeTv.setText(getSummaryText(mUserProfile.getBuddyType()));
                }
                if(getSummaryText(mUserProfile.getYearsCycling()).equals(NO_ENTRY)){
                    yearsCyclingTv.setVisibility(View.GONE);
                } else {
                    yearsCyclingTv.setText(getSummaryText(mUserProfile.getYearsCycling()));
                }
                if(getSummaryText(mUserProfile.getCyclingFrequency()).equals(NO_ENTRY)){
                    cyclingFrequencyTv.setVisibility(View.GONE);
                } else {
                    cyclingFrequencyTv.setText(getSummaryText(mUserProfile.getCyclingFrequency()));
                }
                miniBioTv.setText(mUserProfile.getMiniBio());
                if (mUserProfile.getPhotoUrl() == null || mUserProfile.getPhotoUrl().isEmpty()) {
                    Timber.v("No photo saved yet");
                } else {
                    mPictureUUID = mUserProfile.getPhotoUrl();
                    downloadImage(mPictureUUID);
                }
            }}

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };
}
