package sparkai.service.service.impl.dataset;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparkai.common.exception.BusinessException;
import sparkai.service.service.interfaces.dataset.IDocumentService;
import sparkai.service.vo.document.DocumentItemVo;
import sparkai.service.vo.document.DocumentSplitVo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

@Service
public class DocumentServiceImpl implements IDocumentService {

    @Override
    public List<DocumentSplitVo> uploadFile(MultipartFile[] files) {

        try {

            List<DocumentSplitVo> splitList = new LinkedList<>();
            for (MultipartFile file : files) {

                DocumentSplitVo vo = new DocumentSplitVo();
                // 文本标题
                String originalFilename = file.getOriginalFilename();
                vo.setName(originalFilename);

                byte[] bytes = file.getBytes(); // 获取文件的字节数组
                InputStream inputStream = new ByteArrayInputStream(bytes);

                // 解析文本
                TextDocumentParser parser = new TextDocumentParser();
                Document document = parser.parse(inputStream);

                // 500个字符 10个重合度拆分文本
                DocumentSplitter splitter = new DocumentByParagraphSplitter(500, 10);
                List<TextSegment> segments = splitter.split(document);

                List<DocumentItemVo> itemListVo = new LinkedList<>();
                segments.forEach(segment -> {
                    DocumentItemVo itemVo = new DocumentItemVo();
                    itemVo.setTitle("");
                    itemVo.setContent(segment.text());

                    itemListVo.add(itemVo);
                });
                vo.setContent(itemListVo);

                splitList.add(vo);
            }

            return splitList;

        } catch (IllegalStateException | IOException e) {
            throw new BusinessException("上传失败" + e.getMessage());
        }
    }
}
