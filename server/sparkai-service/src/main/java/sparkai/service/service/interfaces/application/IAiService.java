package sparkai.service.service.interfaces.application;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface IAiService {

    /**
     * 不带角色设定的流式输出
     * @param userMessage String
     * @return TokenStream
     */
    TokenStream chatInTokenStream(String userMessage);

    /**
     * 带角色设定的流式输出
     * @param systemMessage String
     * @param userMessage String
     * @return TokenStream
     */
    @SystemMessage("{{message}}")
    TokenStream chatWithSystem(@V("message") String systemMessage, @UserMessage String userMessage);
}