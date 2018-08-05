package com.riseapp.android.cyclebuddy.ui;

import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.riseapp.android.cyclebuddy.R;
import com.riseapp.android.cyclebuddy.helpers.CircularImageTransform;
import com.riseapp.android.cyclebuddy.helpers.Constants;
import com.riseapp.android.cyclebuddy.helpers.TimeUtils;
import com.riseapp.android.cyclebuddy.model.Message;
import com.riseapp.android.cyclebuddy.model.UserProfile;
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

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

//fragment to show conversation detail, with messages downloaded from Firebase database

public class ConversationFragment extends android.app.Fragment implements View.OnClickListener {

    @BindView(R.id.conversation_list_view)
    ListView mListView;
    @BindView(R.id.message_edit_text)
    EditText mMessageEditText;
    @BindView(R.id.send_message)
    Button mSendButton;

    private String currentUserID;
    private String pictureUUID;
    private String convoPushID;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDBReference;
    private FirebaseListAdapter<Message> mMessageListAdapter;

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
        getActivity().setTitle("Conversation");

        if (getArguments() != null) {
            convoPushID = getArguments().getString(CONVERSATION_UID);
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mMessagesDBReference = mFirebaseDatabase.getReference().child(Constants.MESSAGES_PATH).child(convoPushID);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child(Constants.IMAGES_PATH);
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

    private void sendMessage() {
        final DatabaseReference pushRef = mMessagesDBReference.push();
        final String pushKey = pushRef.getKey();

        String sentMessage = mMessageEditText.getText().toString();
        //create new message object
        Message message = new Message(currentUserID, sentMessage, Long.toString(System.currentTimeMillis()));

        //put message object through HashMap and add to Messages section of database
        HashMap<String, Object> messageItemMap = new HashMap<String, Object>();
        HashMap<String, Object> messageObject = (HashMap<String, Object>) new ObjectMapper()
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
        //set up adapter to display list of messages from database
        mMessageListAdapter = new FirebaseListAdapter<Message>(getActivity(), Message.class,
                R.layout.message_item, mMessagesDBReference) {
            @Override
            protected void populateView(View v, final Message message, final int position) {
                LinearLayout messageBubble = (LinearLayout) v.findViewById(R.id.message_bubble);
                //final TextView userTv = (TextView) v.findViewById(R.id.user_sender);
                TextView messageTv = (TextView) v.findViewById(R.id.message_content);
                TextView timeTv = (TextView)v.findViewById(R.id.message_time);
                final ImageView leftImage = (ImageView) v.findViewById(R.id.leftMessagePic);
                final ImageView rightImage = (ImageView) v.findViewById(R.id.rightMessagePic);
                LinearLayout userAndMessage = (LinearLayout) v.findViewById(R.id.user_and_message);

                //set message and other profile information to view
                messageTv.setText(message.getMessage());
                timeTv.setText(TimeUtils.displayTimeOrDate(Long.parseLong(message.getTimestamp())));
                final String messageUserID = message.getUserID();
                //get other profile information
                DatabaseReference userDbRef = mFirebaseDatabase.getReference().child(Constants.USERS_PATH)
                        .child(messageUserID);
                userDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                        pictureUUID = userProfile.getPhotoUrl();
                        if(pictureUUID != null) {
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
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                //set up UI according to who the sender was
                if (messageUserID.equals(currentUserID)) {
                    messageBubble.setGravity(Gravity.END);
                    leftImage.setVisibility(View.GONE);
                    rightImage.setVisibility(View.VISIBLE);
                    userAndMessage.setBackgroundResource(R.drawable.speechbubbleorange);
                } else {
                    messageBubble.setGravity(Gravity.START);
                    leftImage.setVisibility(View.VISIBLE);
                    rightImage.setVisibility(View.GONE);
                    userAndMessage.setBackgroundResource(R.drawable.speechbubblepurple);
                }
            }
        };

        mListView.setAdapter(mMessageListAdapter);
    }

    private void addListeners() {
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
