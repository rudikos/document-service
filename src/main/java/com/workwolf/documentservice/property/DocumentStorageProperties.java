package com.workwolf.documentservice.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties(prefix = "file")
public class DocumentStorageProperties {

    @Getter
    @Setter
    private String uploadDir;

    public Path getStorageLocation() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }
}
