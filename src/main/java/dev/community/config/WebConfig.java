package dev.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure the upload directory path ends with a slash
        String uploadPath = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        logger.info("Configuring resource handler for images. Upload path: {}", uploadPath);
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadPath);
    }
} 