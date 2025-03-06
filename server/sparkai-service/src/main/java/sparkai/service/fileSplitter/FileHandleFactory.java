package sparkai.service.fileSplitter;

import sparkai.service.fileSplitter.handle.DefaultHandle;
import sparkai.service.fileSplitter.handle.MarkdownHandle;
import sparkai.service.fileSplitter.handle.OfficeHandle;
import sparkai.service.fileSplitter.handle.PdfHandle;

public class FileHandleFactory {

    /**
     * 根据文件选择文件处理器
     * @param ext String
     * @return FileSplitterInterface
     */
    public FileHandleInterface getSplitter(String ext) {

        switch (ext) {
            case "md":
                return new MarkdownHandle();
            case "pdf":
                return new PdfHandle();
            case "xls":
            case "xlsx":
            case "docx":
            case "csv":
                return new OfficeHandle();
            case "html":
        }

        return new DefaultHandle();
    }
}
