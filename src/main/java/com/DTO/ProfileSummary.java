package com.DTO;
import com.constant.AccountType;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileSummary {
    private Integer id;
    private String name;
    private String avt;
    private AccountType accountType;

    public ProfileSummary(Integer id, String name, String avt) {
        this.id = id;
        this.name = name;
        this.avt = avt;
    }
}

