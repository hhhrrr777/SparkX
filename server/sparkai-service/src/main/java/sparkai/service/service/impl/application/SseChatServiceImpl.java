package sparkai.service.service.impl.application;

import cn.hutool.core.date.TimeInterval;
import dev.langchain4j.community.model.qianfan.QianfanStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.constant.SparkAIConstant;
import sparkai.service.service.interfaces.application.IAiService;
import sparkai.service.service.interfaces.application.ISseChatService;
import sparkai.service.vo.application.SseChatResVo;
import sparkai.service.vo.application.SseChatVo;

import java.io.IOException;

@Slf4j
@Service
public class SseChatServiceImpl implements ISseChatService {

    /**
     * 流式与ai交互
     * @param chatVo SseChatVo
     * @return SseEmitter
     */
    @Override
    public SseEmitter sseChat(SseChatVo chatVo) {

        SseEmitter emitter = new SseEmitter();
        // 执行异步发送
        this.asyncSend2Client(chatVo, emitter);

        return emitter;
    }

    /**
     * 发送sse结束信号
     * @param sseEmitter SseEmitter
     */
    private void sendEndSse(SseEmitter sseEmitter, SseChatResVo resVo) {

        try {

            sseEmitter.send(SseEmitter.event().name(SparkAIConstant.SSEEventName.DONE)
                    .data(" " + SparkAIConstant.SSEEventName.META + resVo.toString()));
        } catch (IOException e) {
            log.error("startSse error", e);
            sseEmitter.completeWithError(e);
        }
    }

    /**
     * 异步发送给客户端
     * @param chatVo SseChatVo
     * @param emitter SseEmitter
     */
    @Async
    public void asyncSend2Client(SseChatVo chatVo, SseEmitter emitter) {

        String word = chatVo.getContent();

        QianfanStreamingChatModel model = QianfanStreamingChatModel.builder()
                .apiKey("DYATIgV0vT2W118kz2spXAj3")
                .secretKey("NEVr9XhWa0T8WB3e9INUwYgjPUEXiFas")
                .modelName("ERNIE-Speed-128K")
                .build();

        IAiService assistant = AiServices.create(IAiService.class, model);

        final TimeInterval timer = new TimeInterval();
        TokenStream tokenStream = assistant.chatInTokenStream(word);
        tokenStream.onPartialResponse((content) -> {
            // 加空格配合前端的fetchEventSource进行解析，
            // 见https://github.com/Azure/fetch-event-source/blob/45ac3cfffd30b05b79fbf95c21e67d4ef59aa56a/src/parse.ts#L129-L133
            try {

                String[] lines = content.split("[\\r\\n]", -1);
                if (lines.length > 1) {
                    emitter.send(" " + lines[0]);
                    for (int i = 1; i < lines.length; i++) {
                        /**
                         * 当响应结果的content中包含有多行文本时，
                         * 前端的fetch-event-source框架的BUG会将包含有换行符的那一行内容替换为空字符串，
                         * 故需要先将换行符与后面的内容拆分并转成，前端碰到换行标志时转成换行符处理
                         */
                        emitter.send("-_-_wrap_-_-");
                        emitter.send(" " + lines[i]);
                    }
                } else {
                    emitter.send(" " + content);
                }
            } catch (IOException e) {
                log.error("拆解AI返回信息失败：", e);
            }
        })
        .onCompleteResponse((response) -> {
            System.out.println(response);
            // 输入的token
            int inputTokenCount = response.tokenUsage().totalTokenCount();
            // 输出的token
            int outputTokenCount = response.tokenUsage().outputTokenCount();
            // 输出的报文
            String content = response.aiMessage().text();

            // 计算耗时
            long second = timer.intervalSecond();

            // 发送结束信号
            SseChatResVo resVo = new SseChatResVo();
            resVo.setTokens(inputTokenCount + outputTokenCount);
            resVo.setContent(content);
            resVo.setUseTime(second);
            sendEndSse(emitter, resVo);

            // 关闭sse
            emitter.complete();
        })
        .onError(Throwable::printStackTrace)
        .start();
    }
}