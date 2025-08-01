package com.controller.postactive;

import com.DTO.CommentDTO;
import com.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{postId}/add")
    public ResponseEntity<CommentDTO> addComments(@RequestBody CommentDTO commentDTO,@PathVariable Integer postId) {
        CommentDTO rs=commentService.addComment(commentDTO);
        return new ResponseEntity<CommentDTO>(rs, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Integer postId) {
        List<CommentDTO> commentDTOS=commentService.getCommentsByPost(postId);

        return new ResponseEntity<List<CommentDTO>>(commentDTOS, HttpStatus.OK);
    }

    @GetMapping("/{commentId}/child")
    public ResponseEntity<List<CommentDTO>> getChildComment(@PathVariable Integer commentId) {
        List<CommentDTO> commentDTOS=commentService.getChildComment(commentId);

        return new ResponseEntity<List<CommentDTO>>(commentDTOS, HttpStatus.OK);
    }
}
