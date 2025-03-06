package sparkai.service.fileSplitter;

import sparkai.service.vo.document.DocumentItemVo;
import sparkai.service.vo.document.PreviewVo;

import java.util.List;

public interface FileHandleInterface {

    List<DocumentItemVo> handle(byte[] bytes, PreviewVo previewVo);
}
