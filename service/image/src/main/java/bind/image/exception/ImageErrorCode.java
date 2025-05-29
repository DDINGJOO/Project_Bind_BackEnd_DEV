package bind.image.exception;

import exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {
    IMAGE_NOT_FOUND(404, "Image not found", "IMAGE_NOT_FOUND"),
    INVALID_IMAGE_FORMAT(400, "Invalid image format", "INVALID_IMAGE_FORMAT"),
    IMAGE_TOO_LARGE(413, "Image too large", "IMAGE_TOO_LARGE"),
    NSFW_DETECTION_FAILED(500, "NSFW detection failed", "NSFW_DETECTION_FAILED"),
    IMAGE_ALREADY_EXISTS(409, "Image already exists", "IMAGE_ALREADY_EXISTS"),
    IMAGE_DELETION_FAILED(500, "Image deletion failed", "IMAGE_DELETION_FAILED"),
    IMAGE_UPDATE_FAILED(500, "Image update failed", "IMAGE_UPDATE_FAILED"),
    IMAGE_RETRIEVAL_FAILED(500, "Image retrieval failed", "IMAGE_RETRIEVAL_FAILED"),
    IMAGE_CONFIRMATION_FAILED(500, "Image confirmation failed", "IMAGE_CONFIRMATION_FAILED"),
    IMAGE_DOWNLOAD_FAILED(500, "Image download failed", "IMAGE_DOWNLOAD_FAILED"),
    IMAGE_UPLOAD_FAILED(500, "Image upload failed", "IMAGE_UPLOAD_FAILED"),
    IMAGE_PROCESSING_FAILED(500, "Image processing failed", "IMAGE_PROCESSING_FAILED"),
    IMAGE_NOT_TEMP(404, "Image is not temporary", "IMAGE_NOT_TEMP"),
    IMAGE_ALREADY_PENDING_DELETE(409, "Image is already pending deletion", "IMAGE_ALREADY_PENDING_DELETE"),;

    private final int status;
    private final String message;
    private final String code;
}
