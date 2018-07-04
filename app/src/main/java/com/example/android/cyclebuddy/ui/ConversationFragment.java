package com.example.android.cyclebuddy.ui;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.ViewProfileActivity;
import com.example.android.cyclebuddy.helpers.CircularImageTransform;
import com.example.android.cyclebuddy.model.Message;
import com.example.android.cyclebuddy.model.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationFragment extends android.app.Fragment implements View.OnClickListener {

    @BindView(R.id.conversation_list_view)
    ListView mListView;
    @BindView(R.id.message_edit_text)
    EditText mMessageEditText;
    @BindView(R.id.send_message)
    Button mSendButton;

    private FirebaseListAdapter<Message> mMessageListAdapter;

    private String currentUserID;
    private String conversationBuddyID;
    private String pictureUUID;
    private String convoPushID;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mConversationDBReference;
    private DatabaseReference mMessagesDBReference;
    private ChildEventListener mChildEventListener;

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final String CONVERSATION_UID = "conversation UID";


    public ConversationFragment() {
        // Required empty public constructor
    }

    public static ConversationFragment newInstance() {
        ConversationFragment fragment = new ConversationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mConversationDBReference = mFirebaseDatabase.getReference().child("Conversations");

        if (getArguments() != null) {
            convoPushID = getArguments().getString(CONVERSATION_UID);
        }
        mMessagesDBReference = mFirebaseDatabase.getReference().child("Messages"+ "/" + convoPushID);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child("images");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        ButterKnife.bind(this, view);
        mSendButton.setOnClickListener(this);
        addListeners();
        showMessages();

        return view;
    }

    @Override
    public void onClick(View view) {
        sendMessage();
    }

//    private void getUserInfo(String userID){
//        final DatabaseReference usersDBReference = mFirebaseDatabase.getReference().child("Users").child(userID);
//        final String user;
//        usersDBReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
//                user = userProfile.getUser();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//    }

    private void sendMessage(){
        final DatabaseReference pushRef = mMessagesDBReference.push();
        final String pushKey = pushRef.getKey();

        String sentMessage = mMessageEditText.getText().toString();

        //create new message object
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        Message message = new Message(currentUserID, sentMessage, timestamp);

        //put message object through HashMap for adding to Messages section of database
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String,Object> messageObject = (HashMap<String, Object>) new ObjectMapper()
                .convertValue(message, Map.class);
        messageItemMap.put("/" + pushKey, messageObject);
        mMessagesDBReference.updateChildren(messageItemMap)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mMessageEditText.setText("");
                    }
                });
    }

    private void showMessages() {

        mMessageListAdapter = new FirebaseListAdapter<Message>(getActivity(), Message.class,
                R.layout.message_item, mMessagesDBReference) {
            @Override
            protected void populateView(View v, Message message, int position) {
                LinearLayout messageBubble = (LinearLayout) v.findViewById(R.id.message_bubble);
                final TextView userTv = (TextView) v.findViewById(R.id.user_sender);
                TextView messageTv = (TextView) v.findViewById(R.id.message_content);
                final ImageView leftImage = (ImageView) v.findViewById(R.id.leftMessagePic);
                final ImageView rightImage = (ImageView) v.findViewById(R.id.rightMessagePic);
                LinearLayout userAndMessage = (LinearLayout) v.findViewById(R.id.user_and_message);

                messageTv.setText(message.getMessage());
                final String messageUserID = message.getUserID();

                DatabaseReference userDbRef = mFirebaseDatabase.getReference().child("Users").child(messageUserID);
                userDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                        userTv.setText(userProfile.getUser());
                        pictureUUID = userProfile.getPhotoUrl();

                        //download the saved image
                        StorageReference rightDownloadRef = mStorageReference.child(messageUserID).child(pictureUUID);
                        // Load the image using Glide
                        Glide.with(getContext())
                                .using(new FirebaseImageLoader())
                                .load(rightDownloadRef)
                                .transform(new CircularImageTransform(getContext()))
                                .into(rightImage);

                        //download the saved image
                        StorageReference leftDownloadRef = mStorageReference.child(messageUserID).child(pictureUUID);
                        // Load the image using Glide
                        Glide.with(getContext())
                                .using(new FirebaseImageLoader())
                                .load(leftDownloadRef)
                                .transform(new CircularImageTransform(getContext()))
                                .into(leftImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                if (messageUserID.equals(currentUserID)) {
                    messageBubble.setGravity(Gravity.RIGHT);
                    leftImage.setVisibility(View.GONE);
                    rightImage.setVisibility(View.VISIBLE);
                    userAndMessage.setBackgroundResource(R.drawable.speechbubbleorange);

                } else {

                    messageBubble.setGravity(Gravity.LEFT);
                    leftImage.setVisibility(View.VISIBLE);
                    rightImage.setVisibility(View.GONE);
                    userAndMessage.setBackgroundResource(R.drawable.speechbubblepurple);
                }
            }
        };

        mListView.setAdapter(mMessageListAdapter);
    }

    private void addListeners(){
        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
    }
}
