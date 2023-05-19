package com.workwolf.documentservice.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileScanService {
    void scanFile(MultipartFile file);
}
