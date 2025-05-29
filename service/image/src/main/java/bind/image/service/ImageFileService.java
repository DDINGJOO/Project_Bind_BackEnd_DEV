package bind.image.service;

import bind.image.dto.response.ImageUploadResponse;
import bind.image.entity.ImageFile;
import bind.image.exception.ImageErrorCode;
import bind.image.exception.ImageException;
import bind.image.repository.ImageFileRepository;
import data.enums.image.ImageCategory;
import data.enums.image.ImageStatus;
import data.enums.image.ImageVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageFileService {
    private final ImageFileRepository imageFileRepository;

    private final LocalImageStorage imageStorage;

    private final NsfwDetectionService nsfwDetectionService;


    public ImageUploadResponse upload(MultipartFile file, ImageCategory category, String referenceId, String uploaderId, ImageVisibility visibility) {
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

    public List<String> getImageUrls(ImageCategory category, String referenceId) {
        return imageFileRepository.findByCategoryAndReferenceId(category, referenceId).stream()
                .map(ImageFile::getStoredPath)
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


    public void markAsConfirmed(List<Long> imageIds) {
        List<ImageFile> images = imageFileRepository.findAllById(imageIds);

        images.forEach(ImageFile::confirm);
    }

    public void markAsPendingDelete(List<Long> imageIds) {
        List<ImageFile> images = imageFileRepository.findAllById(imageIds);
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
