package sparkai.service.service.impl.home;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparkai.common.exception.BusinessException;
import sparkai.service.service.interfaces.home.IHomeService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class HomeServiceImpl implements IHomeService {

    @Value("${upload.upload-path}/")
    private String uploadPath;

    // 定义允许的文件后缀
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(
            Arrays.asList("png", "jpg", "jpeg", "gif", "webp")
    );

    /**
     * 上传图片
     * @param file MultipartFile
     * @return String
     */
    @Override
    public String uploadImage(MultipartFile file) {

        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 获取文件名并提取后缀
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException("文件名无效");
        }

        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

        // 校验文件后缀
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new BusinessException("只支持 png,jpg,jpeg,gif,webp");
        }

        try {

            String path = uploadPath + "image/" + DateUtil.today().replace("-", "") + "/";
            Path imagePath = Paths.get(path);
            if (!Files.exists(imagePath)) {
                Files.createDirectories(imagePath);
            }

            String newFileName = IdUtil.fastSimpleUUID() + "." + fileExtension;
            Path targetLocation = imagePath.resolve(newFileName);
            Files.copy(file.getInputStream(), targetLocation);

            return path.substring(1) + newFileName;
        } catch (IOException ex) {
            throw new BusinessException("上传失败" + ex.getMessage());
        }
    }
}
