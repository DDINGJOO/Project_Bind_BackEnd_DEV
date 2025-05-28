package bind.image.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageUploadResponse {
    private Long id;
    private String url;
    private String thumbnailUrl;
}
