package com.workwolf.documentservice.service.impl;

import com.workwolf.documentservice.exception.DocumentStorageException;
import com.workwolf.documentservice.property.DocumentStorageProperties;
import com.workwolf.documentservice.service.DocumentStorageService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class DocumentStorageServiceImpl implements DocumentStorageService {

    @Getter
    private final Path fileStorageLocation;

    @Autowired
    public DocumentStorageServiceImpl(DocumentStorageProperties documentStorageProperties) {
        this.fileStorageLocation = documentStorageProperties.getStorageLocation();
    }

    @Override
    public String store(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        validateFile(fileName);
        writeFile(fileName, file);
        return fileName;
    }

    private void validateFile(String fileName) {
        if (fileName.contains("..")) {
            throw new DocumentStorageException("Sorry! Filename contains invalid path sequence " + fileName);
        }
    }

    private void writeFile(String fileName, MultipartFile file) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new DocumentStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
}
