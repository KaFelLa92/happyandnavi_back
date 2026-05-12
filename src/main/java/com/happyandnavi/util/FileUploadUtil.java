package com.happyandnavi.util;

import com.happyandnavi.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * =========================================
 * FileUploadUtil 클래스
 * =========================================
 * 
 * 파일 업로드 관련 유틸리티 클래스입니다.
 * 이미지 업로드, 삭제, 검증 등의 기능을 제공합니다.
 */
@Slf4j
@Component
public class FileUploadUtil {
    
    /**
     * 파일 업로드 기본 경로 (application.properties에서 설정)
     */
    @Value("${file.upload.base-path}")
    private String basePath;
    
    /**
     * 허용되는 이미지 확장자 목록
     */
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    /** 허용 동영상 확장자 */
    private static final List<String> ALLOWED_VIDEO_EXTENSIONS = Arrays.asList(
            "mp4", "mov", "avi", "webm", "mkv"
    );

    /** 이미지 + 동영상 통합 */
    private static final List<String> ALLOWED_MEDIA_EXTENSIONS;
    static {
        ALLOWED_MEDIA_EXTENSIONS = new java.util.ArrayList<>();
        ALLOWED_MEDIA_EXTENSIONS.addAll(ALLOWED_IMAGE_EXTENSIONS);
        ALLOWED_MEDIA_EXTENSIONS.addAll(ALLOWED_VIDEO_EXTENSIONS);
    }

    /** 최대 파일 크기: 200MB (동영상 고려) */
    private static final long MAX_FILE_SIZE = 200L * 1024 * 1024;

    /** 이미지 최대 파일 크기: 50MB */
    private static final long MAX_IMAGE_SIZE = 50L * 1024 * 1024;

    // ============================================
    // 추억일기 미디어 업로드 (이미지 + 동영상)
    // ============================================

    /**
     * 추억일기 미디어(이미지 또는 동영상) 업로드
     *
     * 260508: 동영상(mp4, mov 등) 지원 추가
     *
     * @param userId 사용자 ID
     * @param file   업로드할 파일
     * @return 저장된 파일의 상대 경로
     */
    public String uploadMemoryMedia(Long userId, MultipartFile file) {
        validateMediaFile(file);

        String extension  = getFileExtension(file.getOriginalFilename()).toLowerCase();
        boolean isVideo   = ALLOWED_VIDEO_EXTENSIONS.contains(extension);
        String subDir     = isVideo ? "memory_video" : "memory";

        // 저장 경로: basePath/{subDir}/{userId}/yyyy/MM/
        String datePath  = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String uploadDir = String.format("%s/%s/%d/%s", basePath, subDir, userId, datePath);
        createDirectoryIfNotExists(uploadDir);

        String newFilename = UUID.randomUUID().toString() + "." + extension;
        String fullPath    = uploadDir + "/" + newFilename;
        saveFile(file, fullPath);

        String relativePath = String.format("/%s/%d/%s/%s", subDir, userId, datePath, newFilename);
        log.info("미디어 업로드 완료: {}", relativePath);
        return relativePath;
    }

    /**
     * 추억일기 이미지 업로드 (하위 호환 유지)
     *
     * @deprecated uploadMemoryMedia() 사용을 권장합니다.
     */
    @Deprecated
    public String uploadMemoryImage(Long userId, MultipartFile file) {
        return uploadMemoryMedia(userId, file);
    }

    // ============================================
    // 반려동물 프로필 사진 업로드 (이미지 전용)
    // ============================================

    /**
     * 반려동물 프로필 사진 업로드
     *
     * @param userId 사용자 ID
     * @param file   업로드할 이미지 파일
     * @return 저장된 파일의 상대 경로
     */
    public String uploadProfileImage(Long userId, MultipartFile file) {
        // 프로필은 이미지만 허용
        validateImageFile(file);

        String uploadDir = String.format("%s/profile/%d", basePath, userId);
        createDirectoryIfNotExists(uploadDir);

        String extension   = getFileExtension(file.getOriginalFilename());
        String newFilename = "profile_" + UUID.randomUUID() + "." + extension;
        String fullPath    = uploadDir + "/" + newFilename;
        saveFile(file, fullPath);

        String relativePath = String.format("/profile/%d/%s", userId, newFilename);
        log.info("프로필 사진 업로드 완료: {}", relativePath);
        return relativePath;
    }
    
    /**
     * 파일 삭제
     * 
     * @param relativePath 삭제할 파일의 상대 경로
     */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return;
        }
        
        try {
            Path filePath = Paths.get(basePath + relativePath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("파일 삭제 완료: {}", relativePath);
            }
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", relativePath, e);
            // 삭제 실패해도 예외를 던지지 않음 (로그만 기록)
        }
    }

    // ============================================
    // 유효성 검증
    // ============================================

    /**
     * 이미지 + 동영상 통합 검증
     *
     * @param file 검증할 파일
     */
    private void validateMediaFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("파일이 없습니다.", 400);
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("파일 크기가 너무 큽니다. 최대 200MB까지 업로드 가능합니다.", 400);
        }

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new BusinessException("파일명이 없습니다.", 400);
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_MEDIA_EXTENSIONS.contains(extension)) {
            throw new BusinessException(
                    "허용되지 않는 파일 형식입니다. (허용: " + String.join(", ", ALLOWED_MEDIA_EXTENSIONS) + ")",
                    400
            );
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
            throw new BusinessException("이미지 또는 동영상 파일만 업로드 가능합니다.", 400);
        }
    }


    /**
     * 이미지 파일 전용 유효성 검증 (프로필 등에서 사용)
     * 
     * @param file 검증할 파일
     */
    private void validateImageFile(MultipartFile file) {
        // 파일 존재 여부 확인
        if (file == null || file.isEmpty()) {
            throw new BusinessException("파일이 없습니다.", 400);
        }
        
        // 파일 크기 확인
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("파일 크기가 너무 큽니다. 최대 50MB까지 업로드 가능합니다.", 400);
        }
        
        // 파일 확장자 확인
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new BusinessException("파일명이 없습니다.", 400);
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new BusinessException(
                    "허용되지 않는 파일 형식입니다. (허용: " + String.join(", ", ALLOWED_IMAGE_EXTENSIONS) + ")",
                    400
            );
        }
        
        // Content-Type 확인 (추가 보안)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("이미지 파일만 업로드 가능합니다.", 400);
        }
    }

    // ============================================
    // 유틸리티
    // ============================================

    /**
     * 파일 확장자 추출
     * 
     * @param filename 파일명
     * @return 확장자 (점 제외)
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
    
    /**
     * 디렉토리 생성 (없는 경우)
     * 
     * @param dirPath 생성할 디렉토리 경로
     */
    private void createDirectoryIfNotExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                log.info("디렉토리 생성: {}", dirPath);
            } else {
                throw new BusinessException("디렉토리 생성에 실패했습니다.", 500);
            }
        }
    }
    
    /**
     * 파일 저장
     * 
     * @param file 저장할 파일
     * @param fullPath 저장할 전체 경로
     */
    private void saveFile(MultipartFile file, String fullPath) {
        try {
            Path path = Paths.get(fullPath);
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            log.error("파일 저장 실패: {}", fullPath, e);
            throw new BusinessException("파일 저장에 실패했습니다.", 500, e);
        }
    }

}
