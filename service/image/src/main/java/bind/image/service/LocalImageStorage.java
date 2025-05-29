package bind.image.service;

import bind.image.exception.ImageErrorCode;
import bind.image.exception.ImageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
public class LocalImageStorage implements ImageStorage {

    @Value("${image.upload.dir}")
    private String baseDir;

    @Override
    public String store(MultipartFile file, String relativePath) {
        try {
            File target = new File(baseDir + relativePath);
            target.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(target)) {
                fos.write(file.getBytes());
            }

        } catch (IOException e) {
                throw  new ImageException(ImageErrorCode.IMAGE_DOWNLOAD_FAILED);
        }
        return relativePath;
    }

    @Override
    public void delete(String relativePath) {
        File file = new File(baseDir + relativePath);
        if (file.exists()) file.delete();
    }
}
