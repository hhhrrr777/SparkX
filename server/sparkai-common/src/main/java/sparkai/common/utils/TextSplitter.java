package sparkai.common.utils;

import java.util.*;
import java.util.regex.*;

public class TextSplitter {

    public static class StepRegex {
        Pattern pattern;
        int maxLen;

        StepRegex(Pattern pattern, int maxLen) {
            this.pattern = pattern;
            this.maxLen = maxLen;
        }
    }

    // 新增的Chunk类，包含标题和内容
    public static class Chunk {
        public final String title;
        public final String content;

        public Chunk(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

    public static class SplitResult {
        List<Chunk> chunks;
        int chars;

        SplitResult(List<Chunk> chunks, int chars) {
            this.chunks = chunks;
            this.chars = chars;
        }
    }

    public static class TextPart {
        String text;
        String title;

        TextPart(String text, String title) {
            this.text = text;
            this.title = title;
        }
    }

    public static SplitResult splitText2Chunks(String text, int chunkLen, double overlapRatio, List<String> customReg) {
        final String splitMarker = "SPARK_SPLIT";
        final String codeBlockMarker = "SPARK_CODE_BLOCK_LINE_MARKER";
        int overlapLen = (int) Math.round(chunkLen * overlapRatio);

        text = processCodeBlocks(text, codeBlockMarker);
        List<StepRegex> stepRegexes = buildStepRegexes(chunkLen, customReg);

        try {
            List<Chunk> chunks = splitTextRecursively(
                    text, 0, "", "",
                    chunkLen, overlapLen,
                    stepRegexes, customReg.size(), splitMarker, codeBlockMarker
            );

            // 使用迭代器恢复代码块换行符
            ListIterator<Chunk> iterator = chunks.listIterator();
            while (iterator.hasNext()) {
                Chunk chunk = iterator.next();
                String newContent = chunk.content.replace(codeBlockMarker, "\n");
                String title = chunk.title.replace("#", "").replace(" ", "");
                if (title.length() > 255) {
                    title = title.substring(0, 255);
                }
                iterator.set(new Chunk(title, newContent));
            }

            int totalChars = chunks.stream().mapToInt(c -> c.content.length()).sum();
            return new SplitResult(chunks, totalChars);
        } catch (Exception e) {
            throw new RuntimeException("Text split error", e);
        }
    }

    private static String processCodeBlocks(String text, String marker) {
        Pattern codeBlockPattern = Pattern.compile("(```[\\s\\S]*?```|~~~[\\s\\S]*?~~~)");
        Matcher matcher = codeBlockPattern.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String replaced = matcher.group(0).replaceAll("\n", marker);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replaced));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private static List<StepRegex> buildStepRegexes(int chunkLen, List<String> customReg) {
        List<StepRegex> stepRegexes = new ArrayList<>();

        // 添加自定义正则表达式
        for (String reg : customReg) {
            String escaped = Pattern.quote(reg);
            stepRegexes.add(new StepRegex(
                    Pattern.compile("(" + escaped + ")"),
                    (int) (chunkLen * 1.4)
            ));
        }

        // 添加内置正则表达式
        stepRegexes.addAll(Arrays.asList(
                new StepRegex(Pattern.compile("^(#\\s[^\\n]+)\\n", Pattern.MULTILINE), (int) (chunkLen * 1.2)),
                new StepRegex(Pattern.compile("^(##\\s[^\\n]+)\\n", Pattern.MULTILINE), (int) (chunkLen * 1.2)),
                new StepRegex(Pattern.compile("^(###\\s[^\\n]+)\\n", Pattern.MULTILINE), (int) (chunkLen * 1.2)),
                new StepRegex(Pattern.compile("^(####\\s[^\\n]+)\\n", Pattern.MULTILINE), (int) (chunkLen * 1.2)),
                new StepRegex(Pattern.compile("([\\n]([`~]))"), (int) (chunkLen * 4)),
                new StepRegex(Pattern.compile("([\\n](?!\\s*[*\\-|>0-9]))"), (int) (chunkLen * 2)),
                new StepRegex(Pattern.compile("([\\n])"), (int) (chunkLen * 1.2)),
                new StepRegex(Pattern.compile("([。]|([a-zA-Z])\\.\\s)"), (int) (chunkLen * 1.2)),
                new StepRegex(Pattern.compile("([！]|!\\s)"), (int) (chunkLen * 1.2)),
                new StepRegex(Pattern.compile("([？]|\\?\\s)"), (int) (chunkLen * 1.4)),
                new StepRegex(Pattern.compile("([；]|;\\s)"), (int) (chunkLen * 1.6)),
                new StepRegex(Pattern.compile("([，]|,\\s)"), (int) (chunkLen * 2))
        ));

        return stepRegexes;
    }

    private static List<TextPart> splitTextParts(String text, int step, List<StepRegex> stepRegexes,
                                                 int customRegLen, String splitMarker) {
        if (step >= stepRegexes.size()) {
            return Collections.singletonList(new TextPart(text, ""));
        }

        StepRegex stepRegex = stepRegexes.get(step);
        boolean isCustomStep = step < customRegLen;
        boolean independentChunk = step >= customRegLen && step <= 3 + customRegLen;

        String replacement;
        if (isCustomStep) {
            replacement = splitMarker;
        } else if (independentChunk) {
            replacement = splitMarker + "$1";
        } else {
            replacement = "$1" + splitMarker;
        }

        String[] parts = stepRegex.pattern.matcher(text)
                .replaceAll(replacement)
                .split(splitMarker);

        List<TextPart> textParts = new ArrayList<>();
        for (String part : parts) {
            if (part.trim().isEmpty()) continue;

            String title = "";
            if (step >= customRegLen && step <= 3 + customRegLen) {
                Matcher titleMatcher = stepRegex.pattern.matcher(part);
                if (titleMatcher.find()) {
                    title = titleMatcher.group(1);
                }
            }

            String cleanedText = part.replace(title, "").trim();
            if (!cleanedText.isEmpty()) {
                textParts.add(new TextPart(cleanedText, title));
            }
        }

        return textParts;
    }

    private static List<Chunk> splitTextRecursively(String text, int step, String lastText, String mdTitle,
                                                    int chunkLen, int overlapLen,
                                                    List<StepRegex> stepRegexes, int customRegLen,
                                                    String splitMarker, String codeBlockMarker) {
        List<Chunk> chunks = new ArrayList<>();

        if (step >= stepRegexes.size()) {
            // 最终分割处理
            List<Chunk> finalChunks = new ArrayList<>();
            for (int i = 0; i < text.length(); i += chunkLen - overlapLen) {
                int end = Math.min(i + chunkLen, text.length());
                finalChunks.add(new Chunk(mdTitle, text.substring(i, end)));
            }
            return finalChunks;
        }

        List<TextPart> splitParts = splitTextParts(text, step, stepRegexes, customRegLen, splitMarker);
        StepRegex currentStep = stepRegexes.get(step);

        String currentLastText = lastText;
        for (TextPart part : splitParts) {
            String newText = currentLastText + part.text;
            int newLength = newText.length();

            if (newLength > currentStep.maxLen) {
                if (currentLastText.length() > chunkLen * 0.7) {
                    // 添加带有标题的新块
                    chunks.add(new Chunk(
                            mdTitle + part.title,
                            currentLastText
                    ));
                    currentLastText = getOverlapText(currentLastText, step, chunkLen, overlapLen, stepRegexes, splitMarker);
                } else {
                    List<Chunk> innerChunks = splitTextRecursively(
                            newText, step + 1, "", mdTitle + part.title,
                            chunkLen, overlapLen, stepRegexes, customRegLen, splitMarker, codeBlockMarker
                    );

                    if (!innerChunks.isEmpty()) {
                        chunks.addAll(innerChunks.subList(0, innerChunks.size() - 1));
                        currentLastText = innerChunks.get(innerChunks.size() - 1).content;
                    }
                }
            } else {
                currentLastText = newText;
                if (shouldSplitCurrentStep(step, customRegLen, currentStep, newLength)) {
                    chunks.add(new Chunk(
                            mdTitle + part.title,
                            currentLastText
                    ));
                    currentLastText = getOverlapText(currentLastText, step, chunkLen, overlapLen, stepRegexes, splitMarker);
                }
            }
        }

        if (!currentLastText.isEmpty() && !chunks.isEmpty()) {
            Chunk lastChunk = chunks.get(chunks.size() - 1);
            if (currentLastText.length() < chunkLen * 0.4) {
                chunks.set(chunks.size() - 1, new Chunk(
                        lastChunk.title,
                        lastChunk.content + currentLastText
                ));
            } else {
                chunks.add(new Chunk(mdTitle, currentLastText));
            }
        } else if (chunks.isEmpty()) {
            chunks.add(new Chunk(mdTitle, currentLastText));
        }

        return chunks;
    }

    private static List<String> handleFinalSplit(String text, String mdTitle, int chunkLen, int overlapLen) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += chunkLen - overlapLen) {
            int end = Math.min(i + chunkLen, text.length());
            chunks.add(mdTitle + text.substring(i, end));
        }
        return chunks;
    }

    private static String getOverlapText(String text, int step, int chunkLen, int overlapLen,
                                         List<StepRegex> stepRegexes, String splitMarker) {
        if (overlapLen == 0 || step >= stepRegexes.size()) return "";

        List<TextPart> parts = splitTextParts(text, step, stepRegexes, 0, splitMarker);
        StringBuilder overlap = new StringBuilder();

        for (int i = parts.size() - 1; i >= 0; i--) {
            String newText = parts.get(i).text + overlap;
            if (newText.length() > overlapLen) {
                return newText.substring(0, overlapLen);
            }
            overlap.insert(0, parts.get(i).text);
        }
        return overlap.toString();
    }

    private static boolean shouldSplitCurrentStep(int step, int customRegLen, StepRegex currentStep, int newLength) {
        return step < customRegLen || step <= 4 + customRegLen && newLength >= currentStep.maxLen * 0.3;
    }
}