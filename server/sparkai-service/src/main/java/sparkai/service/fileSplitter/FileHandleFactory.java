package sparkai.service.fileSplitter;

import sparkai.service.fileSplitter.handle.DefaultHandle;
import sparkai.service.fileSplitter.handle.MarkdownHandle;

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
            case "xls":
            case "xlsx":
            case "html":
            case "docx":
            case "csv":
        }

        return new DefaultHandle();
    }
}
