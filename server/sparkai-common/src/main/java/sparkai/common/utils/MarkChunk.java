package sparkai.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * embedding拆分文本
 */
public class MarkChunk implements IChunkInterface {
    private static final int MAX_CHUNK_LEN = 256;
    private static final String SPLIT_CHUNK_PATTERN = String.format(".{1,%d}[。| |\\\\.|！|;|；|!|\\n]", MAX_CHUNK_LEN);
    private static final String MAX_CHUNK_PATTERN = String.format(".{1,%d}", MAX_CHUNK_LEN);

    @Override
    public List<String> handle(List<String> chunkList) {
        List<String> result = new ArrayList<>();
        Pattern splitPattern = Pattern.compile(SPLIT_CHUNK_PATTERN, Pattern.DOTALL);
        Pattern maxPattern = Pattern.compile(MAX_CHUNK_PATTERN, Pattern.DOTALL);

        for (String chunk : chunkList) {
            // 处理通过split_chunk_pattern匹配的部分
            List<String> chunkResults = new ArrayList<>();
            Matcher splitMatcher = splitPattern.matcher(chunk);
            while (splitMatcher.find()) {
                String matched = splitMatcher.group();
                if (!matched.trim().isEmpty()) {
                    chunkResults.add(matched.trim());
                }
            }
            result.addAll(chunkResults);

            // 处理剩余未匹配部分
            String[] otherChunks = splitPattern.split(chunk);
            for (String otherChunk : otherChunks) {
                if (otherChunk.isEmpty()) continue;

                if (otherChunk.length() < MAX_CHUNK_LEN) {
                    String trimmed = otherChunk.trim();
                    if (!trimmed.isEmpty()) {
                        result.add(trimmed);
                    }
                } else {
                    Matcher maxMatcher = maxPattern.matcher(otherChunk);
                    while (maxMatcher.find()) {
                        String matched = maxMatcher.group();
                        if (!matched.trim().isEmpty()) {
                            result.add(matched.trim());
                        }
                    }
                }
            }
        }

        return result;
    }
}
