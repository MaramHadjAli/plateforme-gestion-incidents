package tn.enicarthage.modules.upload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        String storedFileName = fileStorageService.store(file);
        // Returns the relative URL path or just the filename representing the uploaded file
        return ResponseEntity.ok(Map.of(
            "message", "File uploaded successfully",
            "url", "/uploads/" + storedFileName
        ));
    }
}
