package com.controller.postactive;




import com.DTO.PostDTO;
import com.constant.CategoryContent;
import com.constant.MediaType;

import com.request.SharePostRequest;
import com.response.ApiResponse;
import com.service.imple.CustomUserDetails;
import com.service.PostService;
import com.service.SuggestPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final SuggestPostService suggestPostService;

    @PostMapping("/create")
    public ResponseEntity<PostDTO> createNotification(@RequestParam("title") String title,
                                                      @RequestParam("mediaType") MediaType mediaType,
                                                      @RequestParam(value = "media",required = false) MultipartFile media) throws IOException {

        PostDTO postDTO= postService.createPost(title,media,mediaType);
        return new ResponseEntity<PostDTO>(postDTO, HttpStatus.OK);
    }

    @PostMapping("/community/create")
    public ResponseEntity<PostDTO> create(@RequestParam("title") String title,
                                          @RequestParam("communityId") Integer communityId,
                                          @RequestParam("anonymous") Boolean anonymous,
                                          @RequestParam("mediaType") MediaType mediaType,
                                          @RequestParam(value = "media",required = false) MultipartFile media
    ) throws IOException {

        PostDTO postDTO= postService.createPost(title,communityId,media,mediaType,anonymous);
        return new ResponseEntity<PostDTO>(postDTO, HttpStatus.OK);
    }

    @PostMapping("/share")
    public ResponseEntity<ApiResponse> createNotification(@RequestBody SharePostRequest sharePostRequest) {

        postService.createPost(sharePostRequest);
        ApiResponse apiResponse=new ApiResponse();
        apiResponse.setMessage("Success");
        apiResponse.setStatus(true);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Integer id) {
        PostDTO postDTO=postService.getPostById(id);
        return new ResponseEntity<PostDTO>(postDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updatePostHandle(@PathVariable Integer id,@RequestBody String title) {
        postService.updatePost(id,title.substring(1,title.length()-1));
        ApiResponse apiResponse=new ApiResponse();
        apiResponse.setMessage("Success");
        apiResponse.setStatus(true);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}/pin")
    public ResponseEntity<ApiResponse> pinPostHandle(@PathVariable Integer id,@RequestBody Boolean pin) {
        postService.pinPost(id,pin);
        ApiResponse apiResponse=new ApiResponse();
        apiResponse.setMessage("Success");
        apiResponse.setStatus(true);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletePostHandle(@PathVariable Integer id) {
        postService.deletePost(id);

        ApiResponse apiResponse=new ApiResponse();
        apiResponse.setMessage("Success");
        apiResponse.setStatus(true);
        return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{userId}/all")
    public ResponseEntity<Page<PostDTO>> getuserPost(
             @PathVariable Integer userId,
             @RequestParam(value = "page", defaultValue = "0") int page,
             @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> postDTO=postService.getUserPost(userId,pageable);
        return new ResponseEntity<Page<PostDTO>>(postDTO, HttpStatus.OK);
    }

    @GetMapping("/community/{communityId}/all")
    public ResponseEntity<Page<PostDTO>> getNotifications(
            @PathVariable Integer communityId,@RequestParam Boolean pin,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> postDTO=postService.getCommunityPost(communityId,pin,pageable);
        return new ResponseEntity<Page<PostDTO>>(postDTO, HttpStatus.OK);
    }

    @GetMapping("/community/all")
    public ResponseEntity<Page<PostDTO>> getAll(
         @RequestParam(value = "page", defaultValue = "0") int page,
         @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> postDTO=postService.findAllPostAllCommunity(pageable);
        return new ResponseEntity<Page<PostDTO>>(postDTO, HttpStatus.OK);
    }

    @GetMapping("/{userId}/image")
    public ResponseEntity<List<PostDTO>> getImageHandle(@PathVariable Integer userId) {
        List<PostDTO> medias=postService.getPostWithMediaType(userId,MediaType.IMAGE);

        return new ResponseEntity<List<PostDTO>>(medias, HttpStatus.OK);
    }

    @GetMapping("/{userId}/video")
    public ResponseEntity<List<PostDTO>> getPostVideo(@PathVariable Integer userId) {
        List<PostDTO> post=postService.getPostWithMediaType(userId,MediaType.VIDEO);
        return new ResponseEntity<List<PostDTO>>(post, HttpStatus.OK);
    }

    @GetMapping("/suggest/all")
    public ResponseEntity<List<PostDTO>> getsuggest() {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        List<PostDTO> postDTO=postService.getSuggestPost(myId);
        return new ResponseEntity<List<PostDTO>>(postDTO, HttpStatus.OK);
    }

    @GetMapping("/suggest/all/video")
    public ResponseEntity<List<PostDTO>> getsuggestvideo(
            @RequestParam(name = "category", required = false) String category
    ) {
        try {
            CategoryContent categoryContent = null;
            if (category != null) {
                categoryContent = CategoryContent.valueOf(category.toUpperCase());
            }

            Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            List<PostDTO> postDTO = postService.getSuggestPostVideo(myId, categoryContent);
            return new ResponseEntity<>(postDTO, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/community/{communityId}/media")
    public ResponseEntity<List<PostDTO>> getimagegroup(@PathVariable Integer communityId) {
        List<PostDTO> posts=postService.getMediaGroup(communityId);
        return new ResponseEntity<List<PostDTO>>(posts, HttpStatus.OK);
    }

    @PostMapping("/decreaseRenderTime")
    public ResponseEntity<ApiResponse> decreaseRenderTime(@RequestBody List<Integer> postIds) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        suggestPostService.decreaseRenderTime(myId,postIds);
        ApiResponse res = new ApiResponse("Successfully...", true);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


}

