package DTO;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // 파이어베이스는 반드시 빈 생성자를 필요로 합니다.
public class UserData {
    @NonNull
    private String UID;
    @NonNull
    private String nickName;
    @NonNull
    private String imageURL;
    private String userName;
    private List<FriendData> friendList;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserData userData = (UserData) obj;
        return UID.equals(userData.UID) &&
                nickName.equals(userData.nickName) &&
                imageURL.equals(userData.imageURL);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UID, nickName, imageURL);
    }
}
