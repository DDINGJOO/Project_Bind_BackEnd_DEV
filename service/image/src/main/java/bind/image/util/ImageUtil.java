package bind.image.util;

import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtil {
    static {
        ImageIO.scanForPlugins(); // 플러그인 자동 등록 (생략해도 되는 경우도 많음)
    }

    public static byte[] toWebp(MultipartFile file, float quality) throws IOException {
        BufferedImage input = ImageIO.read(file.getInputStream());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 품질 옵션 커스텀하려면 WebPWriteParam 활용
        ImageIO.write(input, "webp", os);
        return os.toByteArray();
    }
}
