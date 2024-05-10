package Controller;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import DTO.UserData;

public class UserController {
    private final DatabaseReference usersRef;

    private ValueEventListener userListener;

    public UserController() {
        // FirebaseDatabase.getInstance() == 파이어베이스 연결을 위한 인스턴스를 생성합니다.
        // getReference().child("users") == 해당 파이어베이스의 "users"라는 경로에 있는 데이터를 수정할 수 있습니다.
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
    }


    // 파이어베이스에 신규 유저의 데이터를 저장하는 함수
    public void updateUserData(UserData userData) {
        // 1.고유 식별 번호인 UID를 통해서 특정 유저의 데이터에 접근합니다.
        // 2. userData 라는 매개 변수로 해당 유저의 데이터를 덧씌웁니다.
        usersRef.child(userData.getUID()).setValue(userData);
    }

    public void removeUserData(String UID) {
        // 입력받은 UID에 해당되는 유저의 정보를 삭제합니다.
        usersRef.child(UID).removeValue();
    }

}
