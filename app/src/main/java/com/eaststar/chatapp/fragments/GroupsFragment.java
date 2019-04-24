package com.eaststar.chatapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.eaststar.chatapp.R;
import com.eaststar.chatapp.activities.GroupChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    View mainView;
    ListView groupListView;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> listGroups = new ArrayList<>();
    DatabaseReference GrouptRef;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_groups, container, false);

        GrouptRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        groupListView = mainView.findViewById(R.id.groupListView);
        arrayAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,listGroups);
        groupListView.setAdapter(arrayAdapter);

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String groupName = adapterView.getItemAtPosition(i).toString();
                Intent intent = new Intent(getContext(), GroupChatActivity.class);
                intent.putExtra("groupName",groupName);
                getContext().startActivity(intent);

            }
        });
        
        getAndDisplayGroups();

        return mainView;
    }

    private void getAndDisplayGroups() {
        GrouptRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterable = dataSnapshot.getChildren().iterator();

                while (iterable.hasNext()){
                    set.add(((DataSnapshot)iterable.next()).getKey());
                }

                listGroups.clear();
                listGroups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
