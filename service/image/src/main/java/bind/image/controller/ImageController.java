package bind.image.controller;

import bind.image.dto.response.ImageUploadResponse;
import bind.image.service.ImageFileService;
import data.enums.image.ImageCategory;
import data.enums.image.ImageVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageFileService imageFileService;

    @PostMapping
    public ResponseEntity<ImageUploadResponse> upload(@RequestParam MultipartFile file,
                                                      @RequestParam ImageCategory category,
                                                      @RequestParam String referenceId,
                                                      @RequestParam String uploaderId,
                                                      @RequestParam(defaultValue = "PUBLIC") ImageVisibility visibility) {
        return ResponseEntity.ok(imageFileService.upload(file, category, referenceId, uploaderId, visibility));
    }

    @GetMapping
    public ResponseEntity<List<String>> getUrls(@RequestParam ImageCategory category,
                                                @RequestParam String referenceId) {
        return ResponseEntity.ok(imageFileService.getImageUrls(category, referenceId));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long id) {
        imageFileService.confirmImage(id);
        return ResponseEntity.ok().build();
    }
}
