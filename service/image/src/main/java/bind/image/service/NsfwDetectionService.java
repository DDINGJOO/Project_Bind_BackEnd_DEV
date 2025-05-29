package bind.image.service;

import bind.image.dto.response.NsfwDetectionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NsfwDetectionService {

    @Value("${image.upload.NsfwUrl}")
    private String nsfwUrl;

    private final WebClient webClient = WebClient.builder()
            .baseUrl(nsfwUrl)
            .build();

    public boolean isImageSafe(MultipartFile file)  {
        try
        {
            return Boolean.TRUE.equals(webClient.post()
                    .uri("/api/images/detect")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData("file", new ByteArrayResource(file.getBytes()) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    }))
                    .retrieve()
                    .bodyToMono(NsfwDetectionResult.class)
                    .map(NsfwDetectionResult::isSafe)
                    .block());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
