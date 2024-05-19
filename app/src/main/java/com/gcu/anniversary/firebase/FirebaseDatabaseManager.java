package com.gcu.anniversary.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import DTO.AnniversaryData;
import DTO.FriendData;
import DTO.UserData;

public class FirebaseDatabaseManager {
    private DatabaseReference anniversaryRef;
    private DatabaseReference friendRef;
    private DatabaseReference userRef;

    public FirebaseDatabaseManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        anniversaryRef = database.getReference("anniversaries");
        friendRef = database.getReference("friends");
        userRef = database.getReference("users");
    }

    public void addAnniversary(AnniversaryData anniversary) {
        String key = anniversaryRef.push().getKey();
        anniversary.setId(key);
        anniversaryRef.child(key).setValue(anniversary);
    }

    public void updateAnniversary(AnniversaryData anniversary) {
        if (anniversary.getId() != null) {
            anniversaryRef.child(anniversary.getId()).setValue(anniversary);
        }
    }

    public void deleteAnniversary(String id) {
        if (id != null) {
            anniversaryRef.child(id).removeValue();
        }
    }

    public void addFriend(FriendData friend) {
        String key = friendRef.push().getKey();
        friend.setFriendListId(key);
        friendRef.child(key).setValue(friend);
    }

    public void updateFriend(FriendData friend) {
        if (friend.getFriendListId() != null) {
            friendRef.child(friend.getFriendListId()).setValue(friend);
        }
    }

    public void deleteFriend(String friendListId) {
        if (friendListId != null) {
            friendRef.child(friendListId).removeValue();
        }
    }

    public void addUser(UserData user) {
        if (user.getUID() != null) {
            userRef.child(user.getUID()).setValue(user);
        }
    }

    public void updateUser(UserData user) {
        if (user.getUID() != null) {
            userRef.child(user.getUID()).setValue(user);
        }
    }

    public void deleteUser(String uid) {
        if (uid != null) {
            userRef.child(uid).removeValue();
        }
    }

    public DatabaseReference getAnniversariesReference() {
        return anniversaryRef;
    }

    public DatabaseReference getFriendsReference() {
        return friendRef;
    }

    public DatabaseReference getUsersReference() {
        return userRef;
    }
}
