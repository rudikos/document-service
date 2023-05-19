package com.workwolf.documentservice.endpoint;

import com.workwolf.documentservice.exception.DocumentStorageException;
import com.workwolf.documentservice.service.FileScanService;
import com.workwolf.documentservice.service.impl.DocumentInspectServiceImpl;
import com.workwolf.documentservice.service.impl.DocumentStorageServiceImpl;
import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("documents")
public class DocumentController {
    private static final Collection<String> VALID_EXTENSIONS = List.of("html", "pdf", "doc", "docx", "txt", "rtf");
    private final DocumentStorageServiceImpl documentStorageService;
    private final DocumentInspectServiceImpl documentScanService;
    private final FileScanService fileScanService;

    @Autowired
    public DocumentController(DocumentStorageServiceImpl documentStorageService, DocumentInspectServiceImpl documentScanService,
                              FileScanService fileScanService) {
        this.documentStorageService = documentStorageService;
        this.documentScanService = documentScanService;
        this.fileScanService = fileScanService;
    }

    @PostMapping("/upload")
    public ResponseEntity<JSONObject> uploadFile(@RequestParam("file") MultipartFile file) {
        checkExtension(file);
        fileScanService.scanFile(file);
        String fileName = documentStorageService.store(file);
        JSONObject result = documentScanService.inspect(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result);
    }

    private void checkExtension(MultipartFile file) {
        if (!VALID_EXTENSIONS.contains(FilenameUtils.getExtension(file.getOriginalFilename()))) {
            throw new DocumentStorageException("Input format of the file " + file.getOriginalFilename() + " is not supported.");
        }
    }
}
