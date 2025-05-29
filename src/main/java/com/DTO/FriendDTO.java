package com.DTO;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FriendDTO {
    private Integer friendId;
    private String friendName;
    private String friendAvt;

    @Override
    public String toString() {
        return "FriendDTO{" +
                "friendId=" + friendId +
                ", friendName='" + friendName + '\'' +
                ", friendAvt='" + friendAvt + '\'' +
                '}';
    }
}