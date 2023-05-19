package com.workwolf.documentservice.service.impl;

import com.workwolf.documentservice.exception.DocumentInfectedException;
import com.workwolf.documentservice.service.FileScanService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.capybara.clamav.ClamavClient;
import xyz.capybara.clamav.commands.scan.result.ScanResult;

import java.io.IOException;

@Service
public class FileScanServiceClam implements FileScanService {
    private final ClamavClient clamAVClient;

    public FileScanServiceClam() {
        this.clamAVClient = new ClamavClient("localhost");
    }

    public void scanFile(MultipartFile file) {
        ScanResult scanResult;
        try {
            scanResult = this.clamAVClient.scan(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (scanResult instanceof ScanResult.VirusFound) {
            throw new DocumentInfectedException("virus found, cannot proceed");
        }
    }
}
