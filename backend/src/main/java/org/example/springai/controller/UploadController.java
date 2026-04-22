package org.example.springai.controller;

import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
public class UploadController {

    private static final Path UPLOAD_DIR = Paths.get("uploads");
    private static final List<String> ALLOWED =
            List.of("png", "jpg", "jpeg", "pdf", "txt");

    public UploadController() throws IOException {
        Files.createDirectories(UPLOAD_DIR);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("username") String username
    ) throws IOException {

        if (file.isEmpty()) {
            return Map.of("success", false, "message", "empty file");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = getExt(original).toLowerCase();

        if (!ALLOWED.contains(ext)) {
            return Map.of("success", false, "message", "unsupported type");
        }

        String stored = UUID.randomUUID() + "_" + original;
        Path target = UPLOAD_DIR.resolve(stored);

        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return Map.of(
                "success", true,
                "originalFilename", original,
                "storedFilename", stored,
                "username", username,
                "size", file.getSize()
        );
    }

    private String getExt(String name) {
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(i + 1) : "";
    }
}