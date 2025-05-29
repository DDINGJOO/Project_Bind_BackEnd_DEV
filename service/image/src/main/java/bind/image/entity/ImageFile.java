package bind.image.entity;

import data.enums.image.ImageCategory;
import data.enums.image.ImageStatus;
import data.enums.image.ImageVisibility;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "image_files")
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 파일 이름 (uuid.jpg)
    private String uuidName;

    // 원본 파일 이름
    private String originalName;

    // 저장된 전체 경로 (예: /upload/images/POST/2025/05/28/uuid.jpg)
    private String storedPath;

    // 썸네일 경로 (선택)
    private String thumbnailPath;

    // MIME 타입 (image/jpeg 등)
    private String contentType;

    // 파일 크기 (바이트 단위)
    private Long fileSize;

    // 이미지 카테고리 (PROFILE, POST, BAND_ROOM, BUSINESS_DOC 등)
    @Enumerated(EnumType.STRING)
    private ImageCategory category;

    // 참조 ID (ex. 게시글 ID, 유저 ID 등)
    private String referenceId;

    // 이미지 업로더 (누가 업로드했는지)
    private String uploaderId;

    // 이미지 상태 (TEMP, CONFIRMED, PENDING_DELETE)
    @Enumerated(EnumType.STRING)
    private ImageStatus status;

    // 공개 여부 (PUBLIC or PRIVATE)
    @Enumerated(EnumType.STRING)
    private ImageVisibility visibility;

    // 생성 시간
    private LocalDateTime createdAt;

    // 삭제 예정 시간 (PENDING_DELETE인 경우)
    private LocalDateTime pendingDeleteAt;


    //== 상태 전환 메서드 ==//
    public void markPendingDelete() {
        this.status = ImageStatus.PENDING_DELETE;
        this.pendingDeleteAt = LocalDateTime.now();
    }

    public void confirm() {
        this.status = ImageStatus.CONFIRMED;
    }
}
