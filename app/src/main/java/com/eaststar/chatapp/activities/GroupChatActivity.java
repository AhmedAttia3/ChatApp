package com.eaststar.chatapp.activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.eaststar.chatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    DatabaseReference UserRef;
    String currentUserId;
    Toolbar toolbar;
    ScrollView scrollView;
    TextView groupChatTextDisplay;
    EditText messageInput;
    ImageButton sendMessage;
    String currentGroupName, currentDate, currentTime, currentUserName;
    DatabaseReference GrouptRef, GroupNameRef, GroupMessageKeyRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        currentGroupName = getIntent().getExtras().get("groupName").toString();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GrouptRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        GroupNameRef = GrouptRef.child(currentGroupName);

        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(currentGroupName);

        scrollView = findViewById(R.id.scrollView);
        groupChatTextDisplay = findViewById(R.id.groupChatTextDisplay);
        messageInput = findViewById(R.id.messageInput);
        sendMessage = findViewById(R.id.sendMessage);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageInput.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatActivity.this, "write message first...", Toast.LENGTH_SHORT).show();
                }else {
                    sendMessageToFirebase(message);
                    messageInput.setText("");
                }
            }
        });
        
        getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getGroupMessages();
    }

    private void getGroupMessages() {
        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
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

    private void DisplayMessages(DataSnapshot dataSnapshot) {
        Iterator iterable = dataSnapshot.getChildren().iterator();
        while (iterable.hasNext()){
            String chatDate = (String)((DataSnapshot)iterable.next()).getValue();
            String chatMessage = (String)((DataSnapshot)iterable.next()).getValue();
            String chatName = (String)((DataSnapshot)iterable.next()).getValue();
            String chatTime = (String)((DataSnapshot)iterable.next()).getValue();
            String chatUserName = (String)((DataSnapshot)iterable.next()).getValue();

            groupChatTextDisplay.append(chatUserName+"\n"+chatMessage+"\n"+chatDate+" "+chatTime+"\n\n\n");
        }
        scrollView.fullScroll(View.FOCUS_DOWN);

    }


    private void sendMessageToFirebase(String message) {

        String messageKey = GroupNameRef.push().getKey();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDateF = new SimpleDateFormat("dd MMM yyyy");
        currentDate = currentDateF.format(calendar.getTime());
        Calendar calendarT = Calendar.getInstance();
        SimpleDateFormat calendarTT = new SimpleDateFormat("a hh:mm");
        currentTime = calendarTT.format(calendarT.getTime());

        HashMap<String, Object> groupMessage = new HashMap<>();
        GroupNameRef.updateChildren(groupMessage);

        GroupMessageKeyRef = GroupNameRef.child(messageKey);

        HashMap<String, Object> messageInfoMap = new HashMap<>();
        messageInfoMap.put("name",currentUserId);
        messageInfoMap.put("message",message);
        messageInfoMap.put("date",currentDate);
        messageInfoMap.put("time",currentTime);
        messageInfoMap.put("user",currentUserName);

        GroupMessageKeyRef.updateChildren(messageInfoMap);


    }

    private void getUserInfo() {
        UserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserName = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
