package sparkai.common.utils;

import com.vladsch.flexmark.util.ast.Node;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class MarkdownSplitter {

    @Data
    public static class Section {
        private String title;
        private String content;
    }

    public static List<Section> parseMarkdown(String markdown) {
        Parser parser = Parser.builder().build();
        Document document = (Document) parser.parse(markdown);
        List<Section> sections = new ArrayList<>();
        Node currentNode = document.getFirstChild();
        StringBuilder currentContent = new StringBuilder();
        Section currentSection = null;

        while (currentNode != null) {
            if (currentNode instanceof Heading) {
                // 遇到新标题时保存前一个章节
                if (currentSection != null) {
                    currentSection.setContent(currentContent.toString().trim());
                    sections.add(currentSection);
                }
                // 创建新章节
                currentSection = new Section();
                currentSection.setTitle(((Heading) currentNode).getText().toString());
                currentContent = new StringBuilder();
            } else if (currentSection != null) {
                // 收集非标题内容
                currentContent.append(BasedSequence.of(currentNode.getChars())).append("\n");
            }
            currentNode = currentNode.getNext();
        }
        // 处理最后一个章节
        if (currentSection != null) {
            currentSection.setContent(currentContent.toString().trim());
            sections.add(currentSection);
        }
        return sections;
    }
}