package net.wojteksz128.worktimemeasureserver.api.version;

public class IncorrectVersionRangeException extends IllegalArgumentException {

    public IncorrectVersionRangeException(String s) {
        super(s);
    }
}
