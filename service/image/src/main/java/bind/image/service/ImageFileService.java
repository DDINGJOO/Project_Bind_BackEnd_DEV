package bind.image.service;

import bind.image.dto.response.ImageResponse;
import bind.image.dto.response.ImageUploadResponse;
import bind.image.entity.ImageFile;
import bind.image.exception.ImageErrorCode;
import bind.image.exception.ImageException;
import bind.image.repository.ImageFileRepository;
import bind.image.utils.ImageUtil;
import data.enums.ResourceCategory;
import data.enums.image.ImageStatus;
import data.enums.image.ImageVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ImageFileService {

    private static final int THUMBNAIL_SIZE = 256;

    private final ImageFileRepository imageFileRepository;
    private final LocalImageStorage imageStorage;

    @Value("${image.upload.nginx.url}")
    private String publicUrlPrefix;

    public ImageUploadResponse upload(MultipartFile file,
                                      ResourceCategory category,
                                      String referenceId,
                                      String uploaderId,
                                      ImageVisibility visibility,
                                      Boolean isThumbnail) {
        String uuid = UUID.randomUUID().toString();
        String webpFileName = uuid + ".webp";
        String datePath = LocalDateTime.now().toLocalDate().toString().replace("-", "/");
        String storedPath = "/" + category.name() + "/" + datePath + "/" + webpFileName;

        // 1. 이미지 변환 및 저장
        try {
            byte[] webpBytes = Boolean.TRUE.equals(isThumbnail)
                    ? ImageUtil.toWebpThumbnail(file, THUMBNAIL_SIZE, THUMBNAIL_SIZE, 0.8f)
                    : ImageUtil.toWebp(file, 0.8f);
            imageStorage.store(webpBytes, storedPath);
        } catch (Exception e) {
            throw new ImageException(ImageErrorCode.IMAGE_PROCESSING_ERROR);
        }

        // 2. DB 저장 (상대경로만 저장, 공개 URL은 필요시 동적으로 생성)
        ImageFile imageFile = ImageFile.builder()
                .uuidName(webpFileName)
                .originalName(file.getOriginalFilename())
                .storedPath(storedPath)
                .contentType("image/webp")
                .fileSize(file.getSize())
                .category(category)
                .isThumbnail(isThumbnail)
                .referenceId(referenceId)
                .uploaderId(uploaderId)
                .status(ImageStatus.TEMP)
                .visibility(visibility)
                .createdAt(LocalDateTime.now())
                .build();

        imageFileRepository.save(imageFile);

        return ImageUploadResponse.builder()
                .url(publicUrlPrefix + storedPath)
                .build();
    }

    public List<ImageUploadResponse> uploadImages(List<MultipartFile> files,
                                                  ResourceCategory category,
                                                  String referenceId,
                                                  String uploaderId,
                                                  ImageVisibility visibility) {
        List<ImageUploadResponse> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            responses.add(upload(file, category, referenceId, uploaderId, visibility, false));
        }
        return responses;
    }

    /**
     * CONFIRMED 상태의 이미지만 반환. 공개 URL로 응답.
     */
    public List<ImageResponse> getImageUrls(ResourceCategory category, String referenceId) {
        List<ImageFile> images = imageFileRepository.findByCategoryAndReferenceId(category, referenceId);
        if (images.isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
        }
        return images.stream()
                .filter(image -> image.getStatus() == ImageStatus.CONFIRMED)
                .map(image -> ImageResponse.builder()
                        .referenceId(referenceId)
                        .isThumbnail(image.isThumbnail())
                        .url(publicUrlPrefix + image.getStoredPath())
                        .build())
                .toList();
    }

    public void confirmImage(Long imageId) {
        changeImageStatus(imageId, ImageStatus.TEMP, ImageStatus.CONFIRMED, ImageErrorCode.IMAGE_NOT_TEMP);
    }

    public void deleteImage(Long imageId) {
        changeImageStatus(imageId, null, ImageStatus.PENDING_DELETE, ImageErrorCode.IMAGE_ALREADY_PENDING_DELETE);
    }

    private void changeImageStatus(Long imageId, ImageStatus mustBeStatus, ImageStatus toStatus, ImageErrorCode notMatchError) {
        ImageFile image = imageFileRepository.findById(imageId)
                .orElseThrow(() -> new ImageException(ImageErrorCode.IMAGE_NOT_FOUND));

        if (mustBeStatus != null && image.getStatus() != mustBeStatus) {
            throw new ImageException(notMatchError);
        }
        if (toStatus == ImageStatus.PENDING_DELETE && image.getStatus() == ImageStatus.PENDING_DELETE) {
            throw new ImageException(notMatchError);
        }
        image.setStatus(toStatus);
        imageFileRepository.save(image);
    }

    public void markAsConfirmed(ResourceCategory category, String referenceId) {
        changeStatusForImages(category, referenceId, ImageStatus.CONFIRMED);
    }

    public void markAsPendingDeleteExceptTemp(ResourceCategory category, String referenceId) {
        List<ImageFile> images = imageFileRepository.findByCategoryAndReferenceId(category, referenceId);
        if (images.isEmpty()) {
            throw new ImageException(ImageErrorCode.IMAGE_NOT_FOUND);
        }

        // TEMP 아닌 이미지만 상태 변경
        images.stream()
                .filter(img -> img.getStatus() != ImageStatus.TEMP)
                .forEach(img -> img.setStatus(ImageStatus.PENDING_DELETE));

        imageFileRepository.saveAll(images);
    }


    private void changeStatusForImages(ResourceCategory category, String referenceId, ImageStatus newStatus) {
        List<ImageFile> images = imageFileRepository.findByCategoryAndReferenceId(category, referenceId);
        images.forEach(img -> img.setStatus(newStatus));
        imageFileRepository.saveAll(images);
    }

    /**
     * TEMP 1시간 초과, PENDING_DELETE 1시간 초과, REJECTED 모두 삭제
     */
    @Scheduled(cron = "0 0 * * * *")
    public void deleteExpiredImages() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
        List<ImageFile> expired = imageFileRepository.findByStatusAndCreatedAtBefore(ImageStatus.TEMP, cutoff);
        expired.addAll(imageFileRepository.findByStatusAndPendingDeleteAtBefore(ImageStatus.PENDING_DELETE, cutoff));
        expired.addAll(imageFileRepository.findByStatus(ImageStatus.REJECTED));
        for (ImageFile image : expired) {
            imageStorage.delete(image.getStoredPath());
            imageFileRepository.delete(image);
        }
    }
}
