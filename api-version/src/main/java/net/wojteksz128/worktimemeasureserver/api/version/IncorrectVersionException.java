package net.wojteksz128.worktimemeasureserver.api.version;

public class IncorrectVersionException extends IllegalArgumentException {

    public IncorrectVersionException(String s) {
        super(s);
    }
}
