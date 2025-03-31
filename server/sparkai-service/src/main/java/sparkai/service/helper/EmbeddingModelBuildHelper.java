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

        if (Objects.equals(modelId, "AllMiniLmL6V2Embedding")) {

            return new AllMiniLmL6V2EmbeddingModel();
        }

        return null;
    }
}