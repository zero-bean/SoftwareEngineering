package com.gcu.anniversary.firebase;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import DTO.AnniversaryData;
import DTO.FriendData;
import DTO.GiftData;
import DTO.UserData;

public class FirebaseDatabaseManager {
    private DatabaseReference anniversaryRef;
    private DatabaseReference friendRef;
    private DatabaseReference userRef;
    private DatabaseReference giftRef;

    public FirebaseDatabaseManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        anniversaryRef = database.getReference("anniversaries");
        friendRef = database.getReference("friends");
        userRef = database.getReference("users");
        giftRef = database.getReference("gifts");
    }

    public void addAnniversary(AnniversaryData anniversary) {
        String key = anniversaryRef.push().getKey();
        anniversary.setId(key);
        anniversaryRef.child(key).setValue(anniversary);
    }

    public void updateAnniversary(AnniversaryData anniversary, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (anniversary.getId() != null) {
            anniversaryRef.child(anniversary.getId()).setValue(anniversary)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    public void deleteAnniversary(String id, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (id != null) {
            anniversaryRef.child(id).removeValue()
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    public void addFriend(FriendData friend) {
        String key = friendRef.push().getKey();
        friend.setFriendListId(key);
        friendRef.child(key).setValue(friend);
    }

    public void updateFriend(FriendData friend, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (friend.getFriendListId() != null) {
            friendRef.child(friend.getFriendListId()).setValue(friend)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    public void deleteFriend(String friendListId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (friendListId != null) {
            friendRef.child(friendListId).removeValue()
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    public void addUser(UserData user) {
        if (user.getUID() != null) {
            userRef.child(user.getUID()).setValue(user);
        }
    }

    public void updateUser(UserData user, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (user.getUID() != null) {
            userRef.child(user.getUID()).setValue(user)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    public void deleteUser(String uid, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (uid != null) {
            userRef.child(uid).removeValue()
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    public void addGift(GiftData gift) {
        String key = giftRef.push().getKey();
        gift.setGiftID(key);
        giftRef.child(key).setValue(gift);
    }

    public void updateGift(GiftData gift, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (gift.getGiftID() != null) {
            giftRef.child(gift.getGiftID()).setValue(gift)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    public void deleteGift(String giftID, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (giftID != null) {
            giftRef.child(giftID).removeValue()
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
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

    public DatabaseReference getGiftsReference() {
        return giftRef;
    }
}
