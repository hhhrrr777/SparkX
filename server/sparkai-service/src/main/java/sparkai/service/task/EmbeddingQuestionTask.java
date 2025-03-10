package sparkai.service.task;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmbeddingQuestionTask {

    /**
     * 向量化问题
     * @param questionId String
     */
    @Async
    public void executeAsyncTask(String questionId) {

    }
}
