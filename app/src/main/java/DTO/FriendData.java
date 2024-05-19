package DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendData {
    private String friendListId;
    private String friendId1;
    private String friendId2;
    private boolean isFavorite;
}
