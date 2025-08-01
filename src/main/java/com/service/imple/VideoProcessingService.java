package com.service.imple;

import java.io.IOException;

import com.entity.VideoStorage;
import com.exception.UploadVideoException;
import com.repository.VideoStorageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class VideoProcessingService {

    @Value("${video.upload.dir}")
    private String uploadDir;

    @Value("${video.stream.dir}")
    private String outputDir;

    @Autowired
    private VideoStorageRepo videoStorageRepo;

    public VideoProcessingService(VideoStorageRepo videoStorageRepo) {
        this.videoStorageRepo = videoStorageRepo;
    }

    public void handleUploadAndSplit2(Path inputVideo,int jobIndex) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("PRINCIPAL = " + principal.getClass());

        Integer myId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Optional<VideoStorage> videoStorage=videoStorageRepo.findByVideoNumberAndAuthorId(jobIndex,myId);
        if(videoStorage.isPresent()){

            Files.createDirectories(Paths.get(uploadDir));
            Files.createDirectories(Paths.get(outputDir));


            String playlistName = String.format("playlist_%03d.m3u8", jobIndex);
            splitToHlsWithFfmpeg2(inputVideo, jobIndex);

        }
        else{
            throw new UploadVideoException("Upload thất bại, bạn không có quyền!");
        }


    }

    public void splitToHlsWithFfmpeg2(Path inputVideo, int jobIndex) throws IOException {
        String playlist = String.format("playlist_%03d.m3u8", jobIndex);
        String segmentPattern = String.format("segment_%03d_%%03d.ts", jobIndex);

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", inputVideo.toString(),
                "-c", "copy",
                "-f", "hls",
                "-hls_time", "3",
                "-hls_list_size", "0",
                "-hls_segment_filename", outputDir + segmentPattern,
                outputDir + playlist
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            reader.lines().forEach(line -> System.out.println("[FFMPEG] " + line));
        } catch (IOException ignored) {}

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg trả lỗi: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Quá trình FFmpeg bị gián đoạn", e);
        }
    }
    public void splitToHlsWithFfmpeg(MultipartFile media,Integer jobIndex) throws IOException {
        Files.createDirectories(Paths.get(uploadDir));
        Files.createDirectories(Paths.get(outputDir));

        String originalName = StringUtils.cleanPath(media.getOriginalFilename());
        if (originalName == null || originalName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên file không hợp lệ");
        }
        Path inputPath = Paths.get(uploadDir, originalName);
        media.transferTo(inputPath);

        Process process = getProcess(inputPath.toString(), jobIndex);

        // 4. Đọc log FFmpeg để debug/ghi nhận
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[FFMPEG] " + line);
            }
        }

        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("FFmpeg trả về mã lỗi: " + exitCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Quá trình chờ FFmpeg bị gián đoạn", e);
        }

        System.out.println("Tách HLS segments thành công tại: " + outputDir);
    }
    private Process getProcess(String inputVideo, Integer jobIndex) throws IOException {
        String playlistName = String.format("playlist_%03d.m3u8", jobIndex);
        String segmentPattern = String.format("segment_%03d_%%03d.ts", jobIndex);

        ProcessBuilder pb = new ProcessBuilder(
                "C:\\Users\\chien\\Downloads\\ffmpeg-master-latest-win64-gpl-shared\\ffmpeg-master-latest-win64-gpl-shared\\bin\\ffmpeg.exe",
                "-i", inputVideo,
                "-c", "copy",
                "-f", "hls",
                "-hls_time", "3",
                "-hls_list_size", "0",
                "-hls_segment_filename", outputDir + File.separator + segmentPattern,
                outputDir + File.separator + playlistName
        );
        pb.redirectErrorStream(true);  // Gộp cả stdout và stderr

        return pb.start();
    }
}