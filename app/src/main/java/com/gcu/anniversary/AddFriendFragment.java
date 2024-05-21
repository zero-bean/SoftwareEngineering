package com.gcu.anniversary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gcu.anniversary.firebase.FirebaseDatabaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import DTO.FriendData;
import DTO.UserData;

public class AddFriendFragment extends DialogFragment {
    private FirebaseDatabaseManager databaseManager;
    private EditText searchEditText;
    private Button searchButton;
    private ListView searchListView;
    private List<String> searchResults;
    private ArrayAdapter<String> searchAdapter;
    private TextView noResultsTextView;
    private FriendAddedListener friendAddedListener;

    public AddFriendFragment(FriendAddedListener friendAddedListener) {
        this.friendAddedListener = friendAddedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        databaseManager = new FirebaseDatabaseManager();
        searchEditText = view.findViewById(R.id.searchEditText);
        searchButton = view.findViewById(R.id.searchButton);
        searchListView = view.findViewById(R.id.searchListView);
        noResultsTextView = view.findViewById(R.id.noResultsTextView);

        searchResults = new ArrayList<>();
        searchAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, searchResults);
        searchListView.setAdapter(searchAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFriends();
            }
        });

        searchListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedUser = searchResults.get(position);
            addFriend(selectedUser);
        });

        return view;
    }

    private void searchFriends() {
        String userName = searchEditText.getText().toString().trim();
        if (userName.isEmpty()) {
            searchEditText.setError("Enter a username to search");
            return;
        }

        databaseManager.getUsersReference().orderByChild("userName").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        searchResults.clear();
                        if (dataSnapshot.exists()) {
                            noResultsTextView.setVisibility(View.GONE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                UserData user = snapshot.getValue(UserData.class);
                                if (user != null) {
                                    searchResults.add(user.getUserName() + " (" + user.getUID() + ")");
                                }
                            }
                        } else {
                            noResultsTextView.setVisibility(View.VISIBLE);
                        }
                        searchAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    private void addFriend(String selectedUser) {
        String[] parts = selectedUser.split(" ");
        String friendUserName = parts[0];
        String friendUID = parts[1].substring(1, parts[1].length() - 1); // Remove parentheses

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId == null) {
            return;
        }

        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("friends");
        String friendListId = friendsRef.push().getKey();
        if (friendListId == null) {
            return;
        }

        FriendData friendData = new FriendData(friendListId, currentUserId, friendUID, false);
        friendsRef.child(friendListId).setValue(friendData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Friend added successfully", Toast.LENGTH_SHORT).show();
                        dismiss();
                        if (friendAddedListener != null) {
                            friendAddedListener.onFriendAdded();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to add friend", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface FriendAddedListener {
        void onFriendAdded();
    }
}
