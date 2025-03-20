package sparkai.service.service.interfaces.application;

import dev.langchain4j.service.TokenStream;

public interface IAiService {

    TokenStream chatInTokenStream(String userMessage);
}