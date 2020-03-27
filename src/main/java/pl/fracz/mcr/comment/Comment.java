package pl.fracz.mcr.comment;

import pl.fracz.mcr.source.Line;

import java.io.File;

public class Comment extends AbstractComment {
    private String text;

    private File file;

    public Comment(AbstractComment.Type type, Line line) {
        super(type, line.get());
    }

    public void setText(String text) {
        checkValidType(AbstractComment.Type.TEXT);
        this.text = text;
    }

    public String getText() {
        checkValidType(AbstractComment.Type.TEXT);
        return text;
    }

    public void setFile(File file) {
        checkValidType(AbstractComment.Type.VOICE);
        this.file = file;
    }

    public File getFile() {
        checkValidType(AbstractComment.Type.VOICE);
        return file;
    }

    private void checkValidType(AbstractComment.Type type) {
        if (type != this.getType()) {
            throw new IllegalArgumentException("The " + type + " comment is required to set this attribute");
        }
    }
}
