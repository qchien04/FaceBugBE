package com.service;

import com.DTO.PostDTO;
import com.constant.CategoryContent;
import com.constant.MediaType;
import com.entity.Post;
import com.request.SharePostRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    void createPost(SharePostRequest sharePostRequest);
    PostDTO createPost(String title, MultipartFile media, MediaType mediaType) throws IOException;
    void deletePost(Integer postId);
    void updatePost(Integer postId,String tilte);
    Post findById(Integer id);
    PostDTO getPostById(Integer id);
    Page<PostDTO> getUserPost(Integer userId, Pageable pageable);
    List<PostDTO> getPostWithMediaType(Integer userId,MediaType mediaType);
    List<PostDTO> getSuggestPost(Integer userId);
    List<PostDTO> getSuggestPostVideo(Integer userId, CategoryContent categoryContent);
    void pinPost(Integer postId,Boolean pin);


    //Community
    Page<PostDTO> getCommunityPost(Integer communityId,Boolean pin,Pageable pageable);
    Page<PostDTO> findAllPostAllCommunity(Pageable pageable);
    PostDTO createPost(String title,Integer communityId, MultipartFile media, MediaType mediaType,Boolean anonymous) throws IOException;
    List<PostDTO> getMediaGroup(Integer communityId);
}

