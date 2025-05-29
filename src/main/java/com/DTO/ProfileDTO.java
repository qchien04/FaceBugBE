package com.DTO;
import com.constant.AccountType;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private Integer id;
    private String name;
    private String avt;
    private AccountType accountType;

}

