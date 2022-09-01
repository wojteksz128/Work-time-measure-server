package net.wojteksz128.worktimemeasureserver.api.version;

import net.wojteksz128.worktimemeasureserver.utils.ComparingResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @Test
    public void defaultVersionConstructor_shouldCreateVersionWithNoVersionApplied() {
        final Version version = Version.of();

        assertEquals(Version.MAJOR_NO_VERSION, version.major());
        assertEquals(Version.MINOR_NO_VERSION, version.minor());
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_oneStringVersionConstructor_creates_versionWithSpecifiedMajorAndMinorValues")
    public void oneStringVersionConstructor_creates_versionWithSpecifiedMajorAndMinorValues(String versionCode, int major, int minor) {
        final Version version = Version.of(versionCode);

        assertEquals(major, version.major());
        assertEquals(minor, version.minor());
    }

    private static Stream<Arguments> provideArgumentsFor_oneStringVersionConstructor_creates_versionWithSpecifiedMajorAndMinorValues() {
        return Stream.of(
                Arguments.of("0.0", 0, 0),
                Arguments.of("1.0", 1, 0),
                Arguments.of("0.1", 0, 1),
                Arguments.of("1.1", 1, 1),
                Arguments.of("1.2", 1, 2),
                Arguments.of("2.1", 2, 1),
                Arguments.of("v0.0", 0, 0),
                Arguments.of("v1.0", 1, 0),
                Arguments.of("v0.1", 0, 1),
                Arguments.of("v1.1", 1, 1),
                Arguments.of("v1.2", 1, 2),
                Arguments.of("v2.1", 2, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_oneStringVersionConstructorHasIncorrectFormat_throws_illegalVersionFormatException")
    public void oneStringVersionConstructorHasIncorrectFormat_throws_illegalVersionFormatException(String versionCode, String expectedMessage) {
        IllegalVersionFormatException illegalVersionFormatException = assertThrows(IllegalVersionFormatException.class, () -> Version.of(versionCode));
        assertEquals(expectedMessage, illegalVersionFormatException.getMessage());
    }

    private static Stream<Arguments> provideArgumentsFor_oneStringVersionConstructorHasIncorrectFormat_throws_illegalVersionFormatException() {
        return Stream.of(
                Arguments.of(null, "Version code cannot be null"),
                Arguments.of("", "Version code do not match version pattern: [v]0.0 (current value: '')"),
                Arguments.of("1", "Version code do not match version pattern: [v]0.0 (current value: '1')"),
                Arguments.of("v1", "Version code do not match version pattern: [v]0.0 (current value: 'v1')"),
                Arguments.of(".0", "Version code do not match version pattern: [v]0.0 (current value: '.0')"),
                Arguments.of("1.", "Version code do not match version pattern: [v]0.0 (current value: '1.')"),
                Arguments.of("v.0", "Version code do not match version pattern: [v]0.0 (current value: 'v.0')"),
                Arguments.of( "v1.", "Version code do not match version pattern: [v]0.0 (current value: 'v1.')"),
                Arguments.of("v-1.0", "Major cannot be negative (-1)."),
                Arguments.of("v1.-1", "Minor cannot be negative (-1)."),
                Arguments.of("v-1.-1", "Major cannot be negative (-1). Minor cannot be negative (-1).")
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_twoIntsVersionConstructor_creates_versionWithSpecifiedMajorAndMinorValues")
    public void twoIntsVersionConstructor_creates_versionWithSpecifiedMajorAndMinorValues(int major, int minor) {
        final Version version = Version.of(major, minor);

        assertEquals(major, version.major());
        assertEquals(minor, version.minor());
    }

    private static Stream<Arguments> provideArgumentsFor_twoIntsVersionConstructor_creates_versionWithSpecifiedMajorAndMinorValues() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(1, 0),
                Arguments.of(0, 1),
                Arguments.of(1, 1),
                Arguments.of(1, 2),
                Arguments.of(2, 1),
                Arguments.of(0, 0),
                Arguments.of(1, 0),
                Arguments.of(0, 1),
                Arguments.of(1, 1),
                Arguments.of(1, 2),
                Arguments.of(2, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_twoIllegalIntsVersionConstructor_throws_illegalVersionFormatException")
    public void twoIllegalIntsVersionConstructor_throws_illegalVersionFormatException(int major, int minor, String expectedMessage) {
        IllegalVersionFormatException illegalVersionFormatException = assertThrows(IllegalVersionFormatException.class, () -> Version.of(major, minor));
        assertEquals(expectedMessage, illegalVersionFormatException.getMessage());
    }

    private static Stream<Arguments> provideArgumentsFor_twoIllegalIntsVersionConstructor_throws_illegalVersionFormatException() {
        return Stream.of(
                Arguments.of(-1, 1, "Major cannot be negative (-1)."),
                Arguments.of(1, -1, "Minor cannot be negative (-1)."),
                Arguments.of(-1, -1, "Major cannot be negative (-1). Minor cannot be negative (-1).")
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_compareTwoVersions_returnsExpectedResult")
    public void compareTwoVersions_returnsExpectedResult(String versionCode1, String versionCode2, ComparingResult expectedResult) {
        final Version version1 = Version.of(versionCode1);
        final Version version2 = Version.of(versionCode2);

        assertTrue(switch (expectedResult) {
            case EQUALS -> version1.compareTo(version2) == 0 && version2.compareTo(version1) == 0;
            case GREATER_THAN -> version1.compareTo(version2) > 0 && version2.compareTo(version1) < 0;
            case LOWER_THAN -> version1.compareTo(version2) < 0 && version2.compareTo(version1) > 0;
        });
    }

    private static Stream<Arguments> provideArgumentsFor_compareTwoVersions_returnsExpectedResult() {
        return Stream.of(
                Arguments.of("1.0", "1.0", ComparingResult.EQUALS),
                Arguments.of("1.1", "1.0", ComparingResult.GREATER_THAN),
                Arguments.of("1.0", "1.1", ComparingResult.LOWER_THAN),
                Arguments.of("v1.0", "1.0", ComparingResult.EQUALS),
                Arguments.of("v1.1", "1.0", ComparingResult.GREATER_THAN),
                Arguments.of("v1.0", "1.1", ComparingResult.LOWER_THAN),
                Arguments.of("1.0", "v1.0", ComparingResult.EQUALS),
                Arguments.of("1.1", "v1.0", ComparingResult.GREATER_THAN),
                Arguments.of("1.0", "v1.1", ComparingResult.LOWER_THAN),
                Arguments.of("v1.0", "v1.0", ComparingResult.EQUALS),
                Arguments.of("v1.1", "v1.0", ComparingResult.GREATER_THAN),
                Arguments.of("v1.0", "v1.1", ComparingResult.LOWER_THAN)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_previousVersion_returnsExpectedResult")
    public void previousVersion_returnsExpectedResult(Version version, Version expectedVersion) {
        Version previous = version.previous();

        assertEquals(expectedVersion, previous);
    }

    private static Stream<Arguments> provideArgumentsFor_previousVersion_returnsExpectedResult() {
        return Stream.of(
                Arguments.of(Version.of("v0.1"), Version.of("v0.0")),
                Arguments.of(Version.of("v1.0"), Version.of("v0.2147483647")),
                Arguments.of(Version.of("v1.1"), Version.of("v1.0")),
                Arguments.of(Version.of("v1.2"), Version.of("v1.1")),
                Arguments.of(Version.of("v1.2147483647"), Version.of("v1.2147483646")),
                Arguments.of(Version.of("v2.0"), Version.of("v1.2147483647"))
                );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_nextVersion_returnsExpectedResult")
    public void nextVersion_returnsExpectedResult(Version version, Version expectedVersion) {
        Version next = version.next();

        assertEquals(expectedVersion, next);
    }

    private static Stream<Arguments> provideArgumentsFor_nextVersion_returnsExpectedResult() {
        return Stream.of(
                Arguments.of(Version.of("v0.0"), Version.of("v0.1")),
                Arguments.of(Version.of("v0.2147483647"), Version.of("v1.0")),
                Arguments.of(Version.of("v1.0"), Version.of("v1.1")),
                Arguments.of(Version.of("v1.1"), Version.of("v1.2")),
                Arguments.of(Version.of("v1.2147483646"), Version.of("v1.2147483647")),
                Arguments.of(Version.of("v1.2147483647"), Version.of("v2.0"))
                );
    }
}