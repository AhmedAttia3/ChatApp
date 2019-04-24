package com.eaststar.chatapp.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eaststar.chatapp.R;
import com.eaststar.chatapp.activities.FindFriendsActivity;
import com.eaststar.chatapp.activities.ProfileActivity;
import com.eaststar.chatapp.entities.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    RecyclerView contactsRecyclerView;
    DatabaseReference ContactsRequestRef;
    String currentUserID;
    FirebaseAuth mAuth;
    DatabaseReference UsersRef;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ContactsRequestRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerView);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ContactsRequestRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FindFriendViewHolder holder, final int i, @NonNull final Contacts contacts) {

                        String userId = getRef(i).getKey().toString();
                        UsersRef.child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String image = dataSnapshot.child("image").getValue().toString();
                                    String name = dataSnapshot.child("name").getValue().toString();
                                    String status = dataSnapshot.child("status").getValue().toString();

                                    holder.userName.setText(name);
                                    holder.userStatus.setText(status);
                                    if (image != null) {

                                        Glide.with(getActivity()).load(image).placeholder(R.drawable.profile_image).into(holder.userImage);
                                    } else {
                                        Glide.with(getActivity()).load(R.drawable.profile_image).into(holder.userImage);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String userID = getRef(i).getKey();
                                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_find_frinds_display_users, parent, false);
                        FindFriendViewHolder findFriendViewHolder = new FindFriendViewHolder(view);
                        return findFriendViewHolder;
                    }
                };

        contactsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class FindFriendViewHolder extends RecyclerView.ViewHolder {
        CircularImageView userImage;
        ImageView onlineImage;
        TextView userName, userStatus;

        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            onlineImage = itemView.findViewById(R.id.onlineImage);
            userName = itemView.findViewById(R.id.userName);
            userStatus = itemView.findViewById(R.id.userStatus);

        }
    }
}
