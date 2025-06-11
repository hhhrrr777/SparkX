// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.vo.workflow;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class NodeRuntimeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 运行时ID
     */
    private long runtimeId;

    /**
     * 来源id
     */
    private String sourceId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 边信息
     */
    private Map<String, List<EdgeVo>> edges;

    /**
     * 节点信息
     */
    private NodeVo nodeInfo;
}