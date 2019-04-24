package com.eaststar.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eaststar.chatapp.R;
import com.eaststar.chatapp.entities.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

public class FindFriendsActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView friendsRecyclerView;
    DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        toolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        friendsRecyclerView = findViewById(R.id.friendsRecyclerView);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(FindFriendsActivity.this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UsersRef,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewHolder holder,final int i, @NonNull Contacts contacts) {
                        holder.userName.setText(contacts.getName());
                        holder.userStatus.setText(contacts.getStatus());
                        if(contacts.getImage()!=null) {

                            Glide.with(FindFriendsActivity.this).load(contacts.getImage()).placeholder(R.drawable.profile_image).into(holder.userImage);
                        }else {
                            Glide.with(FindFriendsActivity.this).load(R.drawable.profile_image).into(holder.userImage);
                        }

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String userID = getRef(i).getKey();
                                Intent intent = new Intent(FindFriendsActivity.this,ProfileActivity.class);
                                intent.putExtra("userID",userID);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_find_frinds_display_users,parent,false);
                        FindFriendViewHolder findFriendViewHolder = new FindFriendViewHolder(view);
                        return findFriendViewHolder;
                    }
                };

        friendsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{
        CircularImageView userImage;
        ImageView onlineImage;
        TextView userName, userStatus ;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            onlineImage = itemView.findViewById(R.id.onlineImage);
            userName = itemView.findViewById(R.id.userName);
            userStatus = itemView.findViewById(R.id.userStatus);

        }
    }
}
