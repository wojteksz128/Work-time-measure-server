package net.wojteksz128.worktimemeasureserver.api.version;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
class VersionRangeTest {

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_whenBothSideClosedVersionRangeBoundariesAreProvided_create_versionRangeObject")
    public void whenBothSideClosedVersionRangeBoundariesAreProvided_create_versionRangeObject(String from, String to, String expectedFrom, String expectedTo, String expectedToStringResult) {
        VersionRange versionRange = new VersionRange(from, to);

        assertEquals(Version.of(expectedFrom), versionRange.getFrom());
        assertEquals(Version.of(expectedTo), versionRange.getTo());
        assertFalse(versionRange.isLeftSideOpen());
        assertFalse(versionRange.isRightSideOpen());
        assertEquals(expectedToStringResult, versionRange.toString());
    }

    private static Stream<Arguments> provideArgumentsFor_whenBothSideClosedVersionRangeBoundariesAreProvided_create_versionRangeObject() {
        return Stream.of(
                Arguments.of("v1.0", "v1.0", "v1.0", "v1.0", "range[v1.0-v1.0]"),
                Arguments.of("v1.0", "v1.1", "v1.0", "v1.1", "range[v1.0-v1.1]"),
                Arguments.of("v1.0", "v2.0", "v1.0", "v2.0", "range[v1.0-v2.0]"),
                Arguments.of("v1.0", "v2.1", "v1.0", "v2.1", "range[v1.0-v2.1]"),
                Arguments.of("v1.0", "v2147483647.2147483647", "v1.0", "v2147483647.2147483647", "range[v1.0-v2147483647.2147483647]")
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_whenOneSideOpenVersionRangeAreProvided_create_versionRangeObject")
    public void whenOneSideOpenVersionRangeAreProvided_create_versionRangeObject(String from, String to, Optional<Boolean> optionalIsRightSideOpen, String expectedFrom, String expectedTo, boolean expectedLeftSideOpen, boolean expectedRightSideOpen, String expectedToStringResult) {
        VersionRange versionRange = optionalIsRightSideOpen.map(isRightSideOpen -> new VersionRange(from, to, isRightSideOpen)).orElseGet(() -> new VersionRange(from, to));

        assertEquals(Version.of(expectedFrom), versionRange.getFrom());
        assertEquals(Version.of(expectedTo), versionRange.getTo());
        assertEquals(expectedLeftSideOpen, versionRange.isLeftSideOpen());
        assertEquals(expectedRightSideOpen, versionRange.isRightSideOpen());
        assertEquals(expectedToStringResult, versionRange.toString());
    }

    public static Stream<Arguments> provideArgumentsFor_whenOneSideOpenVersionRangeAreProvided_create_versionRangeObject() {
        return Stream.of(
                Arguments.of("v1.0", "v1.1", Optional.of(true), "v1.0", "v1.1", false, true, "range[v1.0-v1.1)"),
                Arguments.of("v1.0", "v2.0", Optional.of(true), "v1.0", "v2.0", false, true, "range[v1.0-v2.0)"),
                Arguments.of("v1.0", "v2.1", Optional.of(true), "v1.0", "v2.1", false, true, "range[v1.0-v2.1)"),
                Arguments.of("v1.0", "v2147483647.2147483647", Optional.of(true), "v1.0", "v2147483647.2147483647", false, true, "range[v1.0-v2147483647.2147483647)"),
                Arguments.of("v1.0", "", Optional.empty(), "v1.0", "v2147483647.2147483647", false, true, "range[v1.0-v2147483647.2147483647)"),
                Arguments.of("v1.0", null, Optional.empty(), "v1.0", "v2147483647.2147483647", false, true, "range[v1.0-v2147483647.2147483647)"),
                Arguments.of("", "v2.0", Optional.of(false), "v0.0", "v2.0", true, false, "range(v0.0-v2.0]"),
                Arguments.of(null, "v2.0", Optional.of(false), "v0.0", "v2.0", true, false, "range(v0.0-v2.0]")
                        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_whenIncorrectVersionRangeAreProvided_throw_incorrectVersionException")
    public void whenIncorrectVersionRangeAreProvided_throw_incorrectVersionException(String from, String to, Optional<Boolean> optionalIsRightSideOpen, Exception expectedException) {
        Exception actualException = assertThrows(Exception.class, () -> optionalIsRightSideOpen.map(isRightSideOpen -> new VersionRange(from, to, isRightSideOpen)).orElseGet(() -> new VersionRange(from, to)));

        assertEquals(expectedException.getClass(), actualException.getClass());
        assertEquals(expectedException.getMessage(), actualException.getMessage());
    }

    public static Stream<Arguments> provideArgumentsFor_whenIncorrectVersionRangeAreProvided_throw_incorrectVersionException() {
        return Stream.of(
                Arguments.of("v1.0", "v1.0", Optional.of(true), new IncorrectVersionRangeException("Range [v1.0-v1.0) is incorrect - the range does not contain any version")),
                Arguments.of(null, null, Optional.empty(), new IncorrectVersionRangeException("'from' or 'to' must be specified")),
                Arguments.of("", null, Optional.empty(), new IncorrectVersionRangeException("'from' or 'to' must be specified")),
                Arguments.of(null, "", Optional.empty(), new IncorrectVersionRangeException("'from' or 'to' must be specified")),
                Arguments.of("", "", Optional.empty(), new IncorrectVersionRangeException("'from' or 'to' must be specified")),
                Arguments.of("v2.0", "v1.0", Optional.empty(), new IncorrectVersionRangeException("'from' version cannot be greater then 'to' version (from: v2.0, to: v1.0)")),
                Arguments.of("v1.1", "v1.0", Optional.empty(), new IncorrectVersionRangeException("'from' version cannot be greater then 'to' version (from: v1.1, to: v1.0)"))
        );
    }


    @ParameterizedTest
    @MethodSource("provideArgumentsFor_checkVersionInVersionRange_returnExpectedResult")
    public void checkVersionInVersionRange_returnExpectedResult(String from, String to, boolean isRightSideOpen, String testedVersionCode, boolean expected) {
        VersionRange versionRange = isRightSideOpen ? new VersionRange(from, to, true) : new VersionRange(from, to);

        assertEquals(expected, versionRange.includes(testedVersionCode));
    }

    private static Stream<Arguments> provideArgumentsFor_checkVersionInVersionRange_returnExpectedResult() {
        return Stream.of(
                Arguments.of("v2.0", "v3.0", false, "v1.0", false),
                Arguments.of("v2.0", "v3.0", false, "v1.2147483647", false),
                Arguments.of("v2.0", "v3.0", false, "v2.0", true),
                Arguments.of("v2.0", "v3.0", false, "v2.1", true),
                Arguments.of("v2.0", "v3.0", false, "v2.2147483647", true),
                Arguments.of("v2.0", "v3.0", false, "v3.0", true),
                Arguments.of("v2.0", "v3.0", false, "v3.1", false),
                Arguments.of("v2.0", "v3.0", false, "v4.0", false),
                Arguments.of("v2.0", "v3.0", true, "v1.0", false),
                Arguments.of("v2.0", "v3.0", true, "v1.2147483647", false),
                Arguments.of("v2.0", "v3.0", true, "v2.0", true),
                Arguments.of("v2.0", "v3.0", true, "v2.1", true),
                Arguments.of("v2.0", "v3.0", true, "v2.2147483647", true),
                Arguments.of("v2.0", "v3.0", true, "v3.0", false),
                Arguments.of("v2.0", "v3.0", true, "v3.1", false),
                Arguments.of("v2.0", "v3.0", true, "v4.0", false)
        );
    }
}