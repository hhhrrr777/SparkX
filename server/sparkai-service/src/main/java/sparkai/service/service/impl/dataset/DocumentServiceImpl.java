package sparkai.service.service.impl.dataset;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import sparkai.common.exception.BusinessException;
import sparkai.service.service.interfaces.dataset.IDocumentService;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements IDocumentService {

    @Override
    public List<String> uploadFile(MultipartFile[] files) {
        for (MultipartFile file : files) {
            System.out.println("---------------------");
            System.out.println(file.getOriginalFilename());
            System.out.println("---------------------");
        }
        return null;
        /*try {

            // TODO 校验文件大小

            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            // 获取文件后缀名
            String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 设置文件上传绝对路径
            String filePath = ResourceUtils.getFile("classpath:").getAbsolutePath() + "uploadFiles/";
            System.out.println(ResourceUtils.getFile("classpath:").getAbsolutePath() );
            // 获取UUID名称
            String fileName = UUID.randomUUID() + suffixName;

            // 获取上传文件的File对象
            File dest = new File(filePath + fileName);

            // 开始上传
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }

            file.transferTo(dest);

            // 转换成文档对象
            String path = filePath + fileName;
            Document document = FileSystemDocumentLoader.loadDocument(path, new TextDocumentParser());

            // postgresql不支持\u0000
            // String content = document.text().replace("\u0000", "");
            DocumentSplitter splitter = new DocumentByParagraphSplitter(500, 10);
            List<TextSegment> segments = splitter.split(document);

            List<String> res = new LinkedList<>();
            segments.forEach(segment -> {
                res.add(segment.text());
            });

            return res;

        } catch (IllegalStateException | IOException e) {
            throw new BusinessException("上传失败" + e.getMessage());
        }*/
    }
}
