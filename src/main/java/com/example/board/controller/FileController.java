package com.example.board.controller;

import com.example.board.service.FtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*") // 모든 도메인 허용
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FtpService ftpService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어있습니다.");
        }

        String savedFileName = ftpService.uploadFile(file);

        if (savedFileName != null) {
            return ResponseEntity.ok("파일 업로드 완료. 저장된 파일명: " + savedFileName);
        } else {
            return ResponseEntity.internalServerError().body("파일 업로드에 실패했습니다.");
        }
    }
}
