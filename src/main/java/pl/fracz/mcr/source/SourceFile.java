package pl.fracz.mcr.source;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import pl.fracz.mcr.comment.CommentNotAddedException;
import pl.fracz.mcr.preferences.ApplicationSettings;
import pl.fracz.mcr.syntax.PrettifyHighlighter;
import pl.fracz.mcr.syntax.SyntaxHighlighter;
import pl.fracz.mcr.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

public class SourceFile {
    private static final SyntaxHighlighter SYNTAX_HIGHLIGHTER = new PrettifyHighlighter();

    private static final Color SELECTED_LINE_COLOR = Color.parseColor("#444444");

    private final View.OnClickListener lineHighlighter = view -> {
        if (selectedLine != null) {
            selectedLine.setBackgroundColor(Color.TRANSPARENT);
        }
        selectedLine = (Line) view;
        selectedLine.setBackgroundColor(SELECTED_LINE_COLOR);
    };

    private final String sourceCode;

    private final String identifier;

    private final String language;

    private Line selectedLine;

    private String highlightedSourceCode;

    public SourceFile(String sourceCode, String language) {
        this.sourceCode = sourceCode;
        this.language = language;
        this.identifier = calculateSHA1SourceChecksum();
    }

    private String calculateSHA1SourceChecksum() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] digest = md.digest(sourceCode.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError();
        }
    }

    public Collection<Line> getLines(Context context) {
        StringTokenizer tokenizer = new StringTokenizer(getHighlightedSourceCode(), "\n");
        Collection<Line> lines = new ArrayList<Line>(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            Line line = new Line(context, this, lines.size() + 1, tokenizer.nextToken());
            line.setOnClickListener(lineHighlighter);
            lines.add(line);
        }
        return lines;
    }

    private String getHighlightedSourceCode() {
        if (highlightedSourceCode == null) {
            highlightedSourceCode = highlightSourceCode();
        }
        return highlightedSourceCode;
    }

    private String highlightSourceCode() {
        String code = replaceTabs();
        if (ApplicationSettings.highlightSources()) {
            return SYNTAX_HIGHLIGHTER.highlight(code, language);
        } else {
            return code;
        }
    }

    private String replaceTabs() {
        StringBuilder tabReplacement = new StringBuilder();
        for (int i = 0; i < ApplicationSettings.getTabSize(); i++) {
            tabReplacement.append(" ");
        }
        return sourceCode.replace("\t", tabReplacement.toString());
    }

    public String getIdentifier() {
        return identifier;
    }

    public void addTextComment(String comment) throws CommentNotAddedException {
        if (selectedLine == null) {
            throw new NoSelectedLineException();
        }
        getSelectedLine().addTextComment(comment);
    }

    public void addVoiceComment(File recordedFile) throws CommentNotAddedException {
        if (selectedLine == null) {
            throw new NoSelectedLineException();
        }
        getSelectedLine().createVoiceComment(recordedFile);
    }

    public Line getSelectedLine() {
        return selectedLine;
    }

    public void markConversation(Line line) {
        // Nothing to do, it's perfect.
    }

    public static SourceFile createFromString(String sourceCode, String language) {
        return new SourceFile(sourceCode, language);
    }

    /**
     * Creates source file based on a file reference.
     */
    public static SourceFile createFromFile(File sourceFile) throws IOException {
        String sourceCode = FileUtils.read(sourceFile);
        return createFromString(sourceCode, FileUtils.getExtension(sourceFile.getName()));
    }
}
