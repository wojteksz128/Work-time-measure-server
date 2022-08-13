package net.wojteksz128.worktimemeasureserver.api.version;

public record Version(int major, int minor) implements Comparable<Version> {
    public static final String MAX_VERSION = "v2147483647.2147483647";
    public static final String VERSION_PREFIX = "v";
    public static final int MAJOR_NO_VERSION = 0;
    public static final int MINOR_NO_VERSION = 0;

    private static final int MAJOR_ELEMENT_INDEX = 0;
    private static final int MINOR_ELEMENT_INDEX = 1;

    public Version() {
        this(MAJOR_NO_VERSION, MINOR_NO_VERSION);
    }

    public Version(String version) {
        this(getVersionPart(version, MAJOR_ELEMENT_INDEX), getVersionPart(version, MINOR_ELEMENT_INDEX));
    }

    private static int getVersionPart(String version, int versionPart) {
        if (version.startsWith(VERSION_PREFIX)) {
            version = version.substring(VERSION_PREFIX.length());
        }

        String[] tokens = version.split("\\.");

        if (tokens.length < versionPart) {
            throw new IllegalArgumentException("Invalid version " + version + ". The version must have major and minor number.");
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
    public String toString() {
        return String.format("%s%d.%d", VERSION_PREFIX, major(), minor());
    }
}
