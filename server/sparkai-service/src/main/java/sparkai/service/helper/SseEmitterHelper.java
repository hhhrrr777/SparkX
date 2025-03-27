package sparkai.service.helper;

import cn.hutool.core.date.TimeInterval;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sparkai.common.constant.SparkAIConstant;
import sparkai.service.vo.application.SseChatResVo;

import java.io.IOException;

@Slf4j
@Component
public class SseEmitterHelper {

    /**
     * 异步发送给客户端
     * @param tokenStream TokenStream
     * @param emitter SseEmitter
     */
    @Async
    public void asyncSend2Client(TokenStream tokenStream, SseEmitter emitter) {

        // 消息开始
        sendStartSse(emitter);

        final TimeInterval timer = new TimeInterval();
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
                        sendErrorSse(emitter);
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

    /**
     * 发送sse开始信号
     * @param sseEmitter SseEmitter
     */
    private void sendStartSse(SseEmitter sseEmitter) {

        try {

            sseEmitter.send(SseEmitter.event().name(SparkAIConstant.SSEEventName.START));
        } catch (IOException e) {
            log.error("startSse error", e);
            sseEmitter.completeWithError(e);
        }
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
     * 发送sse错误信号
     * @param sseEmitter SseEmitter
     */
    private void sendErrorSse(SseEmitter sseEmitter) {

        try {

            sseEmitter.send(SseEmitter.event().name(SparkAIConstant.SSEEventName.ERROR));
        } catch (IOException e) {
            log.error("startSse error", e);
            sseEmitter.completeWithError(e);
        }
    }
}