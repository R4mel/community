package dev.community.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(LocalFileStorageService.class);
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            logger.warn("Attempted to upload empty file");
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFilename;
        
        // Ensure proper path construction
        String uploadPath = uploadDir.endsWith("/") || uploadDir.endsWith("\\") ? uploadDir : uploadDir + "/";
        Path destinationPath = Paths.get(uploadPath + storedFileName);

        logger.info("Uploading file: {} to path: {}", originalFilename, destinationPath);

        try {
            Files.createDirectories(destinationPath.getParent());
            multipartFile.transferTo(destinationPath);
            
            String imageUrl = "/images/" + storedFileName;
            logger.info("File uploaded successfully. URL: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            logger.error("Failed to store file: {}", originalFilename, e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        
        // Ensure proper path construction
        String uploadPath = uploadDir.endsWith("/") || uploadDir.endsWith("\\") ? uploadDir : uploadDir + "/";
        Path filePath = Paths.get(uploadPath + fileName);

        logger.info("Deleting file: {} from path: {}", fileName, filePath);

        try {
            Files.deleteIfExists(filePath);
            logger.info("File deleted successfully: {}", fileName);
        } catch (IOException e) {
            logger.error("Failed to delete file: {}", fileName, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }
}
