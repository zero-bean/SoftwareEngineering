package DTO;

import java.io.Serializable;

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
public class AnniversaryData implements Serializable {
    private String id;
    private String userId;
    private String date;
    private String comment;

    // id 없는 생성자 (추가할 때 사용)
    public AnniversaryData(String userId, String date, String comment) {
        this.userId = userId;
        this.date = date;
        this.comment = comment;
    }
}
