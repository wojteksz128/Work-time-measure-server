package net.wojteksz128.worktimemeasureserver.api.version;

public class VersionRange {

    private final Version from;
    private final Version to;

    public VersionRange(String from, String to) {
        this.from = new Version(from);
        this.to = new Version(to);
    }

    public boolean includes(String other) {
        Version otherVersion = new Version(other);

        return from.compareTo(otherVersion) <= 0 && to.compareTo(otherVersion) >= 0;
    }

    @Override
    public String toString() {
        return String.format("range[%s-%s]", from, to);
    }
}
