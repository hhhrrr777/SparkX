package sparkai.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TextSplitter {

    private final List<String> delimiters;

    public TextSplitter(List<String> delimiters) {
        this.delimiters = delimiters;
    }

    /**
     * 文本按照长度
     * @param text String
     * @param maxLength int
     * @return List<String>
     */
    public List<String> splitText(String text, int maxLength) {
        List<String> result = new ArrayList<>();
        if (text.isEmpty()) {
            return result;
        }

        if (text.length() <= maxLength) {
            result.add(text);
            return result;
        }

        for (String delimiter : delimiters) {
            String regex = Pattern.quote(delimiter);
            String[] parts = text.split(regex);
            if (parts.length > 1) {
                for (String part : parts) {
                    if (!part.isEmpty()) {
                        result.addAll(splitText(part, maxLength));
                    }
                }
                return result;
            }
        }

        String first = text.substring(0, maxLength);
        String remaining = text.substring(maxLength);
        result.add(first);
        result.addAll(splitText(remaining, maxLength));
        return result;
    }
}
