package dev.community.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    /**
     * MultipartFile을 받아 스토리지에 저장하고, 접근 가능한 URL을 반환한다.
     *
     * @param multipartFile 저장할 파일
     * @return 저장된 파일의 접근 URL
     */
    String uploadFile(MultipartFile multipartFile);

    /**
     * 파일 URl을 받아 스토리지에서 해당 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 URL
     */
    void deleteFile(String fileUrl);
}
