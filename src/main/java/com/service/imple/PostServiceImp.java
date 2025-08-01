package com.service.imple;

import com.DTO.PostDTO;
import com.constant.*;
import com.entity.Group.Community;
import com.entity.Group.CommunityUserprofile;
import com.entity.Post;
import com.entity.VideoStorage;
import com.entity.auth.UserProfile;
import com.exception.PostException;
import com.mapper.PostMapper;
import com.repository.CommunityUserprofileRepo;
import com.repository.PostRepo;

import com.repository.VideoStorageRepo;
import com.request.SharePostRequest;
import com.service.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class PostServiceImp implements PostService {
    private PostRepo postRepo;
    private final PostMapper postMapper;
    private UpLoadImageFileService upLoadImageFileService;
    private VideoProcessingService videoProcessingService;
    private VideoStorageRepo videoStorageRepo;
    private CommunityUserprofileRepo communityUserprofileRepo;
    private final SuggestPostService suggestPostService;
    private final PostRankingService postRankingService;



    @Override
    public void createPost(SharePostRequest sharePostRequest) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        String link= "http://localhost:5173/community/"+sharePostRequest.getId();

        UserProfile userProfile= UserProfile.builder().id(myId).build();
        Post newPost= Post.builder()
                .author(userProfile)
                .title(sharePostRequest.getTitle()+" "+link)
                .media(null)
                .mediaType(MediaType.NONE)
                .type(PostType.PERSONAL)
                .build();
        postRepo.save(newPost);

    }

    @Override
    @Transactional
    public PostDTO createPost(String title, MultipartFile media, MediaType mediaType) throws IOException {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        AccountType accountType=((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccountType();
        UserProfile userProfile = UserProfile.builder().id(myId).build();
        Post newPost = Post.builder()
                .author(userProfile)
                .title(title)
                .mediaType(mediaType)
                .type(PostType.PERSONAL)
                .build();

        String mediaUrl = "";
        if (media != null && !media.isEmpty() && !mediaType.equals(MediaType.NONE)) {
            if (media.getContentType().startsWith("image/")&&mediaType.equals(MediaType.IMAGE)) {
                mediaUrl = upLoadImageFileService.uploadImage(media);
                newPost.setMedia(mediaUrl);
            }
        } else {
            newPost.setMedia(null);
        }
        if (mediaType.equals(MediaType.VIDEO)) {
            Integer totalVideo=videoStorageRepo.findMaxVideoNumber();

            VideoStorage videoStorage=VideoStorage.builder().videoNumber(totalVideo+1)
                    .author(UserProfile.builder().id(myId).build())
                    .build();

            videoStorage=videoStorageRepo.save(videoStorage);


            mediaUrl = String.format("playlist_%03d.m3u8", videoStorage.getVideoNumber());
            newPost.setMedia(mediaUrl);

        }

        Post newpost=postRepo.save(newPost);
        PostDTO postDTO = PostDTO.builder()
                .title(title)
                .media(mediaUrl)
                .mediaType(mediaType)
                .build();

        //goi y
        suggestPostService.pushSuggestPost(accountType,myId,newpost,false);

        return postDTO;
    }

    @Override
    public PostDTO createPost(String title, Integer communityId, MultipartFile media, MediaType mediaType,Boolean anonymous) throws IOException {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        UserProfile userProfile= UserProfile.builder().id(myId).build();
        Community community=Community.builder().id(communityId).build();
        Post newPost= Post.builder()
                .author(userProfile)
                .title(title)
                .mediaType(mediaType)
                .type(PostType.GROUP)
                .community(community)
                .isAnonymous(anonymous)
                .build();

        String mediaUrl="";
        if (media != null && !media.isEmpty() && !mediaType.equals(MediaType.NONE)) {
            if (media.getContentType().startsWith("image/")&&mediaType.equals(MediaType.IMAGE)) {
                mediaUrl = upLoadImageFileService.uploadImage(media);
                newPost.setMedia(mediaUrl);
            }
        } else {
            newPost.setMedia(null);
        }
        if (mediaType.equals(MediaType.VIDEO)) {
            Integer totalVideo=videoStorageRepo.findMaxVideoNumber();

            VideoStorage videoStorage=VideoStorage.builder().videoNumber(totalVideo+1)
                    .author(UserProfile.builder().id(myId).build())
                    .build();

            videoStorage=videoStorageRepo.save(videoStorage);


            mediaUrl = String.format("playlist_%03d.m3u8", videoStorage.getVideoNumber());
            newPost.setMedia(mediaUrl);

        }

        Post newpost=postRepo.save(newPost);

        PostDTO postDTO=postMapper.postToPostDTO(newpost);

        suggestPostService.pushSuggestPost(null,myId,newpost,true);
        return postDTO;
    }

    @Override
    public void deletePost(Integer postId) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Post post=postRepo.findPostById(postId);

        if(post.getCommunity()!=null){
            Integer communityId=post.getCommunity().getId();
            CommunityUserprofile communityUserprofile=communityUserprofileRepo.findByUserProfileIdAndCommunityId(myId,communityId).get();
            if(communityUserprofile.getCommunityRole().equals(CommunityRole.ADMIN)){
                postRepo.delete(post);
            }
            else{
                throw new PostException("Không có quyền!");
            }
        }
        else{
            if(Objects.equals(post.getAuthor().getId(), myId)){
                postRepo.delete(post);
            }
            else{
                throw new PostException("Không có quyền!");
            }
        }
    }

    @Override
    public void updatePost(Integer postId, String tilte) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Post post=postRepo.findPostById(postId);
        try{
            if(Objects.equals(post.getAuthor().getId(), myId)){
                post.setTitle(tilte);
                postRepo.save(post);
            }
        } catch (Exception e) {
            throw new PostException("Not valid");
        }
    }

    @Override
    public Post findById(Integer id) {
        return postRepo.findById(id).get();
    }

    @Override
    public PostDTO getPostById(Integer id) {
        Post post=postRepo.findPostById(id);
        System.out.println(post);
        return postMapper.postToPostDTO(post);
    }

    @Override
    public Page<PostDTO> getUserPost(Integer userId, Pageable pageable) {
        Page<Post> posts=postRepo.findAllWithAuthorId(userId,pageable);

        return postMapper.postPageToPostDTOPage(posts);
    }

    @Override
    public List<PostDTO> getPostWithMediaType(Integer userId, MediaType mediaType) {
        List<Post> posts=postRepo.getPostUserByMediaType(userId,mediaType);

        return postMapper.postsToPostDTOs(posts);
    }

    @Override
    public List<PostDTO> getSuggestPost(Integer userId) {
        List<Integer> suggestPosts = suggestPostService.getSuggestPosts(userId)
                .stream()
                .map(sp -> sp.getPost().getId())
                .toList();
        List<Post> posts=postRepo.findPostsByIdList(suggestPosts);


        List<Integer> list=postRankingService.randomPublicPostSuggest();

        List<Post> post2 = postRepo.findPostsByIdList(list);

        List<Post> combinedList = new ArrayList<>();
        combinedList.addAll(posts);
        combinedList.addAll(post2);

        Collections.shuffle(combinedList);
        return postMapper.postsToPostDTOs(combinedList);
    }

    @Override
    public List<PostDTO> getSuggestPostVideo(Integer userId, CategoryContent categoryContent) {
        if(categoryContent==null){
            List<Post> posts=postRepo.getSuggestPostVideo(userId);
            List<Post> suggest=postRankingService.randomPublicPostVideoSuggest();

            return postMapper.postsToPostDTOs(suggest);
        }
        else{
            List<Post> posts=postRepo.getSuggestPostVideoWithContent(userId,categoryContent);
            return postMapper.postsToPostDTOs(posts);
        }

    }


    @Override
    public void pinPost(Integer postId,Boolean pin) {
        Post comPost=postRepo.findById(postId).get();
        comPost.setIsPinned(pin);
        postRepo.save(comPost);
    }

    @Override //lay trong 1 group
    public Page<PostDTO> getCommunityPost(Integer communityId,Boolean pin,Pageable pageable) {
        Page<Post> posts=postRepo.findPostByCommunityId(communityId, pin,pageable);
        return postMapper.postPageToPostDTOPage(posts);
    }

    @Override // lay tat ca post nhom ma user tham gia
    public Page<PostDTO> findAllPostAllCommunity(Pageable pageable) {
        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Page<Post> posts=postRepo.findAllPostAllCommunity(myId,pageable);
        return postMapper.postPageToPostDTOPage(posts);
    }

    @Override
    public List<PostDTO> getMediaGroup(Integer communityId) {
        List<Post> posts=postRepo.getMediaGroup(communityId);
        return postMapper.postsToPostDTOs(posts);
    }
}
