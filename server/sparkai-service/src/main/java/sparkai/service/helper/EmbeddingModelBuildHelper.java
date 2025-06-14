// +----------------------------------------------------------------------
// | SparkAI 基于大语言模型和 RAG 的知识库问答系统
// +----------------------------------------------------------------------
// | Copyright (c) 2022~2099 http://sparkai.sparkshop.cn All rights reserved.
// +----------------------------------------------------------------------
// | Licensed SparkAI 并不是自由软件，未经许可不能去掉 SparkAI 相关版权
// +----------------------------------------------------------------------
// | Author: NickBai  <1902822973@qq.com>
// +----------------------------------------------------------------------
package sparkai.service.helper;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EmbeddingModelBuildHelper {

    /**
     * 构建向量模型
     * @param modelId String
     * @return EmbeddingModel
     */
    public EmbeddingModel build(String modelId) {
        // 默认内存型的模型
        if (Objects.equals(modelId, "AllMiniLmL6V2Embedding")) {

            return new AllMiniLmL6V2EmbeddingModel();
        }

        return null;
    }
}