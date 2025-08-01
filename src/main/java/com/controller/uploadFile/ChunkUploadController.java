package com.controller.uploadFile;

import com.service.imple.CustomUserDetails;
import com.service.imple.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/upload2")
@RequiredArgsConstructor
public class ChunkUploadController {

    private final VideoProcessingService videoProcessingService;

    @Value("${video.upload.dir}")
    private String uploadDir;

    // Lưu trữ thông tin về các chunk đã upload
    private final Map<String, boolean[]> uploadedChunks = new ConcurrentHashMap<>();

    @PostMapping("/chunk")
    public ResponseEntity<?> uploadChunk(
            @RequestParam("chunk") MultipartFile chunk,
            @RequestParam("chunkIndex") int chunkIndex,
            @RequestParam("totalChunks") int totalChunks,
            @RequestParam("fileName") String fileName) {

        try {

            System.out.println("Chunk "+chunkIndex);
            Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            System.out.println(myId);
            System.out.println("----------------------------------------------------------------------------");
            // Tạo thư mục temp nếu chưa tồn tại
            Path tempDir = Paths.get(uploadDir, "temp");
            Files.createDirectories(tempDir);

            // Tạo file tạm cho chunk
            String tempFileName = fileName + ".part" + chunkIndex;
            Path chunkPath = tempDir.resolve(tempFileName);
            chunk.transferTo(chunkPath);

            // Cập nhật trạng thái chunk đã upload
            uploadedChunks.computeIfAbsent(fileName, k -> new boolean[totalChunks])[chunkIndex] = true;

            // nếu đã upload hết
            boolean[] chunks = uploadedChunks.get(fileName);
            boolean allUploaded = true;
            for (boolean uploaded : chunks) {
                if (!uploaded) {
                    allUploaded = false;
                    break;
                }
            }

            if (allUploaded) {
                // Gộp chunk
                Path finalPath = Paths.get(uploadDir, fileName);
                Files.deleteIfExists(finalPath);

                try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(finalPath))) {
                    for (int i = 0; i < totalChunks; i++) {
                        Path chunkFile = tempDir.resolve(fileName + ".part" + i);
                        Files.copy(chunkFile, out);
                        Files.delete(chunkFile);
                    }
                }


                videoProcessingService.handleUploadAndSplit2(finalPath,
                    Integer.parseInt(fileName)
                );


                // Xóa chunk đã upload
                uploadedChunks.remove(fileName);

                Map<String, Object> response = new HashMap<>();
                response.put("message", "Upload completed");
                response.put("fileName", fileName);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.ok().body("Chunk uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error uploading chunk: " + e.getMessage());
        }
    }

    @GetMapping("/status/{fileName}")
    public ResponseEntity<?> getUploadStatus(@PathVariable String fileName) {
        boolean[] chunks = uploadedChunks.get(fileName);
        if (chunks == null) {
            return ResponseEntity.notFound().build();
        }

        int uploadedCount = 0;
        for (boolean uploaded : chunks) {
            if (uploaded) uploadedCount++;
        }

        Map<String, Object> status = new HashMap<>();
        status.put("uploadedChunks", uploadedCount);
        status.put("totalChunks", chunks.length);
        status.put("completed", uploadedCount == chunks.length);

        return ResponseEntity.ok(status);
    }
} 