package net.wojteksz128.worktimemeasureserver.api.version;

import net.wojteksz128.worktimemeasureserver.utils.ComparingResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionTest {

    @Test
    public void defaultVersionConstructor_shouldCreateVersionWithNoVersionApplied() {
        final Version version = new Version();

        assertEquals(Version.MAJOR_NO_VERSION, version.major());
        assertEquals(Version.MINOR_NO_VERSION, version.minor());
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_oneStringVersionConstructor_creates_versionWithSpecifiedMajorAndMinorValues")
    public void oneStringVersionConstructor_creates_versionWithSpecifiedMajorAndMinorValues(String versionCode, int major, int minor) {
        final Version version = new Version(versionCode);

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
    @MethodSource("provideArgumentsFor_twoIntsVersionConstructor_creates_versionWithSpecifiedMajorAndMinorValues")
    public void twoIntsVersionConstructor_creates_versionWithSpecifiedMajorAndMinorValues(int major, int minor) {
        final Version version = new Version(major, minor);

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
    @MethodSource("provideArgumentsFor_compareTwoVersions_returnsExpectedResult")
    public void compareTwoVersions_returnsExpectedResult(String versionCode1, String versionCode2, ComparingResult expectedResult) {
        final Version version1 = new Version(versionCode1);
        final Version version2 = new Version(versionCode2);

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
}