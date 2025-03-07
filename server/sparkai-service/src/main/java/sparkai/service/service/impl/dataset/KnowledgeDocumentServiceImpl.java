package sparkai.service.service.impl.dataset;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sparkai.common.exception.BusinessException;
import sparkai.service.fileSplitter.FileHandleFactory;
import sparkai.service.fileSplitter.FileHandleInterface;
import sparkai.service.service.interfaces.dataset.IKnowledgeDocumentService;
import sparkai.service.vo.document.DocumentItemVo;
import sparkai.service.vo.document.DocumentSplitVo;
import sparkai.service.vo.document.PreviewVo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class KnowledgeDocumentServiceImpl implements IKnowledgeDocumentService{

    /**
     * 预览文件
     * @param previewVo PreviewVo
     * @return List<DocumentSplitVo>
     */
    @Override
    public List<DocumentSplitVo> previewFile(PreviewVo previewVo) {

        try {

            List<DocumentSplitVo> splitList = new LinkedList<>();
            for (MultipartFile file : previewVo.getFiles()) {

                DocumentSplitVo vo = new DocumentSplitVo();
                // 文本标题
                String originalFilename = file.getOriginalFilename();
                vo.setName(originalFilename);

                byte[] bytes = file.getBytes(); // 获取文件的字节数组
                FileHandleFactory fileHandleFactory = new FileHandleFactory();

                // 选择文件处理器
                String ext = originalFilename.split("\\.")[1];
                FileHandleInterface fileHandle = fileHandleFactory.getSplitter(ext);
                List<DocumentItemVo> itemListVo = fileHandle.handle(bytes, previewVo);

                vo.setContent(itemListVo);
                splitList.add(vo);
            }

            return splitList;

        } catch (IllegalStateException | IOException e) {
            throw new BusinessException("上传失败" + e.getMessage());
        }
    }
}
