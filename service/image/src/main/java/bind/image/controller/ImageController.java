package bind.image.controller;

import bind.image.dto.response.ImageResponse;
import bind.image.dto.response.ImageUploadResponse;
import bind.image.dto.response.NsfwDetectionResult;
import bind.image.exception.ImageException;
import bind.image.service.ImageFileService;
import data.BaseResponse;
import data.enums.ResourceCategory;
import data.enums.image.ImageVisibility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/images")
public class ImageController {

    private final ImageFileService imageFileService;

    @PostMapping
    public ResponseEntity<BaseResponse<ImageUploadResponse>> upload(@RequestParam MultipartFile file,
                                                      @RequestParam ResourceCategory category,
                                                      @RequestParam String referenceId,
                                                      @RequestParam String uploaderId,
                                                      @RequestParam(defaultValue = "PUBLIC") ImageVisibility visibility) {


        log.info("Image upload request: category={}, referenceId={}, uploaderId={}, visibility={}",
                category, referenceId, uploaderId, visibility);
        try {
            var result = imageFileService.upload(file, category, referenceId, uploaderId, visibility);
            return ResponseEntity.ok(BaseResponse.success(result));
        } catch (ImageException e) {
           return ResponseEntity.internalServerError().body(BaseResponse.fail(e.getErrorCode()));
        }
    }



    @GetMapping
    public ResponseEntity<BaseResponse<List<ImageResponse>>> getUrls(@RequestParam ResourceCategory category,
                                                                     @RequestParam String referenceId) {
        log.info("Get image URLs request: category={}, referenceId={}", category, referenceId);
        try {
            List<ImageResponse> imageUrls = imageFileService.getImageUrls(category, referenceId);
            return ResponseEntity.ok(BaseResponse.success(imageUrls));
        } catch (ImageException e) {
            return ResponseEntity.internalServerError().body(BaseResponse.fail(e.getErrorCode()));
        }
    }

    @PatchMapping("/confirm")
    public ResponseEntity<BaseResponse<Long>> confirm(@RequestParam Long imageId) {
        log.info("Confirm image request: imageId={}", imageId);
        try {
            imageFileService.confirmImage(imageId);
            return ResponseEntity.ok(BaseResponse.success(imageId));
        } catch (ImageException e) {
            return ResponseEntity.internalServerError().body(BaseResponse.fail(e.getErrorCode()));
        }
    }

    @PatchMapping("/confirms")
    public ResponseEntity<BaseResponse<Long>> confirms(@RequestParam ResourceCategory category,
                                                       @RequestParam String referenceId) {
        log.info(" Confirm images request: category={}, referenceId={}", category, referenceId);
        try {
            imageFileService.markAsConfirmed(category, referenceId);
            return ResponseEntity.ok(BaseResponse.success());
        } catch (ImageException e) {
            return ResponseEntity.internalServerError().body(BaseResponse.fail(e.getErrorCode()));
        }
    }


    @DeleteMapping
    public ResponseEntity<BaseResponse<Long>> delete(@RequestParam Long imageId) {
        log.info("Delete image request: imageId={}", imageId);
        try {
            imageFileService.deleteImage(imageId);
            return ResponseEntity.ok(BaseResponse.success(imageId));
        } catch (ImageException e) {
            return ResponseEntity.internalServerError().body(BaseResponse.fail(e.getErrorCode()));
        }
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<BaseResponse<Long>> deletes(@RequestParam ResourceCategory category,
                                                      @RequestParam String referenceId) {
        log.info("Delete images request: category={}, referenceId={}", category, referenceId);
        try {
            imageFileService.markAsPendingDelete(category, referenceId);
            return ResponseEntity.ok(BaseResponse.success());
        } catch (ImageException e) {
            return ResponseEntity.internalServerError().body(BaseResponse.fail(e.getErrorCode()));
        }
    }



    //TODO : 실제 NSFW 감지 로직 구현 후 제거
    @PostMapping("/detect")
    public ResponseEntity<NsfwDetectionResult> detectNsfw(@RequestParam MultipartFile file) {
        log.info("NSFW detection request for file: {}", file.getOriginalFilename());

        return ResponseEntity.ok(
                NsfwDetectionResult.builder()
                        .safe(true)
                        .build()
        );
    }
}
