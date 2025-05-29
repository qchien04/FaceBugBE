package com.service;

import com.DTO.CommentDTO;
import com.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentDTO addComment(CommentDTO comment);
    void deleteComment(Integer commentId);
    List<CommentDTO> getCommentsByPost(Integer postId);
    List<CommentDTO> getChildComment(Integer commentId);
}
