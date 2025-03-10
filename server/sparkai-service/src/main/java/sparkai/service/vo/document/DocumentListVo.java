// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.vo.document;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class DocumentListVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文档名称
     */
    private String name;

    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 文件大小
     */
    private long fileSize;

    /**
     * 向量化状态
     */
    private Integer status;

    /**
     * 生成问题状态
     */
    private Integer questionStatus;

    /**
     * 是否启用
     */
    private Integer active;

    /**
     * 扩充信息
     */
    private String statusMeta;

    /**
     * 分段数
     */
    private Integer paragraphNum;

    /**
     * 命中处理方式
     */
    private String hitDealType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
