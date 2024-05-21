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

    // FirebaseDatabaseManager 생성자 - FirebaseDatabase 인스턴스를 가져와 참조 초기화
    public FirebaseDatabaseManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        anniversaryRef = database.getReference("anniversaries"); // 기념일 데이터베이스 참조
        friendRef = database.getReference("friends"); // 친구 데이터베이스 참조
        userRef = database.getReference("users"); // 사용자 데이터베이스 참조
        giftRef = database.getReference("gifts"); // 선물 데이터베이스 참조
    }

    // 기념일 데이터 추가
    public void addAnniversary(AnniversaryData anniversary) {
        String key = anniversaryRef.push().getKey();
        anniversary.setId(key);
        anniversaryRef.child(key).setValue(anniversary);
    }

    // 기념일 데이터 업데이트
    public void updateAnniversary(AnniversaryData anniversary, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (anniversary.getId() != null) {
            anniversaryRef.child(anniversary.getId()).setValue(anniversary)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    // 기념일 데이터 삭제
    public void deleteAnniversary(String id, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (id != null) {
            anniversaryRef.child(id).removeValue()
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    // 친구 데이터 추가
    public void addFriend(FriendData friend) {
        String key = friendRef.push().getKey();
        friend.setFriendListId(key);
        friendRef.child(key).setValue(friend);
    }

    // 친구 데이터 업데이트
    public void updateFriend(FriendData friend, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (friend.getFriendListId() != null) {
            friendRef.child(friend.getFriendListId()).setValue(friend)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    // 친구 데이터 삭제
    public void deleteFriend(String friendListId, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (friendListId != null) {
            friendRef.child(friendListId).removeValue()
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    // 사용자 데이터 추가
    public void addUser(UserData user) {
        if (user.getUID() != null) {
            userRef.child(user.getUID()).setValue(user);
        }
    }

    // 사용자 데이터 업데이트
    public void updateUser(UserData user, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (user.getUID() != null) {
            userRef.child(user.getUID()).setValue(user)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    // 사용자 데이터 삭제
    public void deleteUser(String uid, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (uid != null) {
            userRef.child(uid).removeValue()
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    // 선물 데이터 추가
    public void addGift(GiftData gift) {
        String key = giftRef.push().getKey();
        gift.setGiftID(key);
        giftRef.child(key).setValue(gift);
    }

    // 선물 데이터 업데이트
    public void updateGift(GiftData gift, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (gift.getGiftID() != null) {
            giftRef.child(gift.getGiftID()).setValue(gift)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    // 선물 데이터 삭제
    public void deleteGift(String giftID, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        if (giftID != null) {
            giftRef.child(giftID).removeValue()
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);
        }
    }

    // 기념일 데이터베이스 참조 반환
    public DatabaseReference getAnniversariesReference() {
        return anniversaryRef;
    }

    // 친구 데이터베이스 참조 반환
    public DatabaseReference getFriendsReference() {
        return friendRef;
    }

    // 사용자 데이터베이스 참조 반환
    public DatabaseReference getUsersReference() {
        return userRef;
    }

    // 선물 데이터베이스 참조 반환
    public DatabaseReference getGiftsReference() {
        return giftRef;
    }
}
