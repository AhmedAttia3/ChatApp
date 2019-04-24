package com.eaststar.chatapp.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eaststar.chatapp.R;
import com.eaststar.chatapp.entities.Contacts;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

//    RecyclerView contactsRecyclerView;
    DatabaseReference ContactsRequestRef;
    String currentUserID;
    FirebaseAuth mAuth;
    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ContactsRequestRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);

        View view = inflater.inflate(R.layout.fragment_chats, container, false);
//        contactsRecyclerView = view.findViewById(R.id.contactsRecyclerView);
//        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return view ;
    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ContactsRequestRef,Contacts.class)
                        .build();


    }
}
