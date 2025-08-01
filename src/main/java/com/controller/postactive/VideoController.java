package com.controller.postactive;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/videos")
public class VideoController {

    @Value("${video.stream.dir}")
    private String videoDir; // Thư mục chứa các đoạn video .ts và .m3u8

    @GetMapping("/video/{fileName:.+}")
    public ResponseEntity<Resource> serveVideo(@PathVariable String fileName) {
        try {

            System.out.println("cou"+"----------------------------------------------------------");
            FileSystemResource video = new FileSystemResource(videoDir +"/"+ fileName);
            System.out.println(videoDir + fileName);
            if (!video.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = "application/octet-stream";  // Default

            if (fileName.endsWith(".m3u8")) {
                contentType = "application/vnd.apple.mpegurl";
            } else if (fileName.endsWith(".ts")) {
                contentType = "video/MP2T";
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + video.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(video);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/stream1")
//    public ResponseEntity<Resource> streamVideo(@RequestHeader(value = "Range", required = false) String range) throws IOException {
//        Resource video = resourceLoader.getResource("classpath:static/sample-video.mp4");
//        long contentLength = video.contentLength(); // Tổng độ dài video
//        HttpHeaders headers = new HttpHeaders();
//
//        long start = 0;
//        long end = contentLength - 1; // Byte cuối cùng
//
//        if (range != null) {
//            // Phân tích phạm vi Range từ client
//            HttpRange httpRange = HttpRange.parseRanges(range).get(0);
//            start = httpRange.getRangeStart(contentLength);
//            end = httpRange.getRangeEnd(contentLength);
//        }
//
//        // Thêm header Content-Range
//        headers.add("Content-Range", "bytes " + start + "-" + end + "/" + contentLength);
//        headers.add("Accept-Ranges", "bytes");
//        headers.add("Content-Type", "video/mp4");
//        headers.add("Content-Length", String.valueOf(end - start + 1));
//
//        InputStream videoStream = video.getInputStream();
//        videoStream.skip(start);
//
//        return ResponseEntity.status(range != null ? 206 : 200) // Nếu có Range, trả về 206 Partial Content
//                .headers(headers)
//                .body(new InputStreamResource(videoStream));
//    }


//    @GetMapping("/stream/range1")
//    public ResponseEntity<Resource> streamVideoRange(@RequestHeader(value = "Range", required = false) String range) throws IOException {
//        cnount++;
//        System.out.println(range);
//
//        Resource resource = resourceLoader.getResource("classpath:static/sample-video.mp4");
//
//        String contentType = "application/octet-stream";
//
//        //file ki length
//        long fileLength = resource.contentLength();
//
//
//        long rangeStart;
//        long rangeEnd;
//
//        if (range == null) {
//            rangeStart = 0;
//        }
//        else {
//            String[] ranges = range.replace("bytes=", "").split("-");
//            rangeStart = Long.parseLong(ranges[0]);
//        }
//        int CHUNK_SIZE=1024*1024;
//        rangeEnd = rangeStart +CHUNK_SIZE - 1;
//
//        if (rangeEnd >= fileLength) {
//            rangeEnd = fileLength - 1;
//        }
//        System.out.println("range start : " + rangeStart);
//        System.out.println("range end : " + rangeEnd);
//        try {
//            try (RandomAccessFile raf = new RandomAccessFile(resource.getFile(), "r")) {
//                raf.seek(rangeStart);
//                long contentLength = rangeEnd - rangeStart + 1;
//                byte[] data = new byte[(int) contentLength];
//                raf.readFully(data);
//
//                HttpHeaders headers = new HttpHeaders();
//                headers.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
//                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//                headers.add("Pragma", "no-cache");
//                headers.add("Expires", "0");
//                headers.add("X-Content-Type-Options", "nosniff");
//                headers.setContentLength(contentLength);
//
//                return ResponseEntity
//                        .status(HttpStatus.PARTIAL_CONTENT)
//                        .headers(headers)
//                        .contentType(MediaType.parseMediaType(contentType))
//                        .body(new ByteArrayResource(data));
//            }
//
//
//        } catch (IOException ex) {
//            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//
//
//
//    }
}

//package com.test;
//
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpRange;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//
//@RestController
//public class VideoStreamController {
//
//    private final ResourceLoader resourceLoader;
//
//    public VideoStreamController(ResourceLoader resourceLoader) {
//        this.resourceLoader = resourceLoader;
//    }
//
//    @GetMapping("/video")
//    public ResponseEntity<byte[]> streamVideo(@RequestHeader(value = "Range", required = false) String range) throws IOException {
//        Resource videoResource = resourceLoader.getResource("classpath:static/sample.mp4");
//        Path videoPath = videoResource.getFile().toPath();
//
//        byte[] videoData = Files.readAllBytes(videoPath);
//        long videoLength = videoData.length;
//
//        if (range == null) {
//            // Trả về toàn bộ video nếu không có Range header
//            return ResponseEntity.status(HttpStatus.OK)
//                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
//                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(videoLength))
//                    .body(videoData);
//        } else {
//            // Xử lý video streaming dựa trên Range header
//            HttpRange httpRange = HttpRange.parseRanges(range).get(0);
//            long start = httpRange.getRangeStart(videoLength);
//            long end = httpRange.getRangeEnd(videoLength);
//            byte[] partialData = new byte[(int) (end - start + 1)];
//            System.arraycopy(videoData, (int) start, partialData, 0, partialData.length);
//
//            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
//                    .header(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + videoLength)
//                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(partialData.length))
//                    .body(partialData);
//        }
//    }
//}


