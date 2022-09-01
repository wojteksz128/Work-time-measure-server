package net.wojteksz128.worktimemeasureserver.api.version;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class VersionRange implements Serializable {
    private static final String LEFT_SIDE_OPEN_MARK = "(";
    private static final String LEFT_SIDE_CLOSED_MARK = "[";
    private static final String RIGHT_SIDE_OPEN_MARK = ")";
    private static final String RIGHT_SIDE_CLOSED_MARK = "]";

    private final Version from;
    private final Version to;
    private final boolean leftSideOpen;
    private final boolean rightSideOpen;

    public VersionRange(String from, String to) {
        this(from, to, false);
    }

    public VersionRange(String from, String to, boolean rightSideOpen) {
        checkVersionRange(from, to, rightSideOpen);
        this.from = getFromVersion(from);
        this.to = getToVersion(to);
        this.leftSideOpen = checkLeftSideWillBeOpen(from);
        this.rightSideOpen = checkRightSideWillBeOpen(to, rightSideOpen);
    }

    private static void checkVersionRange(String from, String to, boolean rightSideOpen) {
        if (!StringUtils.hasText(from) && !StringUtils.hasText(to)) {
            throw new IncorrectVersionRangeException("'from' or 'to' must be specified");
        }

        checkVersionRange(getFromVersion(from), getToVersion(to), rightSideOpen);
    }

    private static void checkVersionRange(Version from, Version to, boolean rightSideOpen) {
        if (from.equals(to) && rightSideOpen) {
            throw new IncorrectVersionRangeException("Range [%s-%s) is incorrect - the range does not contain any version".formatted(from, to));
        }
        if (from.compareTo(to) > 0) {
            throw new IncorrectVersionRangeException("'from' version cannot be greater then 'to' version (from: %s, to: %s)".formatted(from, to));
        }
    }

    private static Version getFromVersion(String from) {
        return Version.of(StringUtils.hasText(from) ? from : Version.MIN_VERSION);
    }

    private static Version getToVersion(String to) {
        return Version.of(StringUtils.hasText(to) ? to : Version.MAX_VERSION);
    }

    private static boolean checkLeftSideWillBeOpen(String from) {
        return !StringUtils.hasText(from);
    }

    private static boolean checkRightSideWillBeOpen(String to, boolean rightSideOpen) {
        return !StringUtils.hasText(to) || rightSideOpen;
    }

    public boolean includes(String other) {
        return includes(Version.of(other));
    }

    public boolean includes(Version otherVersion) {
        boolean isGreaterThanFrom = getFirstVersion().compareTo(otherVersion) <= 0;
        boolean isLowerThanTo = getLastVersion().compareTo(otherVersion) >= 0;

        return isGreaterThanFrom && isLowerThanTo;
    }

    public boolean isSeparate(VersionRange other) {
        return !includes(other.getFirstVersion())
                && !includes(other.getLastVersion())
                && !isInside(other);
    }

    public boolean isTouching(VersionRange other) {
        return other.getFirstVersion().equals(getLastVersion())
                || other.getLastVersion().equals(getFirstVersion());
    }

    public boolean isIntersected(VersionRange other) {
        return other.includes(getFirstVersion())
                || other.includes(getLastVersion())
                || (from.compareTo(other.from) < 0 && to.compareTo(other.to) > 0);
    }

    public Version getFirstVersion() {
        return leftSideOpen ? from.next() : from;
    }

    // TODO: 01.09.2022 Last version in range will be specified in this place?
    public Version getLastVersion() {
        return rightSideOpen ? to.previous() : to;
    }

    public boolean isInside(VersionRange other) {
        return other.includes(from) && other.includes(to);
    }

    public Version getFrom() {
        return from;
    }

    public Version getTo() {
        return to;
    }

    public boolean isLeftSideOpen() {
        return leftSideOpen;
    }

    public boolean isRightSideOpen() {
        return rightSideOpen;
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
        return String.format("range%s%s-%s%s", (leftSideOpen ? LEFT_SIDE_OPEN_MARK : LEFT_SIDE_CLOSED_MARK), from, to, (rightSideOpen ? RIGHT_SIDE_OPEN_MARK : RIGHT_SIDE_CLOSED_MARK));
    }
}
