package com.controller.uploadFile;


import com.service.UpLoadImageFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/upload")
@RestController
@RequiredArgsConstructor
public class CloudUpLoadController {

    private UpLoadImageFileService uploadImageFile;

    @PostMapping("/image")
    public String uploadImage(@RequestParam("file")MultipartFile file) throws IOException {
        return uploadImageFile.uploadImage(file);
    }
}