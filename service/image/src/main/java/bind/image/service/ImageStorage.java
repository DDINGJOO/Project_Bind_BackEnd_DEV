package bind.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {
    String store(MultipartFile file, String relativePath);
    void delete(String relativePath);
}
