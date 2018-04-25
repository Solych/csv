package model;

/**
 * Class for return recorded and skipped lines
 */
public class Lines {

    private int recordedLines;
    private int skippedLines;

    public int getRecordedLines() {
        return recordedLines;
    }

    public int getSkippedLines() {
        return skippedLines;
    }

    public Lines(int recordedLines, int skippedLines) {
        this.recordedLines = recordedLines;
        this.skippedLines = skippedLines;
    }

}
