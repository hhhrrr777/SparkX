package sparkai.service.fileSplitter.handle;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import sparkai.service.fileSplitter.FileHandleInterface;
import sparkai.service.vo.document.DocumentItemVo;
import sparkai.service.vo.document.PreviewVo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * 默认文本拆分器
 */
public class DefaultHandle implements FileHandleInterface {

    @Override
    public List<DocumentItemVo> handle(byte[] bytes, PreviewVo previewVo) {

        InputStream inputStream = new ByteArrayInputStream(bytes);
        // 解析文本
        TextDocumentParser parser = new TextDocumentParser();
        Document document = parser.parse(inputStream);

        // 512个字符 10个重合度拆分文本
        DocumentSplitter splitter = new DocumentByParagraphSplitter(previewVo.getSplitLen(), 10);
        List<TextSegment> segments = splitter.split(document);

        List<DocumentItemVo> itemListVo = new LinkedList<>();
        segments.forEach(segment -> {
            DocumentItemVo itemVo = new DocumentItemVo();
            itemVo.setTitle("");
            itemVo.setContent(segment.text());

            itemListVo.add(itemVo);
        });

        return itemListVo;
    }
}
