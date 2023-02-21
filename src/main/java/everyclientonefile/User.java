package everyclientonefile;

public class User {
    int filenameLength;

    String filename;

    Long filecontentLength;

    int mark;

    public int getFilenameLength() {
        return filenameLength;
    }

    public void setFilenameLength(int filenameLength) {
        this.filenameLength = filenameLength;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFilecontentLength() {
        return filecontentLength;
    }

    public void setFilecontentLength(Long filecontentLength) {
        this.filecontentLength = filecontentLength;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }
}
