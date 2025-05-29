package com.controller;

import com.service.auth.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/public")
@AllArgsConstructor
public class PublicController {
    @GetMapping("/")
    public ResponseEntity<String> getUserTokenHandler(){
        String token="Connect success";
        return new ResponseEntity<String>(token, HttpStatus.OK);
    }

}
