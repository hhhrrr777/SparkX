// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
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
