package com.request;

import com.DTO.FriendDTO;
import com.constant.MediaType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    private String title;
    private MediaType mediaType;
}
