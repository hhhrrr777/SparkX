package sparkai.service.fileSplitter.handle;

import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import sparkai.service.fileSplitter.FileHandleInterface;
import sparkai.service.fileSplitter.SparkDocumentSplitter;
import sparkai.service.vo.document.DocumentItemVo;
import sparkai.service.vo.document.PreviewVo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class PdfHandle implements FileHandleInterface {

    @Override
    public List<DocumentItemVo> handle(byte[] bytes, PreviewVo previewVo) {

        InputStream inputStream = new ByteArrayInputStream(bytes);
        DocumentParser parser = new ApachePdfBoxDocumentParser();

        return SparkDocumentSplitter.splitter(parser, inputStream, previewVo);
    }
}
