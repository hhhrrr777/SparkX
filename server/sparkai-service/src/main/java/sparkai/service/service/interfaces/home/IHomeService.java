package sparkai.service.service.interfaces.home;

import org.springframework.web.multipart.MultipartFile;

public interface IHomeService {

    /**
     * 上传图片
     * @param file MultipartFile
     * @return String
     */
    String uploadImage(MultipartFile file);
}