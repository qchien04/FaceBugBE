package com.repository;

import com.DTO.CommentDTO;
import com.DTO.ProfileSummary;
import com.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Integer> {
    @Query("SELECT new com.DTO.CommentDTO(" +
            "c.id," +
            "c.author.id, " +
            "c.author.name, " +
            "c.author.avt, " +
            "c.content, " +
            "c.post.id, " +
            "c.replyCounter, " +
            "c.parent.id, " +
            "c.createdAt)" +
            "FROM Comment c " +
            "JOIN c.author " +
            "WHERE c.post.id=:postId AND c.parent IS NULL")
    List<CommentDTO> findByPostId(@Param("postId")Integer postId);

    @Query("SELECT new com.DTO.CommentDTO(" +
            "c.id," +
            "c.author.id, " +
            "c.author.name, " +
            "c.author.avt, " +
            "c.content, " +
            "c.post.id, " +
            "c.replyCounter, " +
            "c.parent.id, " +
            "c.createdAt)" +
            "FROM Comment c " +
            "JOIN c.author " +
            "WHERE c.parent.id=:commentId")
    List<CommentDTO> getChildComment(@Param("commentId")Integer commentId);

    List<Comment> findByParentId(Long parentId);
}