package bind.image.repository;

import bind.image.entity.ImageFile;
import data.enums.ResourceCategory;
import data.enums.image.ImageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    List<ImageFile> findByCategoryAndReferenceId(ResourceCategory category, String referenceId);
    List<ImageFile> findByStatusAndCreatedAtBefore(ImageStatus status, LocalDateTime cutoff);
    List<ImageFile> findByStatusAndPendingDeleteAtBefore(ImageStatus status, LocalDateTime cutoff);

    Collection<? extends ImageFile> findByStatus(ImageStatus imageStatus);

}
