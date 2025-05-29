package bind.image.service;

import bind.image.dto.response.ImageResponse;
import bind.image.dto.response.ImageUploadResponse;
import bind.image.entity.ImageFile;
import bind.image.exception.ImageErrorCode;
import bind.image.exception.ImageException;
import bind.image.repository.ImageFileRepository;
import data.enums.ResourceCategory;
import data.enums.image.ImageStatus;
import data.enums.image.ImageVisibility;
import lombok.RequiredArgsConstructor;

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



    public ImageUploadResponse upload(MultipartFile file, ResourceCategory category, String referenceId, String uploaderId, ImageVisibility visibility) {
        String uuid = UUID.randomUUID().toString();
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));

        String fileName = uuid + extension;
        String datePath = LocalDateTime.now().toLocalDate().toString().replace("-", "/");
        String storedPath = "/upload/images/" + category.name() + "/" + datePath + "/" + fileName;

        imageStorage.store(file, storedPath);



        //TODO : 실제 NSFW 감지 로직 구현 후 제거
        boolean isSafe = true;


        ImageStatus status = isSafe ? ImageStatus.TEMP  : ImageStatus.REJECTED;


        ImageFile imageFile = ImageFile.builder()
                .uuidName(fileName)
                .originalName(file.getOriginalFilename())
                .storedPath(storedPath)
                .thumbnailPath(null) // 썸네일 생성 미적용 상태
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .category(category)
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
                        .url(image.getStoredPath())
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
