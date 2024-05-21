package com.gcu.anniversary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.gcu.anniversary.firebase.FirebaseDatabaseManager;

import DTO.AnniversaryData;
import DTO.FriendData;

public class AlertFragment extends Fragment {
    private FirebaseDatabaseManager databaseManager;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> alertList;
    private List<FriendData> friendsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alert, container, false);

        databaseManager = new FirebaseDatabaseManager();
        listView = view.findViewById(R.id.alertListView);
        alertList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, alertList);
        listView.setAdapter(adapter);
        friendsList = new ArrayList<>();

        fetchFriends();

        return view;
    }

    private void fetchFriends() {
        databaseManager.getFriendsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    FriendData friend = snapshot.getValue(FriendData.class);
                    if (friend != null) {
                        friendsList.add(friend);
                    }
                }
                fetchAlerts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AlertFragment", "Failed to fetch friends: " + databaseError.getMessage());
            }
        });
    }

    private void fetchAlerts() {
        databaseManager.getAnniversariesReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alertList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AnniversaryData anniversary = snapshot.getValue(AnniversaryData.class);
                    if (anniversary != null && isFriendAnniversary(anniversary.getUserId())) {
                        alertList.add(anniversary.getDate() + ": " + anniversary.getComment());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AlertFragment", "Failed to fetch anniversaries: " + databaseError.getMessage());
            }
        });
    }

    private boolean isFriendAnniversary(String userId) {
        for (FriendData friend : friendsList) {
            if (friend.getFriendId1().equals(userId) || friend.getFriendId2().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
