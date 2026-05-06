package com.example.board.controller;

import com.example.board.service.FtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FtpService ftpService;

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("attach") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("ok", 0, "message", "파일이 없습니다."));
        }

        List<Map<String, String>> items = new ArrayList<>();
        int maxFiles = 10;
        
        if (files.size() > maxFiles) {
            return ResponseEntity.status(422).body(Map.of("ok", 0, "message", "최대 10개까지 업로드 가능합니다."));
        }

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String savedFileName = ftpService.uploadFile(file);
                String filePath = ftpService.getServerUrl() + "/images/" + savedFileName;
                
                items.add(Map.of(
                    "name", file.getOriginalFilename(),
                    "path", filePath
                ));
            }
        }

        if (items.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("ok", 0, "message", "업로드할 파일이 없습니다."));
        }

        return ResponseEntity.status(201).body(Map.of("ok", 1, "item", items));
    }
}