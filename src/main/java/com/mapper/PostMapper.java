package com.mapper;
import com.DTO.PostDTO;
import com.entity.Post;
import com.entity.auth.UserProfile;
import com.repository.UserProfileRepo;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(source = "author.avt", target = "authorAvatar")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "anonymous", target = "anonymous")
    @Mapping(source = "isPinned", target = "isPinned")
    @Mapping(source = "community.id", target = "communityId")
    PostDTO postToPostDTO(Post post);

    List<PostDTO> postsToPostDTOs(List<Post> posts);

    default Page<PostDTO> postPageToPostDTOPage(Page<Post> posts) {
        return posts.map(this::postToPostDTO);
    }

    @Mapping(source = "authorId", target = "author", qualifiedByName = "mapAuthor")
    Post postDTOToPost(PostDTO postDTO, @Context UserProfileRepo userRepository);

    @Named("mapAuthor")
    default UserProfile mapAuthor(Integer authorId, @Context UserProfileRepo userRepository) {
        return userRepository.findById(authorId).orElseThrow(() ->
                new RuntimeException("User not found with id: " + authorId));
    }
}

