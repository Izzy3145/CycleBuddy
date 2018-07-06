package com.example.android.cyclebuddy;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.cyclebuddy.helpers.CircularImageTransform;
import com.example.android.cyclebuddy.helpers.Constants;
import com.example.android.cyclebuddy.model.UserProfile;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    @BindView(R.id.edit_profile_toolbar)
    Toolbar editProfileToolbar;
    @BindView(R.id.profile_image_view)
    ImageView profileImageView;
    @BindView(R.id.spinner_buddy_type)
    Spinner buddyTypeSpinner;
    @BindView(R.id.spinner_years_of_cycling)
    Spinner yearsCyclingSpinner;
    @BindView(R.id.spinner_cycling_frequency)
    Spinner cyclingFrequencySpinner;
    @BindView(R.id.save_button)
    Button saveButton;
    @BindView(R.id.name_edit_text)
    EditText nameEditText;
    @BindView(R.id.bio_edit_text)
    EditText bioEditText;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mProfileDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private String mName;
    private String mBuddyType;
    private String mYearsCycling;
    private String mCyclingFrequency;
    private String mMiniBio;
    private String mPhotoUrl = null;
    private String mSharedPrefUserID;
    private String mPictureUUID;
    private Uri selectedImageUri;
    private UserProfile mUserProfile;
    private SharedPreferences mSharedPreferences;
    private InterstitialAd mInterstitialAd;


    private boolean mProfileHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProfileHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(Html.fromHtml("<font color='#FFFFFF'> Edit Profile </font>"));
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        setSupportActionBar(editProfileToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        //initialise ad
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        initialiseMemberVariables();
        findExistingValues();

        //set up photo intent
        profileImageView.setClickable(true);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePictureIntent();
            }
        });

        setupBuddySpinner();
        setupYearsSpinner();
        setupFrequencySpinner();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //load ad
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Timber.d("The interstitial wasn't loaded yet.");
                }

                mName = nameEditText.getText().toString();
                mMiniBio = bioEditText.getText().toString();

                HashMap<String, Object> newUserMap = new HashMap<>();
                newUserMap.put("userID", mSharedPrefUserID);
                newUserMap.put("user", mName);
                newUserMap.put("buddyType", mBuddyType);
                newUserMap.put("yearsCycling", mYearsCycling);
                newUserMap.put("cyclingFrequency", mCyclingFrequency);
                newUserMap.put("miniBio", mMiniBio);
                newUserMap.put("photoUrl", mPictureUUID);

                mProfileDatabaseReference.updateChildren(newUserMap);

                finish();
            }
        });

        profileImageView.setOnTouchListener(mTouchListener);
        nameEditText.setOnTouchListener(mTouchListener);
        bioEditText.setOnTouchListener(mTouchListener);
        buddyTypeSpinner.setOnTouchListener(mTouchListener);
        yearsCyclingSpinner.setOnTouchListener(mTouchListener);
        cyclingFrequencySpinner.setOnTouchListener(mTouchListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSharedPrefUserID = mSharedPreferences.getString(getString(R.string.preference_user_ID),
                getResources().getString(R.string.unsuccessful));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mProfileHasChanged) {
                    super.onBackPressed();
                } else {
                    // If some fields have changed, setup a dialog to warn the user.
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // "Discard" mainscreen_button clicked, close the current activity.
                                    finish();
                                }
                            };
                    // Show dialog that there are unsaved changes
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    //methods relating to images
    private void choosePictureIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            selectedImageUri = data.getData();

            try {//set image to image view
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                profileImageView.setImageBitmap(bitmap);
                uploadImage();

            } catch (IOException e) {
                e.printStackTrace();
                Timber.v("Problem with getting picture");
            }
        }
    }

    private void uploadImage() {
        if (selectedImageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getResources().getString(R.string.uploading));
            progressDialog.show();

            mPictureUUID = UUID.randomUUID().toString();

            StorageReference ref = mStorageReference.child(mPictureUUID);
            ref.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.uploaded),
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, getResources().getString(R.string.failed) + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage(getResources().getString(R.string.uploaded) + (int) progress + "%");
                        }
                    });
        }
    }

    private void downloadImage(String pictureUUID) {
        //download the saved image
        StorageReference downloadRef = mStorageReference.child(pictureUUID);

        // Load the image using Glide
        Glide.with(getApplicationContext())
                .using(new FirebaseImageLoader())
                .load(downloadRef)
                .transform(new CircularImageTransform(EditProfileActivity.this))
                .placeholder(R.drawable.ic_add_a_photo)
                .into(profileImageView);
    }

    private void initialiseMemberVariables(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        //get the user ID and picture ID from shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefUserID = mSharedPreferences.getString(getString(R.string.preference_user_ID),
                getResources().getString(R.string.unsuccessful));
        //get reference to this user's part of the database
        mUserDatabaseReference = mFirebaseDatabase.getReference().child(Constants.USERS_PATH);
        mProfileDatabaseReference = mFirebaseDatabase.getReference().child(Constants.USERS_PATH).child(mSharedPrefUserID);
        mStorageReference = mFirebaseStorage.getReference().child(Constants.IMAGES_PATH).child(mSharedPrefUserID);
    }

    private void findExistingValues(){
        //download existing values and populate views
        mProfileDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserProfile = dataSnapshot.getValue(UserProfile.class);
                //set data to views
                if (mUserProfile != null) {
                    nameEditText.setText(mUserProfile.getUser());
                    buddyTypeSpinner.setSelection(getIndexMethod(mUserProfile.getBuddyType()));
                    yearsCyclingSpinner.setSelection(getIndexMethod(mUserProfile.getYearsCycling()));
                    cyclingFrequencySpinner.setSelection(getIndexMethod(mUserProfile.getCyclingFrequency()));
                    bioEditText.setText(mUserProfile.getMiniBio());
                    if (mUserProfile.getPhotoUrl() == null || mUserProfile.getPhotoUrl().isEmpty()) {
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

    //set up buddy type spinner
    private void setupBuddySpinner() {
        //set up array adapter to take spinner options
        ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_buddy_options, android.R.layout.simple_spinner_item);
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        //apply spinner to adapter
        buddyTypeSpinner.setAdapter(typeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        buddyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.be_a_buddy))) {
                        mBuddyType = getString(R.string.be_a_buddy);
                    } else if (selection.equals(getString(R.string.need_a_buddy))) {
                        mBuddyType = getString(R.string.need_a_buddy);
                    } else if (selection.equals(getString(R.string.both))) {
                        mBuddyType = getString(R.string.both);
                    } else if (selection.equals(getString(R.string.choose_spinner))) {
                        mBuddyType = "";
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mBuddyType = getString(R.string.none_selected);
            }
        });
    }

    //set up years spinner
    private void setupYearsSpinner() {
        //set up array adapter to take spinner options
        ArrayAdapter yearsSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_years_of_cycling, android.R.layout.simple_spinner_item);
        yearsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        //apply spinner to adapter
        yearsCyclingSpinner.setAdapter(yearsSpinnerAdapter);

        // Set the integer mSelected to the constant values
        yearsCyclingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.never))) {
                        mYearsCycling = getString(R.string.never);
                    } else if (selection.equals(getString(R.string.less_than_year))) {
                        mYearsCycling = getString(R.string.less_than_year);
                    } else if (selection.equals(getString(R.string.year_or_so))) {
                        mYearsCycling = getString(R.string.year_or_so);
                    } else if (selection.equals(getString(R.string.few_years))) {
                        mYearsCycling = getString(R.string.few_years);
                    } else if (selection.equals(getString(R.string.very_long))) {
                        mYearsCycling = getString(R.string.very_long);
                    } else if (selection.equals(getString(R.string.choose_spinner))) {
                        mYearsCycling = "";
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mYearsCycling = getString(R.string.none_selected);
            }
        });
    }

    //set up frequency spinner
    private void setupFrequencySpinner() {
        //set up array adapter to take spinner options
        ArrayAdapter yearsSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_cycling_frequency, android.R.layout.simple_spinner_item);
        yearsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        //apply spinner to adapter
        cyclingFrequencySpinner.setAdapter(yearsSpinnerAdapter);

        // Set the integer mSelected to the constant values
        cyclingFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.once_month))) {
                        mCyclingFrequency = getString(R.string.once_month);
                    } else if (selection.equals(getString(R.string.once_week))) {
                        mCyclingFrequency = getString(R.string.once_week);
                    } else if (selection.equals(getString(R.string.several_days))) {
                        mCyclingFrequency = getString(R.string.several_days);
                    } else if (selection.equals(getString(R.string.everyday))) {
                        mCyclingFrequency = getString(R.string.everyday);
                    } else if (selection.equals(getString(R.string.seldom_cycle))) {
                        mCyclingFrequency = getString(R.string.seldom_cycle);
                    } else if (selection.equals(getString(R.string.choose_spinner))) {
                        mCyclingFrequency = "";
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mYearsCycling = getString(R.string.none_selected);
            }
        });
    }

    //set up warning dialogues when there are unsaved changes
    @Override
    public void onBackPressed() {
        // If no fields have  changed, continue with handling back mainscreen_button press
        if (!mProfileHasChanged) {
            super.onBackPressed();
        } else {
            // If some fields have changed, setup a dialog to warn the user.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // "Discard" mainscreen_button clicked, close the current activity.
                            finish();
                        }
                    };
            // Show dialog that there are unsaved changes
            showUnsavedChangesDialog(discardButtonClickListener);
        }
    }

    //unsaved changes dialogue, to sometimes be used when back mainscreen_button is pressed
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set initial message
        builder.setMessage(R.string.unsaved_changes_message);
        //set positive and negative response messages, if negative, dismiss dialogue
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public int getIndexMethod(String spinnerString) {
        if (spinnerString.equals(getResources().getString(R.string.be_a_buddy)) ||
                spinnerString.equals(getResources().getString(R.string.never)) ||
                spinnerString.equals(getResources().getString(R.string.everyday))) {
            return 1;
        } else if (spinnerString.equals(getResources().getString(R.string.need_a_buddy)) ||
                spinnerString.equals(getResources().getString(R.string.less_than_year)) ||
                spinnerString.equals(getResources().getString(R.string.several_days))) {
            return 2;
        } else if (spinnerString.equals(getResources().getString(R.string.both)) ||
                spinnerString.equals(getResources().getString(R.string.year_or_so)) ||
                spinnerString.equals(getResources().getString(R.string.once_week))) {
            return 3;
        } else if (spinnerString.equals(getResources().getString(R.string.few_years)) ||
                spinnerString.equals(getResources().getString(R.string.once_month))) {
            return 4;
        } else if (spinnerString.equals(getResources().getString(R.string.very_long)) ||
                spinnerString.equals(getResources().getString(R.string.seldom_cycle))) {
            return 5;
        } else {
            return 0;
        }
    }

}
