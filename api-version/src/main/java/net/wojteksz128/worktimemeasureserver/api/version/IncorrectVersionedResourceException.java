package net.wojteksz128.worktimemeasureserver.api.version;

public class IncorrectVersionedResourceException extends IllegalStateException {

    public IncorrectVersionedResourceException(String s) {
        super(s);
    }
}
