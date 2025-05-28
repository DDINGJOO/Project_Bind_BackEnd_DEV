package bind.image.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Service("localImageStorage")
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
            return relativePath;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    @Override
    public void delete(String relativePath) {
        File file = new File(baseDir + relativePath);
        if (file.exists()) file.delete();
    }
}
