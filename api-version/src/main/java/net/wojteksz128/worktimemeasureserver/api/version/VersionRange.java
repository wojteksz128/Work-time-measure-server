package net.wojteksz128.worktimemeasureserver.api.version;

import org.springframework.util.StringUtils;

import java.util.Objects;

public class VersionRange {

    private final Version from;
    private final Version to;

    public VersionRange(String from, String to) {
        checkVersionRange(from, to);
        this.from = getFromVersion(from);
        this.to = getToVersion(to);
    }

    private static void checkVersionRange(String from, String to) {
        if (!StringUtils.hasText(from) && !StringUtils.hasText(to)) {
            throw new IncorrectVersionRangeException("'from' or 'to' must be specified");
        }

        checkVersionRange(getFromVersion(from), getToVersion(to));
    }

    private static Version getFromVersion(String from) {
        return Version.of(StringUtils.hasText(from) ? from : Version.MIN_VERSION);
    }

    private static Version getToVersion(String to) {
        return Version.of(StringUtils.hasText(to) ? to : Version.MAX_VERSION);
    }

    private static void checkVersionRange(Version from, Version to) {
        if (from.compareTo(to) > 0) {
            throw new IncorrectVersionRangeException("'from' version cannot be greater then 'to' version (from: %s, to: %s)".formatted(from, to));
        }
    }

    public boolean includes(String other) {
        Version otherVersion = Version.of(other);

        return from.compareTo(otherVersion) <= 0 && to.compareTo(otherVersion) >= 0;
    }

    public Version getFrom() {
        return from;
    }

    public Version getTo() {
        return to;
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
