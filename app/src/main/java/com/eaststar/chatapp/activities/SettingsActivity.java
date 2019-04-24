package com.eaststar.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eaststar.chatapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    CircularImageView userImage;
    EditText userName, userStatus;
    Button updateStatus;
    FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;
    DatabaseReference RootRef;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();
        currentUserId = mAuth.getCurrentUser().getUid();

        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        userStatus = findViewById(R.id.userStatus);
        updateStatus = findViewById(R.id.updateStatus);

        updateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validation()) {
                    UpdateUserSettings();
                }

            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }
        });

        VeryfyUserExistance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
//            Uri imageUri = data.getData();
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1,1)
//                    .start(this);
//
//        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri resultUri = result.getUri();
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
    }

    private void VeryfyUserExistance() {
        String currentUserId = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists())) {
                    String username = dataSnapshot.child("name").getValue().toString();
                    String userstatus = dataSnapshot.child("status").getValue().toString();
                    userName.setText(username);
                    userStatus.setText(userstatus);
                    Toast.makeText(SettingsActivity.this, "welcome", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, "please set & update your profile information...", Toast.LENGTH_SHORT).show();
                    userStatus.setText("hey, i am available now");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void UpdateUserSettings() {
        String username = userName.getText().toString();
        String userstatus = userStatus.getText().toString();
        HashMap<String, String> profileMap = new HashMap<>();
        profileMap.put("uid", currentUserId);
        profileMap.put("name", username);
        profileMap.put("status", userstatus);

        RootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "profile updated successfully...", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                } else {
                    String message = task.getException().getMessage().toString();
                    Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Boolean validation() {
        Boolean i = true;
        if (TextUtils.isEmpty(userName.getText().toString())) {
            Toast.makeText(SettingsActivity.this, "username cant be empty", Toast.LENGTH_LONG).show();
            i = false;
        } else if (userName.getText().toString().length() < 6) {
            Toast.makeText(SettingsActivity.this, "username is Very small", Toast.LENGTH_LONG).show();
            i = false;
        }
        if (TextUtils.isEmpty(userStatus.getText().toString())) {
            Toast.makeText(SettingsActivity.this, "userStatus cant be empty", Toast.LENGTH_LONG).show();
            i = false;
        } else if (userStatus.getText().toString().length() < 6) {
            Toast.makeText(SettingsActivity.this, "userStatus is Very small", Toast.LENGTH_LONG).show();
            i = false;
        }
        return i;
    }
}
