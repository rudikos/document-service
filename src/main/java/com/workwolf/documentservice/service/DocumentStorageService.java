package com.workwolf.documentservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentStorageService {
    String store(MultipartFile file);
}
