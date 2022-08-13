package net.wojteksz128.worktimemeasureserver.api.version;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VersionRange that = (VersionRange) o;

        if (!Objects.equals(from, that.from)) return false;
        return Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        int result = from != null ? from.hashCode() : 0;
        result = 31 * result + (to != null ? to.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("range[%s-%s]", from, to);
    }
}
