package sparkai.service.service.impl.dataset;

import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import sparkai.common.exception.BusinessException;
import sparkai.service.service.interfaces.dataset.IDocumentService;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements IDocumentService {

    @Override
    public void uploadFile(MultipartFile file) {

        try {

            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀名
            String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 设置文件上传绝对路径
            String filePath = ResourceUtils.getURL("classpath:").getPath() + "uploadFiles/";
            System.out.println(filePath);
            // 获取UUID名称
            String fileName = UUID.randomUUID() + suffixName;

            // 获取上传文件的File对象
            File dest = new File(filePath + fileName);

            // 开始上传
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }

            file.transferTo(dest);
        } catch (IllegalStateException | IOException e) {
            throw new BusinessException("上传失败" + e.getMessage());
        }
    }
}
