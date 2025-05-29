package com.exception;


import com.entity.Comment;
import com.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(UserException.class)
    @ResponseBody
    public ResponseEntity<ErrorDetail> UserExceptionHandler(UserException e, WebRequest req){
        ErrorDetail errorDetail=new ErrorDetail(e.getMessage(),req.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<ErrorDetail>(errorDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversationException.class)
    public ResponseEntity<ApiResponse> ConversationExceptionHandler(ConversationException e, WebRequest req){

        return new ResponseEntity<ApiResponse>(new ApiResponse("Conversation error",false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ApiResponse> ConversationExceptionHandler(CommentException e, WebRequest req){

        return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(),false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostException.class)
    public ResponseEntity<ApiResponse> PostExceptionHandler(PostException e, WebRequest req){

    return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(),  false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommunityException.class)
    public ResponseEntity<ApiResponse> PostExceptionHandler(CommunityException e, WebRequest req){

        return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(),false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FriendException.class)
    public ResponseEntity<ApiResponse> ConversationExceptionHandler(FriendException e, WebRequest req){

        return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(),false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UploadVideoException.class)
    public ResponseEntity<ApiResponse> ConversationExceptionHandler(UploadVideoException e, WebRequest req){

        return new ResponseEntity<ApiResponse>(new ApiResponse(e.getMessage(),false), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetail> MethodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e, WebRequest req){

        String error=e.getBindingResult().getFieldError().getDefaultMessage();
        ErrorDetail errorDetail=new ErrorDetail("Validation error",error, LocalDateTime.now());
        return new ResponseEntity<ErrorDetail>(errorDetail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorDetail> NoHandlerFoundExceptionHandler(MethodArgumentNotValidException e, WebRequest req){
        ErrorDetail errorDetail=new ErrorDetail("Enpoint not found",req.getDescription(false), LocalDateTime.now());
        return new ResponseEntity<ErrorDetail>(errorDetail, HttpStatus.BAD_REQUEST);
    }


//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorDetail> otherExceptionHandler(Exception e, WebRequest req){
//        ErrorDetail errorDetail=new ErrorDetail(e.getMessage(),req.getDescription(false), LocalDateTime.now());
//        return new ResponseEntity<ErrorDetail>(errorDetail, HttpStatus.BAD_REQUEST);
//    }

}
