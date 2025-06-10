package bind.image.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageUploadResponse {
    private String referenceId;
    private String url;
    private String thumbnailUrl;
}
