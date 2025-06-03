package bind.image.service;

import bind.image.dto.response.ImageResponse;
import bind.image.dto.response.ImageUploadResponse;
import bind.image.entity.ImageFile;
import bind.image.exception.ImageErrorCode;
import bind.image.exception.ImageException;
import bind.image.repository.ImageFileRepository;
import bind.image.util.ImageUtil;
import data.enums.ResourceCategory;
import data.enums.image.ImageStatus;
import data.enums.image.ImageVisibility;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.util.Locale.filter;


@Service
@RequiredArgsConstructor
public class ImageFileService {

    private final ImageFileRepository imageFileRepository;
    private final LocalImageStorage imageStorage;

    @Value("${image.upload.nginx.url}")
    private String publicUrlPrefix;

    public ImageUploadResponse upload(MultipartFile file,
                                      ResourceCategory category,
                                      String referenceId,
                                      String uploaderId,
                                      ImageVisibility visibility,
                                      Boolean isThumbnail
    ) {
        String uuid = UUID.randomUUID().toString();
        String webpFileName = uuid + ".webp";
        String datePath = LocalDateTime.now().toLocalDate().toString().replace("-", "/");
        String storedPath = "/" + category.name() + "/" + datePath + "/" + webpFileName;

        // WebP 변환 및 저장
        try {
            byte[] webpBytes = ImageUtil.toWebp(file, 0.8f);
            imageStorage.store(webpBytes, storedPath);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_PROCESSING_ERROR);
        }

        // (NSFW 등 안전성 검사 후) 임시상태로 등록
        ImageStatus status = ImageStatus.TEMP;

        ImageFile imageFile = ImageFile.builder()
                .uuidName(webpFileName)
                .originalName(file.getOriginalFilename())
                .storedPath(storedPath)
                .url(publicUrlPrefix + storedPath)
                .contentType("image/webp")
                .fileSize(file.getSize()) // 원본 사이즈 (WebP로 바뀌어도 무방)
                .category(category)
                .isThumbnail(isThumbnail)
                .referenceId(referenceId)
                .uploaderId(uploaderId)
                .status(status)
                .visibility(visibility)
                .createdAt(LocalDateTime.now())
                .build();

        imageFileRepository.save(imageFile);

        return ImageUploadResponse.builder()
                .id(imageFile.getId())
                .url(storedPath)
                .build();
    }

    public List<ImageResponse> getImageUrls(ResourceCategory category, String referenceId) {
        List<ImageFile> images = imageFileRepository.findByCategoryAndReferenceId(
                category, referenceId);

        if (images.isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
        }
        return images.stream()
                .filter(image -> image.getStatus() == ImageStatus.CONFIRMED )
                .map(image -> ImageResponse.builder()
                        .id(image.getId())
                        .isThumbnail(image.isThumbnail())
                        .url(image.getUrl())
                        .build())
                .toList();
    }

    public void confirmImage(Long imageId) {
        ImageFile image = imageFileRepository.findById(imageId)
                .orElseThrow(() -> new ImageException(ImageErrorCode.IMAGE_NOT_FOUND));

        if (image.getStatus() != ImageStatus.TEMP) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_TEMP);
        }
        image.confirm();
        imageFileRepository.save(image);
    }



    public void deleteImage(Long imageId) {
        ImageFile image = imageFileRepository.findById(imageId)
                .orElseThrow(() -> new ImageException(ImageErrorCode.IMAGE_NOT_FOUND));

        if (image.getStatus() == ImageStatus.PENDING_DELETE) {
            throw new ImageException(ImageErrorCode.IMAGE_ALREADY_PENDING_DELETE);
        }
        image.markPendingDelete();
        imageFileRepository.save(image);
    }


    public void markAsConfirmed(ResourceCategory category , String referenceId) {
        List<ImageFile> images = imageFileRepository.findByCategoryAndReferenceId(category,referenceId);

        images.forEach(ImageFile::confirm);
    }

    public void markAsPendingDelete(ResourceCategory category , String referenceId) {
        List<ImageFile> images = imageFileRepository.findByCategoryAndReferenceId(category, referenceId);
        if (images.isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
        }
        images.forEach(ImageFile::markPendingDelete);
    }


    @Scheduled(cron = "0 0 * * * *")
    public void deleteExpiredImages() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        List<ImageFile> expired = imageFileRepository.findByStatusAndCreatedAtBefore(ImageStatus.TEMP, cutoff);
        expired.addAll(imageFileRepository.findByStatusAndPendingDeleteAtBefore(ImageStatus.PENDING_DELETE, cutoff));
        expired.add(imageFileRepository.findByStatus(ImageStatus.REJECTED));
        for (ImageFile image : expired) {

            imageStorage.delete(image.getStoredPath());
            imageFileRepository.delete(image);
        }
    }

}
