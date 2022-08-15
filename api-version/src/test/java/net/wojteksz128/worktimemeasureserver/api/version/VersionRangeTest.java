package net.wojteksz128.worktimemeasureserver.api.version;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VersionRangeTest {

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_whenVersionRangeBoundariesAreProvided_create_versionRangeObject")
    public void whenVersionRangeBoundariesAreProvided_create_versionRangeObject(String from, String to) {
        VersionRange versionRange = new VersionRange(from, to);

        assertEquals(Version.of(from), versionRange.getFrom());
        assertEquals(Version.of(to), versionRange.getTo());
    }

    private static Stream<Arguments> provideArgumentsFor_whenVersionRangeBoundariesAreProvided_create_versionRangeObject() {
        return Stream.of(
                Arguments.of("v1.0", "v1.0"),
                Arguments.of("v1.0", "v1.1"),
                Arguments.of("v1.0", "v2.0"),
                Arguments.of("v1.0", "v2.1"),
                Arguments.of("v1.0", "v2147483647.2147483647")
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_whenIncorrectVersionRangeAreProvided_throw_illegalArgumentException")
    public void whenIncorrectVersionRangeAreProvided_throw_illegalArgumentException(String from, String to, String expectedMessage) {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> new VersionRange(from, to));

        assertEquals(expectedMessage, illegalArgumentException.getMessage());
    }

    public static Stream<Arguments> provideArgumentsFor_whenIncorrectVersionRangeAreProvided_throw_illegalArgumentException() {
        return Stream.of(
                Arguments.of(null, null, "'from' argument cannot be null"),
                Arguments.of("", null, "Version code do not match version pattern: [v]0.0 (current value: '')"),
                Arguments.of("v1.0", null, "Version code cannot be null"),
                Arguments.of("v2.0", "v1.0", "'from' version cannot be greater then 'to' version (from: v2.0, to: v1.0)"),
                Arguments.of("v1.1", "v1.0", "'from' version cannot be greater then 'to' version (from: v1.1, to: v1.0)")
        );
    }



    @ParameterizedTest
    @MethodSource("provideArgumentsFor_checkVersionInVersionRange_returnExpectedResult")
    public void checkVersionInVersionRange_returnExpectedResult(String from, String to, String testedVersionCode, boolean expected) {
        VersionRange versionRange = new VersionRange(from, to);

        assertEquals(expected, versionRange.includes(testedVersionCode));
    }

    private static Stream<Arguments> provideArgumentsFor_checkVersionInVersionRange_returnExpectedResult() {
        return Stream.of(
                Arguments.of("v2.0", "v3.0", "v1.0", false),
                Arguments.of("v2.0", "v3.0", "v1.2147483647", false),
                Arguments.of("v2.0", "v3.0", "v2.0", true),
                Arguments.of("v2.0", "v3.0", "v2.1", true),
                Arguments.of("v2.0", "v3.0", "v2.2147483647", true),
                Arguments.of("v2.0", "v3.0", "v3.0", true),
                Arguments.of("v2.0", "v3.0", "v3.1", false),
                Arguments.of("v2.0", "v3.0", "v4.0", false)
        );
    }
}