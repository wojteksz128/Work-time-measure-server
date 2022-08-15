package net.wojteksz128.worktimemeasureserver.api.version;

public class IllegalVersionFormatException extends IllegalArgumentException {

    public IllegalVersionFormatException(String s) {
        super(s);
    }
}
