package com.eaststar.chatapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eaststar.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    String userID, currentUserID, currentStat;

    CircularImageView userImage;
    TextView userName, userStatus;
    Button sedMessage, cancelMessage;
    FirebaseAuth mAuth;

    DatabaseReference UserRef, ChatRequestRef, ContactsRequestRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRequestRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        userStatus = findViewById(R.id.userStatus);
        sedMessage = findViewById(R.id.sedMessage);
        cancelMessage = findViewById(R.id.cancelMessage);
        currentStat = "new";
        userID = getIntent().getExtras().get("userID").toString();

        sedMessage.setEnabled(false);
        RetrieveUserInfo();
    }

    private void RetrieveUserInfo() {
        UserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("name").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();
                    userName.setText(username);
                    userStatus.setText(userstatus);
                    Glide.with(ProfileActivity.this).load(R.drawable.profile_image).into(userImage);
                    if (dataSnapshot.hasChild("image")) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Glide.with(ProfileActivity.this).load(image).placeholder(R.drawable.profile_image).into(userImage);

                    }
                    ManageChatRequests();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void ManageChatRequests() {
        ChatRequestRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userID)) {
                    String request_type = dataSnapshot.child(userID).child("request_type").getValue().toString();
                    if (request_type.equals("send")) {
                        sedMessage.setVisibility(View.VISIBLE);
                        sedMessage.setEnabled(true);
                        currentStat = "request_send";
                        sedMessage.setText("Cancel Chat Request");
                    } else if (request_type.equals("received")) {
                        currentStat = "request_received";
                        sedMessage.setText("Accept Chat Request");
                        cancelMessage.setVisibility(View.VISIBLE);
                        cancelMessage.setEnabled(true);
                        cancelMessage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CancelChatRequest();
                            }
                        });
                    }

                } else {
                    ContactsRequestRef.child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(userID)) {
                                currentStat = "friends";
                                sedMessage.setText("Remove From Contacts");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (currentUserID.equals(userID)) {
            sedMessage.setVisibility(View.GONE);
        } else {
            sedMessage.setVisibility(View.VISIBLE);
            sedMessage.setEnabled(true);
            sedMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentStat.equals("new")) {
                        sendChatRequest();
                    } else if (currentStat.equals("request_send")) {
                        CancelChatRequest();
                    } else if (currentStat.equals("request_received")) {
                        AcceptChatRequest();
                    } else if (currentStat.equals("friends")) {
                        RemoveSpecificContacts();
                    }
                }
            });
        }
    }

    private void RemoveSpecificContacts() {
        ContactsRequestRef.child(currentUserID).child(userID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ContactsRequestRef.child(userID).child(currentUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sedMessage.setEnabled(true);
                                                currentStat = "new";
                                                sedMessage.setText("Send Message");
                                                cancelMessage.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptChatRequest() {
        ContactsRequestRef.child(currentUserID).child(userID).child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ContactsRequestRef.child(userID).child(currentUserID).child("Contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                ChatRequestRef.child(currentUserID).child(userID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    ChatRequestRef.child(userID).child(currentUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        sedMessage.setEnabled(true);
                                                                                        cancelMessage.setVisibility(View.INVISIBLE);
                                                                                        currentStat = "friends";
                                                                                        sedMessage.setText("Remove From Contacts");
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelChatRequest() {
        ChatRequestRef.child(currentUserID).child(userID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ChatRequestRef.child(userID).child(currentUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sedMessage.setEnabled(true);
                                                currentStat = "new";
                                                sedMessage.setText("Send Message");
                                                cancelMessage.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendChatRequest() {
        ChatRequestRef.child(currentUserID).child(userID)
                .child("request_type").setValue("send")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ChatRequestRef.child(userID).child(currentUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sedMessage.setEnabled(true);
                                                currentStat = "request_send";
                                                sedMessage.setText("Cancel Chat Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
