package com.gcu.anniversary;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.gcu.anniversary.firebase.FirebaseDatabaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import DTO.AnniversaryData;
import DTO.FriendData;
import DTO.UserData;

public class FriendFragment extends Fragment {
    private FirebaseDatabaseManager databaseManager; // Firebase 데이터베이스를 관리하는 객체
    private LinearLayout favoriteFriendContainer; // 즐겨찾기 친구를 표시하는 레이아웃
    private ListView friendListView; // 친구 목록을 표시하는 리스트뷰
    private FriendAdapter friendAdapter; // 친구 목록 어댑터
    private List<FriendData> favoriteFriendDataList; // 즐겨찾기 친구 데이터 리스트
    private List<FriendData> friendDataList; // 친구 데이터 리스트
    private List<AnniversaryData> anniversaryDataList; // 기념일 데이터 리스트
    private Map<String, List<AnniversaryData>> friendAnniversariesMap; // 친구별 기념일 데이터를 매핑하는 맵
    private List<UserData> userDataList; // 사용자 데이터 리스트
    private TextView currentDateTextView; // 현재 날짜를 표시하는 텍스트뷰

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        // Firebase 데이터베이스 매니저 초기화
        databaseManager = new FirebaseDatabaseManager();
        favoriteFriendContainer = view.findViewById(R.id.favoriteFriendContainer);
        friendListView = view.findViewById(R.id.friendListView);
        currentDateTextView = view.findViewById(R.id.currentDateTextView);

        // 각 리스트 초기화
        favoriteFriendDataList = new ArrayList<>();
        friendDataList = new ArrayList<>();
        anniversaryDataList = new ArrayList<>();
        friendAnniversariesMap = new HashMap<>();
        userDataList = new ArrayList<>();
        friendAdapter = new FriendAdapter(getContext(), friendDataList, userDataList, friendAnniversariesMap);
        friendListView.setAdapter(friendAdapter);

        // 현재 날짜 설정
        setCurrentDate();
        // 친구 데이터 가져오기
        fetchFriends();

        // 툴바에 메뉴 설정
        Toolbar toolbar = view.findViewById(R.id.toolbar_friend);
        toolbar.inflateMenu(R.menu.friend_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.add_friend_button) {
                showAddFriendDialog(); // 친구 추가 다이얼로그 표시
                return true;
            }
            return false;
        });

        return view;
    }

    // 현재 날짜를 설정하는 메서드
    private void setCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        currentDateTextView.setText("현재 날짜: " + currentDate);
    }

    // 친구 데이터를 가져오는 메서드
    private void fetchFriends() {
        String userId = getCurrentUserId(); // 현재 사용자 ID 가져오기
        if (userId == null) {
            Log.e("FriendFragment", "User ID is null");
            return;
        }

        // Firebase에서 친구 데이터 가져오기
        databaseManager.getFriendsReference().orderByChild("friendId1").equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        favoriteFriendDataList.clear();
                        friendDataList.clear();
                        anniversaryDataList.clear();
                        userDataList.clear();
                        friendAnniversariesMap.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FriendData friend = snapshot.getValue(FriendData.class);
                            if (friend != null) {
                                if (friend.isFavorite()) {
                                    favoriteFriendDataList.add(friend);
                                } else {
                                    friendDataList.add(friend);
                                }
                                fetchUserDetails(friend.getFriendId2()); // 친구의 사용자 정보 가져오기
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FriendFragment", "Failed to fetch friends: " + databaseError.getMessage());
                    }
                });
    }

    // 사용자 세부 정보를 가져오는 메서드
    private void fetchUserDetails(String userId) {
        databaseManager.getUsersReference().child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserData user = dataSnapshot.getValue(UserData.class);
                        if (user != null) {
                            userDataList.add(user);
                            fetchAnniversaries(user.getUID()); // 사용자의 기념일 정보 가져오기
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FriendFragment", "Failed to fetch user details: " + databaseError.getMessage());
                    }
                });
    }

    // 친구의 기념일 데이터를 가져오는 메서드
    private void fetchAnniversaries(String userId) {
        databaseManager.getAnniversariesReference().orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<AnniversaryData> anniversaries = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            AnniversaryData anniversary = snapshot.getValue(AnniversaryData.class);
                            if (anniversary != null && "Birthday".equals(anniversary.getComment())) {
                                anniversaries.add(anniversary); // 생일 기념일만 추가
                            }
                        }
                        friendAnniversariesMap.put(userId, anniversaries);
                        sortAndDisplayFriends(); // 친구 데이터를 정렬하고 표시
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FriendFragment", "Failed to fetch anniversaries: " + databaseError.getMessage());
                    }
                });
    }

    // 친구 데이터를 정렬하고 표시하는 메서드
    private void sortAndDisplayFriends() {
        Comparator<FriendData> comparator = new Comparator<FriendData>() {
            @Override
            public int compare(FriendData o1, FriendData o2) {
                Date date1 = getClosestAnniversaryDate(o1.getFriendId2());
                Date date2 = getClosestAnniversaryDate(o2.getFriendId2());
                boolean isDate1Past = isDatePast(date1);
                boolean isDate2Past = isDatePast(date2);

                if (isDate1Past && !isDate2Past) {
                    return 1;
                } else if (!isDate1Past && isDate2Past) {
                    return -1;
                } else {
                    if (date1 != null && date2 != null) {
                        return date1.compareTo(date2);
                    } else if (date1 != null) {
                        return -1;
                    } else if (date2 != null) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        };

        Collections.sort(favoriteFriendDataList, comparator); // 즐겨찾기 친구 목록 정렬
        Collections.sort(friendDataList, comparator); // 일반 친구 목록 정렬

        favoriteFriendContainer.removeAllViews(); // 기존의 즐겨찾기 친구 뷰 제거
        for (FriendData friend : favoriteFriendDataList) {
            View friendView = createFriendView(friend); // 새로운 친구 뷰 생성
            favoriteFriendContainer.addView(friendView); // 즐겨찾기 컨테이너에 추가
        }

        friendAdapter.notifyDataSetChanged(); // 어댑터 갱신
    }

    // 날짜가 이미 지났는지 확인하는 메서드
    private boolean isDatePast(Date date) {
        if (date == null) {
            return false;
        }
        return date.before(new Date());
    }

    // 가장 가까운 기념일 날짜를 가져오는 메서드
    private Date getClosestAnniversaryDate(String friendId) {
        List<AnniversaryData> anniversaries = friendAnniversariesMap.get(friendId);
        if (anniversaries == null || anniversaries.isEmpty()) {
            return null;
        }
        Date closestDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);

        for (AnniversaryData anniversary : anniversaries) {
            try {
                Date date = sdf.parse(anniversary.getDate());
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.YEAR, currentYear);
                    Date thisYearBirthday = calendar.getTime();
                    if (isDatePast(thisYearBirthday)) {
                        calendar.set(Calendar.YEAR, currentYear + 1);
                        date = calendar.getTime();
                    } else {
                        date = thisYearBirthday;
                    }
                }
                if (closestDate == null || (date != null && date.before(closestDate))) {
                    closestDate = date;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return closestDate;
    }

    // 친구 뷰를 생성하는 메서드
    private View createFriendView(FriendData friend) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View convertView = inflater.inflate(R.layout.friend_list_item, favoriteFriendContainer, false);

        UserData user = getUserData(friend.getFriendId2());
        String closestAnniversary = getClosestAnniversary(friend.getFriendId2());
        String dDay = calculateDDay(closestAnniversary);

        TextView friendInfo = convertView.findViewById(R.id.friendInfo);
        ImageButton giftButton = convertView.findViewById(R.id.giftButton);
        ImageButton favoriteButton = convertView.findViewById(R.id.favoriteButton);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

        if (user != null) {
            String displayText = user.getUserName() + (friend.isFavorite() ? " ★" : "") +
                    (closestAnniversary != null ? " - " + closestAnniversary : "") +
                    (dDay != null ? " D-" + dDay : "");
            friendInfo.setText(displayText);
        } else {
            friendInfo.setText("Unknown user");
        }

        giftButton.setOnClickListener(v -> showGiftList(friend.getFriendListId())); // 선물 목록 표시
        favoriteButton.setOnClickListener(v -> toggleFavorite(friend.getFriendId2())); // 즐겨찾기 토글
        deleteButton.setOnClickListener(v -> deleteFriend(friend.getFriendId2())); // 친구 삭제

        return convertView;
    }

    // 선물 목록을 표시하는 메서드
    private void showGiftList(String friendListId) {
        GiftListFragment giftListFragment = new GiftListFragment(friendListId);
        giftListFragment.show(getParentFragmentManager(), "GiftListFragment");
    }

    // D-Day를 계산하는 메서드
    private String calculateDDay(String date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date anniversaryDate = sdf.parse(date);
            Date currentDate = new Date();
            long diff = anniversaryDate.getTime() - currentDate.getTime();
            long days = diff / (24 * 60 * 60 * 1000);
            return String.valueOf(days);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 사용자 데이터를 가져오는 메서드
    @Nullable
    private UserData getUserData(String userId) {
        for (UserData user : userDataList) {
            if (user.getUID().equals(userId)) {
                return user;
            }
        }
        return null;
    }

    // 가장 가까운 기념일을 가져오는 메서드
    @Nullable
    private String getClosestAnniversary(String friendId) {
        List<AnniversaryData> anniversaries = friendAnniversariesMap.get(friendId);
        if (anniversaries == null || anniversaries.isEmpty()) {
            return null;
        }
        Date closestDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar currentCalendar = Calendar.getInstance();
        int currentYear = currentCalendar.get(Calendar.YEAR);

        for (AnniversaryData anniversary : anniversaries) {
            try {
                Date date = sdf.parse(anniversary.getDate());
                if (date != null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.set(Calendar.YEAR, currentYear);
                    Date thisYearBirthday = calendar.getTime();
                    if (isDatePast(thisYearBirthday)) {
                        calendar.set(Calendar.YEAR, currentYear + 1);
                        date = calendar.getTime();
                    } else {
                        date = thisYearBirthday;
                    }
                }
                if (closestDate == null || (date != null && date.before(closestDate))) {
                    closestDate = date;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return closestDate != null ? sdf.format(closestDate) : null;
    }

    // 친구 추가 다이얼로그를 표시하는 메서드
    private void showAddFriendDialog() {
        AddFriendFragment addFriendFragment = new AddFriendFragment(() -> fetchFriends());
        addFriendFragment.show(getParentFragmentManager(), "AddFriendFragment");
    }

    // 현재 사용자 ID를 가져오는 메서드
    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }

    // 즐겨찾기 상태를 토글하는 메서드
    private void toggleFavorite(String friendId) {
        for (FriendData friend : favoriteFriendDataList) {
            if (friend.getFriendId2().equals(friendId)) {
                friend.setFavorite(!friend.isFavorite());
                databaseManager.updateFriend(friend, aVoid -> {
                    sortAndDisplayFriends();
                }, e -> {
                    Toast.makeText(getContext(), "Failed to update friend", Toast.LENGTH_SHORT).show();
                });
                return;
            }
        }

        for (FriendData friend : friendDataList) {
            if (friend.getFriendId2().equals(friendId)) {
                friend.setFavorite(!friend.isFavorite());
                databaseManager.updateFriend(friend, aVoid -> {
                    sortAndDisplayFriends();
                }, e -> {
                    Toast.makeText(getContext(), "Failed to update friend", Toast.LENGTH_SHORT).show();
                });
                return;
            }
        }
    }

    // 친구를 삭제하는 메서드
    private void deleteFriend(String friendId) {
        for (FriendData friend : favoriteFriendDataList) {
            if (friend.getFriendId2().equals(friendId)) {
                databaseManager.deleteFriend(friend.getFriendListId(), aVoid -> {
                    favoriteFriendDataList.remove(friend);
                    sortAndDisplayFriends();
                }, e -> {
                    Toast.makeText(getContext(), "Failed to delete friend", Toast.LENGTH_SHORT).show();
                });
                return;
            }
        }

        for (FriendData friend : friendDataList) {
            if (friend.getFriendId2().equals(friendId)) {
                databaseManager.deleteFriend(friend.getFriendListId(), aVoid -> {
                    friendDataList.remove(friend);
                    sortAndDisplayFriends();
                }, e -> {
                    Toast.makeText(getContext(), "Failed to delete friend", Toast.LENGTH_SHORT).show();
                });
                return;
            }
        }
    }

    // 친구 목록 어댑터 클래스
    private class FriendAdapter extends ArrayAdapter<FriendData> {
        private List<FriendData> friends; // 친구 데이터 리스트
        private List<UserData> users; // 사용자 데이터 리스트
        private Map<String, List<AnniversaryData>> friendAnniversariesMap; // 친구별 기념일 데이터를 매핑하는 맵

        public FriendAdapter(Context context, List<FriendData> friends, List<UserData> users, Map<String, List<AnniversaryData>> friendAnniversariesMap) {
            super(context, R.layout.friend_list_item, friends);
            this.friends = friends;
            this.users = users;
            this.friendAnniversariesMap = friendAnniversariesMap;
        }

        @NonNull
        @Override
        public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_list_item, parent, false);
            }

            FriendData friend = friends.get(position);
            UserData user = getUserData(friend.getFriendId2());
            String closestAnniversary = getClosestAnniversary(friend.getFriendId2());
            String dDay = calculateDDay(closestAnniversary);

            TextView friendInfo = convertView.findViewById(R.id.friendInfo);
            ImageButton giftButton = convertView.findViewById(R.id.giftButton);
            ImageButton favoriteButton = convertView.findViewById(R.id.favoriteButton);
            ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

            if (user != null) {
                String displayText = user.getUserName() + (friend.isFavorite() ? " ★" : "") +
                        (closestAnniversary != null ? " - " + closestAnniversary : "") +
                        (dDay != null ? " D-" + dDay : "");
                friendInfo.setText(displayText);
            } else {
                friendInfo.setText("Unknown user");
            }

            giftButton.setOnClickListener(v -> showGiftList(friend.getFriendListId())); // 선물 목록 표시
            favoriteButton.setOnClickListener(v -> toggleFavorite(friend.getFriendId2())); // 즐겨찾기 토글
            deleteButton.setOnClickListener(v -> deleteFriend(friend.getFriendId2())); // 친구 삭제

            return convertView;
        }

        @Nullable
        private UserData getUserData(String userId) {
            for (UserData user : users) {
                if (user.getUID().equals(userId)) {
                    return user;
                }
            }
            return null;
        }

        @Nullable
        private String getClosestAnniversary(String friendId) {
            List<AnniversaryData> anniversaries = friendAnniversariesMap.get(friendId);
            if (anniversaries == null || anniversaries.isEmpty()) {
                return null;
            }
            Date closestDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar currentCalendar = Calendar.getInstance();
            int currentYear = currentCalendar.get(Calendar.YEAR);

            for (AnniversaryData anniversary : anniversaries) {
                try {
                    Date date = sdf.parse(anniversary.getDate());
                    if (date != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.set(Calendar.YEAR, currentYear);
                        Date thisYearBirthday = calendar.getTime();
                        if (isDatePast(thisYearBirthday)) {
                            calendar.set(Calendar.YEAR, currentYear + 1);
                            date = calendar.getTime();
                        } else {
                            date = thisYearBirthday;
                        }
                    }
                    if (closestDate == null || (date != null && date.before(closestDate))) {
                        closestDate = date;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return closestDate != null ? sdf.format(closestDate) : null;
        }

        private String calculateDDay(String date) {
            if (date == null) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date anniversaryDate = sdf.parse(date);
                Date currentDate = new Date();
                long diff = anniversaryDate.getTime() - currentDate.getTime();
                long days = diff / (24 * 60 * 60 * 1000);
                return String.valueOf(days);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
