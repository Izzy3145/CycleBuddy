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
import com.example.android.cyclebuddy.model.UserProfile;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class EditProfileActivity extends AppCompatActivity {

    //TODO: either open with a Uri (editing existing profile), or no Uri (new profile)

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
    @BindView(R.id.upload_button)
    Button uploadButton;
    @BindView(R.id.save_button)
    Button saveButton;
    @BindView(R.id.name_edit_text)
    EditText nameEditText;
    @BindView(R.id.bio_edit_text)
    EditText bioEditText;
    private FirebaseDatabase mFirebaseDatabase;
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
    private boolean mProfileHasChanged = false;
    //set up the on TouchListener method, to be used later in onCreate
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

        //set up writable database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        //get the user ID and picture ID from shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefUserID = mSharedPreferences.getString(getString(R.string.preference_user_ID),
                "unsuccessful");

        //get reference to this user's part of the database
        mProfileDatabaseReference = mFirebaseDatabase.getReference().child("Users").child(mSharedPrefUserID);
        mStorageReference = mFirebaseStorage.getReference().child("images").child(mSharedPrefUserID);

        //download existing values
        mProfileDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserProfile = dataSnapshot.getValue(UserProfile.class);
                //set data to views
                if (mUserProfile != null) {
                    nameEditText.setText(mUserProfile.getUser());
                    //buddyTypeTv.setText(mUserProfile.getBuddyType());
                    //yearsCyclingTv.setText(mUserProfile.getYearsCycling());
                    //cyclingFreqeuncyTv.setText(mUserProfile.getCyclingFrequency());
                    bioEditText.setText(mUserProfile.getMiniBio());
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

        //set up photo intent
        profileImageView.setClickable(true);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePictureIntent();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        setupBuddySpinner();
        setupYearsSpinner();
        setupFrequencySpinner();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfile newUser;
                mName = nameEditText.getText().toString();
                mMiniBio = bioEditText.getText().toString();
                //TODO: remove photoUrl
                if (mPictureUUID == null || mPictureUUID.isEmpty()) {
                    newUser = new UserProfile(mSharedPrefUserID, mName, mBuddyType, mYearsCycling,
                            mCyclingFrequency, mMiniBio);
                } else {
                    newUser = new UserProfile(mSharedPrefUserID, mName, mBuddyType, mMiniBio, mYearsCycling,
                            mCyclingFrequency, mPictureUUID);
                }
                mProfileDatabaseReference.setValue(newUser);
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

        //TODO: could remove?
        mSharedPrefUserID = mSharedPreferences.getString(getString(R.string.preference_user_ID),
                "unsuccessful");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // If some fields have changed, setup a dialog to warn the user.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // "Discard" button clicked, close the current activity.
                                finish();
                            }
                        };
                // Show dialog that there are unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

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

            } catch (IOException e) {
                e.printStackTrace();
                Timber.v("Problem with getting picture");
            }
        }
    }

    private void uploadImage() {
        if (selectedImageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            mPictureUUID = UUID.randomUUID().toString();

            StorageReference ref = mStorageReference.child(mPictureUUID);
            //TODO: delete previous photos
            ref.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }

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
                    if (selection.equals(getString(R.string.not_since_child))) {
                        mYearsCycling = getString(R.string.not_since_child);
                    } else if (selection.equals(getString(R.string.zero_to_one))) {
                        mYearsCycling = getString(R.string.zero_to_one);
                    } else if (selection.equals(getString(R.string.one_to_three))) {
                        mYearsCycling = getString(R.string.one_to_three);
                    } else if (selection.equals(getString(R.string.very_long))) {
                        mYearsCycling = getString(R.string.very_long);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mYearsCycling = getString(R.string.none_selected);
            }
        });
    }

    //set up years spinner
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
        // If no fields have  changed, continue with handling back button press
        if (!mProfileHasChanged) {
            super.onBackPressed();
            return;
        }
        // If some fields have changed, setup a dialog to warn the user.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // "Discard" button clicked, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    //unsaved changes dialogue, to sometimes be used when back button is pressed
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
}
