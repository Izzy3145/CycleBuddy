package com.example.android.cyclebuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.style.UpdateLayout;
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

import com.example.android.cyclebuddy.model.UserProfile;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class EditProfileActivity extends AppCompatActivity {

    //TODO: add changes listener with "Discard" dialogue

    //TODO: either open with a Uri (editing existing profile), or no Uri (new profile)
    //TODO: add setOnTouchListeners to all edit texts and buttons


    @BindView(R.id.edit_profile_toolbar) Toolbar editProfileToolbar;
    @BindView(R.id.profile_image_view) ImageView profileImageView;
    @BindView(R.id.spinner_buddy_type) Spinner buddyTypeSpinner;
    @BindView(R.id.save_button) Button saveButton;
    @BindView(R.id.name_edit_text) EditText nameEditText;
    @BindView(R.id.bio_edit_text) EditText bioEditText;


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mProfileDatabaseReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    private String mUserID;
    private String mName;
    private String mBuddyType;
    private String mMiniBio;
    private String mPhotoUrl = null;

    public static final String USER_ID_TO_EDIT = "user ID to edit";
    private static final int PICK_IMAGE = 1;

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
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        setSupportActionBar(editProfileToolbar);

        ActionBar ab = getSupportActionBar();
        if(ab != null){
            ab.setDisplayHomeAsUpEnabled(true);}

        Intent intent = getIntent();
        mUserID = intent.getStringExtra(USER_ID_TO_EDIT);

        //set up writable database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mProfileDatabaseReference = mFirebaseDatabase.getReference().child("Users").child(mUserID);
        //mStorageReference = mFirebaseStorage.getReference().child("profile_pics");

        //set up photo intent
        profileImageView.setClickable(true);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureIntent();
            }
        });

        setupSpinner();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfile newUser;
                mName = nameEditText.getText().toString();
                mMiniBio = bioEditText.getText().toString();

                if(mPhotoUrl == null){
                    newUser = new UserProfile(mUserID, mName, mBuddyType, mMiniBio);
                } else {
                    newUser = new UserProfile(mUserID, mName, mBuddyType, mMiniBio, mPhotoUrl);
                }
                mProfileDatabaseReference.push().setValue(newUser);
            }
        });

        profileImageView.setOnTouchListener(mTouchListener);
        nameEditText.setOnTouchListener(mTouchListener);
        bioEditText.setOnTouchListener(mTouchListener);
        buddyTypeSpinner.setOnTouchListener(mTouchListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
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

    private void takePictureIntent() {

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

            Uri selectedImageUri = data.getData();

            try {
                //save image to database
                //final StorageReference photoRef = mStorageReference.child(selectedImageUri.getLastPathSegment());
                //photoRef.putFile(selectedImageUri);
                //TODO: sort out saving photo to Cloud Storage

//                //  get the download Url
//                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                    @Override
//                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                        if (!task.isSuccessful()) {
//                            throw task.getException();
//                        }
//
//                        // Continue with the task to get the download URL
//                        return photoRef.getDownloadUrl();
//                    }
//                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        if (task.isSuccessful()) {
//                            Uri downloadUri = task.getResult();
                //mPhotoUrl = downloadUri.toString();
//
//                        } else {
//                            Toast.makeText(getApplicationContext(), getString(R.string.task_not_complete),
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

                //set image to image view
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                profileImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
                Timber.v("Problem with getting picture");
            }
        }
    }

    //set up spinner
    private void setupSpinner() {
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
