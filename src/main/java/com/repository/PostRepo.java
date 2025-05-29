package com.repository;

import com.constant.CategoryContent;
import com.constant.MediaType;
import com.entity.Post;
import com.entity.auth.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepo extends JpaRepository<Post,Integer> {

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.author.id=:userId AND p.type=PostType.PERSONAL ORDER BY p.createdAt DESC")
    Page<Post> findAllWithAuthorId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id=:id ORDER BY p.createdAt DESC")
    Post findPostById(@Param("id") Integer id);

    @Query("SELECT media FROM Post p WHERE p.mediaType=:mediaType AND p.author.id=:userId ORDER BY p.createdAt DESC")
    List<String> getMedia(@Param("userId") Integer userId, MediaType mediaType);

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.author " +
            "WHERE p.mediaType !=MediaType.NONE " +
            "AND p.community.id=:communityId " +
            "ORDER BY p.createdAt DESC")
    List<Post> getMediaGroup(@Param("communityId") Integer communityId);

    @Query("SELECT p FROM Post p JOIN FETCH p.author")
    List<Post> getSuggestPost(@Param("userId") Integer userId);

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.author " +
            "WHERE p.author.accountType=AccountType.PAGE " +
            "AND p.type=PostType.PERSONAL " +
            "AND p.mediaType=MediaType.VIDEO " +
            "ORDER BY p.createdAt DESC")
    List<Post> getSuggestPostVideo(@Param("userId") Integer userId);

    @Query("SELECT p FROM Post p " +
            "JOIN FETCH p.author " +
            "WHERE p.author.accountType=AccountType.PAGE " +
            "AND p.type=PostType.PERSONAL " +
            "AND p.mediaType=MediaType.VIDEO " +
            "AND p.author.categoryContent=:categoryContent "+
            "ORDER BY p.createdAt DESC")
    List<Post> getSuggestPostVideoWithContent(@Param("userId") Integer userId , CategoryContent categoryContent);

    @Query("SELECT p FROM Post p JOIN FETCH p.author " +
            "WHERE p.community.id=:id " +
            "AND p.type=PostType.GROUP " +
            "AND (:pin = false OR p.isPinned = true) " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findPostByCommunityId(@Param("id") Integer id,Boolean pin,Pageable pageable);

    @Query("""
        SELECT p
        FROM Post p
        JOIN FETCH p.author a
        JOIN CommunityUserprofile cup ON p.community.id = cup.community.id
        WHERE cup.userProfile.id = :userId
          AND p.type = PostType.GROUP
        ORDER BY p.createdAt DESC
    """)
    Page<Post> findAllPostAllCommunity(@Param("userId") Integer userId,Pageable pageable);


    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN FETCH p.author "+
            "WHERE p.mediaType=:mediaType " +
            "AND p.author.id=:userId " +
            "AND p.type=PostType.PERSONAL " +
            "ORDER BY p.createdAt DESC")
    List<Post> getPostUserByMediaType(@Param("userId") Integer userId,MediaType mediaType);


    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN FETCH p.author " +
            "WHERE p.id IN :postIds")
    List<Post> findPostsByIdList(@Param("postIds") List<Integer> postIds);


}