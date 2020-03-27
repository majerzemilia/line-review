package pl.fracz.mcr.comment;

import pl.fracz.mcr.preferences.ApplicationSettings;
import pl.fracz.mcr.source.SourceFile;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AbstractComment {
    private final SimpleDateFormat CREATION_TIME_FORMAT = new SimpleDateFormat("HH:mm dd.MM.yyyy");

    private final Type type;

    private final String author;

    protected SourceFile sourceFile;

    private Date date;

    private int lineNumber;

    public AbstractComment(Type type, int lineNumber) {
        this.type = type;
        this.author = ApplicationSettings.getAuthor();
        this.lineNumber = lineNumber;
        this.date = new Date(System.currentTimeMillis());
    }

    public Type getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getDateFormatted() {
        return CREATION_TIME_FORMAT.format(date);
    }

    public int getLineNumber() {
        return lineNumber;
    }

    protected String getAuthor() {
        return author;
    }

    public static enum Type {
        TEXT, VOICE
    }
}
