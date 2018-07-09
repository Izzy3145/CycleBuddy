package com.riseapp.android.cyclebuddy.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.riseapp.android.cyclebuddy.R;
import com.riseapp.android.cyclebuddy.helpers.CircularImageTransform;
import com.riseapp.android.cyclebuddy.helpers.Constants;
import com.riseapp.android.cyclebuddy.helpers.TimeUtils;
import com.riseapp.android.cyclebuddy.model.Message;
import com.riseapp.android.cyclebuddy.model.MessageSummary;
import com.riseapp.android.cyclebuddy.model.UserProfile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import butterknife.BindView;
import butterknife.ButterKnife;

//fragment to show a summary of existing conversations, with messages downloaded from Firebase database

public class MessageListFragment extends Fragment {

    @BindView(R.id.messages_overview_list_view)
    ListView mListView;
    @BindView(R.id.messages_empty_view)
    TextView mEmptyMessages;
    private FragmentManager fm;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatDbReference;
    private DatabaseReference mUserDbReference;
    private FirebaseListAdapter mMessageSummaryAdapter;
    private String currentUserID;
    private static final String CONVERSATION_UID = "conversation UID";
    private Context mContext;

    public MessageListFragment() {
        // Required empty public constructor
    }

    public static MessageListFragment newInstance() {
        MessageListFragment fragment = new MessageListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiseMemberVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this, view);
        loadExistingConversations(currentUserID);
        return view;
    }

    private void initialiseMemberVariables(){
        fm = getActivity().getFragmentManager();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserID = mFirebaseAuth.getCurrentUser().getUid();
        mUserDbReference = mFirebaseDatabase.getReference().child(Constants.USERS_PATH);
        mStorageReference = mFirebaseStorage.getReference().child(Constants.IMAGES_PATH);
        mContext = getActivity().getApplicationContext();
    }

    private void loadExistingConversations(String currentUser) {
        mChatDbReference = mFirebaseDatabase.getReference().child(Constants.USERS_PATH).child(currentUser)
        .child(Constants.CHATS_PATH);

        //set up the adapter to display list of message summary objects
        mMessageSummaryAdapter = new FirebaseListAdapter<MessageSummary>(getActivity(),
                MessageSummary.class, R.layout.message_list_item, mChatDbReference) {
            @Override
            protected void populateView(View v, MessageSummary msgSummary, int position) {
                    final ImageView buddyImage = (ImageView) v.findViewById(R.id.small_image_view_messages);
                    final TextView buddyName = (TextView) v.findViewById(R.id.contact_username);
                    final TextView lastMessage = (TextView) v.findViewById(R.id.most_recent_message);
                    final TextView lastTimestamp = (TextView) v.findViewById(R.id.last_message_time);

                    String buddyOne = msgSummary.getbuddyOneID();
                    String buddyTwo = msgSummary.getbuddyTwoID();
                    final String otherUser;
                    if (buddyOne.equals(currentUserID)) {
                        otherUser = buddyTwo;
                    } else {
                        otherUser = buddyOne;
                    }

                    //get and set username and buddy photo
                    final StorageReference userRef = mStorageReference.child(otherUser);
                    mUserDbReference.child(otherUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);

                            if (userProfile == null){
                                mListView.setVisibility(View.GONE);
                                mEmptyMessages.setVisibility(View.VISIBLE);
                            } else {
                                mListView.setVisibility(View.VISIBLE);
                                mEmptyMessages.setVisibility(View.GONE);
                            }

                            String buddyUsername = userProfile.getUser();
                            buddyName.setText(buddyUsername);
                            String pictureUUID = userProfile.getPhotoUrl();

                            if (pictureUUID != null) {
                                //download the saved image
                                StorageReference downloadRef = userRef.child(pictureUUID);
                                // Load the image using Glide
                                Glide.with(mContext)
                                        .using(new FirebaseImageLoader())
                                        .load(downloadRef)
                                        .transform(new CircularImageTransform(getContext()))
                                        .into(buddyImage);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    //set up listener on messages database, and set last message to tv
                    final DatabaseReference messageDbRef = mFirebaseDatabase.getReference().child(Constants.MESSAGES_PATH)
                            .child(msgSummary.getConvoUID());
                    messageDbRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Message newMessage = dataSnapshot.getValue(Message.class);
                            lastMessage.setText(newMessage.getMessage());
                            if (newMessage.getUserID().equals(otherUser)) {
                                lastMessage.setTypeface(null, Typeface.BOLD);
                            } else {
                                lastMessage.setTypeface(null, Typeface.NORMAL);
                            }

                            lastTimestamp.setText(TimeUtils.displayTimeOrDate(Long.parseLong(newMessage.getTimestamp())));
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }

        };

        mListView.setAdapter(mMessageSummaryAdapter);



        //upon clicking item, send convo pushKey to conversation fragment
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String messageLocation = mMessageSummaryAdapter.getRef(position).getKey();

                if (messageLocation != null) {
                    String convoPushID = mMessageSummaryAdapter.getRef(position).getKey();
                    ConversationFragment cf = ConversationFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString(CONVERSATION_UID, convoPushID);
                    cf.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, cf);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }
}
