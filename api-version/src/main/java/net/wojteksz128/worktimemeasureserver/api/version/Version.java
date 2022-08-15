package net.wojteksz128.worktimemeasureserver.api.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Version implements Comparable<Version> {
    public static final String MAX_VERSION = "v2147483647.2147483647";
    public static final String VERSION_PREFIX = "v";
    public static final String VERSION_PARTS_DELIMITER = ".";
    public static final int MAJOR_NO_VERSION = 0;
    public static final int MINOR_NO_VERSION = 0;

    private static final int MAJOR_ELEMENT_INDEX = 0;
    private static final int MINOR_ELEMENT_INDEX = 1;
    private static final Pattern versionPattern = Pattern.compile("^%s?(-?\\d+)%s(-?\\d+)$".formatted(VERSION_PREFIX, VERSION_PARTS_DELIMITER));

    private final int major;
    private final int minor;

    private Version() {
        this(MAJOR_NO_VERSION, MINOR_NO_VERSION);
    }

    private Version(String versionCode) {
        this(getVersionPart(versionCode, MAJOR_ELEMENT_INDEX), getVersionPart(versionCode, MINOR_ELEMENT_INDEX));
    }

    private Version(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public static Version of() {
        return new Version();
    }

    public static Version of(String versionCode) {
        checkVersionCode(versionCode);
        return new Version(versionCode);
    }

    private static void checkVersionCode(String versionCode) {
        if (versionCode == null) {
            throw new IllegalVersionFormatException("Version code cannot be null");
        }
        Matcher matcher = versionPattern.matcher(versionCode);
        if (!matcher.matches()) {
            throw new IllegalVersionFormatException("Version code do not match version pattern: [%s]0%s0 (current value: '%s')".formatted(VERSION_PREFIX, VERSION_PARTS_DELIMITER, versionCode));
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));
        checkVersionParts(major, minor);
    }

    public static Version of(int major, int minor) {
        checkVersionParts(major, minor);
        return new Version(major, minor);
    }

    private static void checkVersionParts(int major, int minor) {
        StringBuilder errorMessage = new StringBuilder();

        if (major < 0) {
            errorMessage.append("Major cannot be negative (%d). ".formatted(major));
        }
        if (minor < 0) {
            errorMessage.append("Minor cannot be negative (%d). ".formatted(minor));
        }

        if (!errorMessage.isEmpty()) {
            errorMessage.deleteCharAt(errorMessage.length() - 1);
            throw new IllegalVersionFormatException(errorMessage.toString());
        }
    }

    private static int getVersionPart(String version, int versionPart) {
        if (version.startsWith(VERSION_PREFIX)) {
            version = version.substring(VERSION_PREFIX.length());
        }

        String[] tokens = version.split("\\%s".formatted(VERSION_PARTS_DELIMITER));

        if (tokens.length < versionPart) {
            throw new IllegalVersionFormatException("Invalid version %s. The version must have major and minor number.".formatted(version));
        }

        return Integer.parseInt(tokens[versionPart]);
    }

    @Override
    public int compareTo(Version other) {
        int majorComparingResult = Integer.compare(this.major(), other.major());

        if (majorComparingResult != 0) {
            return majorComparingResult;
        }

        return Integer.compare(this.minor(), other.minor());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        if (major != version.major) return false;
        return minor == version.minor;
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        return result;
    }

    @Override
    public String toString() {
        return "%s%d.%d".formatted(VERSION_PREFIX, major(), minor());
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

}
