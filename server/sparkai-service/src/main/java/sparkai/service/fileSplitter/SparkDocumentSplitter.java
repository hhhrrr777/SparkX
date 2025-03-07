package sparkai.service.fileSplitter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;
import sparkai.common.utils.Tool;
import sparkai.service.vo.document.DocumentItemVo;
import sparkai.service.vo.document.PreviewVo;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

public class SparkDocumentSplitter {

    /**
     * 执行文本拆分
     * @param parser DocumentParser
     * @param inputStream InputStream
     * @param previewVo PreviewVo
     * @return List<DocumentItemVo>
     */
    public static List<DocumentItemVo> splitter(DocumentParser parser, InputStream inputStream, PreviewVo previewVo) {

        Document document = parser.parse(inputStream);

        // 512个字符 10个重合度拆分文本
        DocumentSplitter splitter = new DocumentByParagraphSplitter(previewVo.getSplitLen(), 10);
        List<TextSegment> segments = splitter.split(document);

        List<DocumentItemVo> itemListVo = new LinkedList<>();
        segments.forEach(segment -> {
            DocumentItemVo itemVo = new DocumentItemVo();
            itemVo.setTitle("");

            // 自动清理
            String content = segment.text();
            if (previewVo.getAutoClean().equals(1)) {
                content = Tool.cleanText(content);
            }
            itemVo.setContent(content);

            itemListVo.add(itemVo);
        });

        return itemListVo;
    }
}
