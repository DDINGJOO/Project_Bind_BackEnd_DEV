package bind.image.controller;

import bind.image.dto.response.ImageResponse;
import bind.image.dto.response.ImageUploadResponse;
import bind.image.dto.response.NsfwDetectionResult;
import bind.image.exception.ImageException;
import bind.image.service.ImageFileService;
import bind.image.service.NsfwDetectionService;
import data.BaseResponse;
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
    private final NsfwDetectionService nsfwDetectionService;

    @PostMapping
    public ResponseEntity<BaseResponse<ImageUploadResponse>> upload(@RequestParam MultipartFile file,
                                                      @RequestParam ImageCategory category,
                                                      @RequestParam String referenceId,
                                                      @RequestParam String uploaderId,
                                                      @RequestParam(defaultValue = "PUBLIC") ImageVisibility visibility) {


        try {
            var result = imageFileService.upload(file, category, referenceId, uploaderId, visibility);
            return ResponseEntity.ok(BaseResponse.success(result));
        } catch (ImageException e) {
           return ResponseEntity.internalServerError().body(BaseResponse.fail(e.getErrorCode()));
        }
    }



    @GetMapping
    public ResponseEntity<BaseResponse<List<ImageResponse>>> getUrls(@RequestParam ImageCategory category,
                                                                     @RequestParam String referenceId) {
        try {
            List<ImageResponse> imageUrls = imageFileService.getImageUrls(category, referenceId);
            return ResponseEntity.ok(BaseResponse.success(imageUrls));
        } catch (ImageException e) {
            return ResponseEntity.internalServerError().body(BaseResponse.fail(e.getErrorCode()));
        }
    }

    @PatchMapping("/confirm")
    public ResponseEntity<BaseResponse<Long>> confirm(@RequestParam Long imageId) {
        try {
            imageFileService.confirmImage(imageId);
            return ResponseEntity.ok(BaseResponse.success(imageId));
        } catch (ImageException e) {
            return ResponseEntity.internalServerError().body(BaseResponse.fail(e.getErrorCode()));
        }
    }


    @DeleteMapping
    public ResponseEntity<BaseResponse<Long>> delete(@RequestParam Long imageId) {
        try {
            imageFileService.deleteImage(imageId);
            return ResponseEntity.ok(BaseResponse.success(imageId));
        } catch (ImageException e) {
            return ResponseEntity.internalServerError().body(BaseResponse.fail(e.getErrorCode()));
        }

    }



    //TODO : 실제 NSFW 감지 로직 구현 후 제거
    @PostMapping("/detect")
    public ResponseEntity<NsfwDetectionResult> detectNsfw(@RequestParam MultipartFile file) {

        return ResponseEntity.ok(
                NsfwDetectionResult.builder()
                        .safe(true)
                        .build()
        );
    }
}
