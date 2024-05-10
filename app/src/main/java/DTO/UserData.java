package DTO;

import java.io.Serializable;

public class UserData implements Serializable {
    private String UID;
    private String nickName;
    private String imageURL;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserData(String UID, String nickName, String imageURL) {
        this.UID = UID;
        this.nickName = nickName;
        this.imageURL = imageURL;
    }

    public void setUID(String UID) { this.UID = UID;}
    public void setNickName(String nickName) {this.nickName = nickName;}
    public void setImageURL(String imageURL) {this.imageURL = imageURL;}

    public String getUID() { return UID; }
    public String getNickName() { return nickName; }
    public String getImageURL() { return imageURL; }

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
}
