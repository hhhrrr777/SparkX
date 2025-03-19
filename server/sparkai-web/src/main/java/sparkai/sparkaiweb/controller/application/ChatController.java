package sparkai.sparkaiweb.controller.application;

import com.hankcs.hanlp.dependency.nnparser.util.Log;
import dev.langchain4j.community.model.qianfan.QianfanChatModel;
import dev.langchain4j.community.model.qianfan.QianfanStreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RequestMapping("/api/chat")
@RestController
public class ChatController {

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody Map<String, String> request) {

        SseEmitter emitter = new SseEmitter();

        new Thread(() -> {
            try {
                String query = request.get("query");

               QianfanStreamingChatModel model = QianfanStreamingChatModel.builder()
                        .apiKey("DYATIgV0vT2W118kz2spXAj3")
                        .secretKey("NEVr9XhWa0T8WB3e9INUwYgjPUEXiFas")
                        .modelName("ERNIE-Speed-128K") // 一个免费的模型名称
                        .build();

                model.chat(query, new StreamingChatResponseHandler() {

                    @Override
                    public void onPartialResponse(String partialResponse) {

                        try {
                            emitter.send(SseEmitter.event().name("message").data(partialResponse)); // 发送事件和数据
                        } catch (IOException e) {
                            // 处理异常，例如移除失效的emitter等
                            emitter.complete();
                        }
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse completeResponse) {
                        emitter.complete();
                        System.out.println("onCompleteResponse: 结束了");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            } catch (Exception e) {
                Log.ERROR_LOG("error");
            }
        }).start();

        return emitter;
    }
}