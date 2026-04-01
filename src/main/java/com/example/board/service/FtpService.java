package com.example.board.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
public class FtpService {

    @Value("${ftp.host}")
    private String host;

    @Value("${ftp.port}")
    private int port;

    @Value("${ftp.username}")
    private String username;

    @Value("${ftp.password}")
    private String password;

    @Value("${ftp.directory}")
    private String remoteDir;

    public String uploadFile(MultipartFile file) {
        FTPClient ftpClient = new FTPClient();
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + extension;

        try {
            // 1. 서버 접속 및 로그인
            ftpClient.connect(host, port);
            ftpClient.login(username, password);

            // 2. 설정 (패시브 모드 필수)
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // 3. 디렉토리 이동 (없으면 생성 시도 가능)
            if (!ftpClient.changeWorkingDirectory(remoteDir)) {
                log.warn("디렉토리가 존재하지 않아 생성을 시도합니다: {}", remoteDir);
                ftpClient.makeDirectory(remoteDir);
                ftpClient.changeWorkingDirectory(remoteDir);
            }

            // 4. 파일 업로드
            try (InputStream inputStream = file.getInputStream()) {
                boolean success = ftpClient.storeFile(savedName, inputStream);
                if (success) {
                    log.info("파일 업로드 성공: {}", savedName);
                    return savedName;
                }
            }
        } catch (IOException e) {
            log.error("FTP 업로드 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 전송 실패", e);
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                log.error("FTP 연결 종료 중 오류: {}", ex.getMessage());
            }
        }
        return null;
    }
}
