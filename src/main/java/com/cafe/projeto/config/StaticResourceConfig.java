package com.cafe.projeto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    private final String uploadProdutosDir;

    public StaticResourceConfig(@Value("${app.upload.produtos-dir}") String uploadProdutosDir) {
        this.uploadProdutosDir = uploadProdutosDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadProdutosDir).toAbsolutePath().normalize();
        String resourceLocation = uploadPath.toUri().toString();

        if (!resourceLocation.endsWith("/")) {
            resourceLocation = resourceLocation + "/";
        }

        registry.addResourceHandler("/uploads/produtos/**")
                .addResourceLocations(resourceLocation);
    }
}
