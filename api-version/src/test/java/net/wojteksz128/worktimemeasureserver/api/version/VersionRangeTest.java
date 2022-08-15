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
    public void whenVersionRangeBoundariesAreProvided_create_versionRangeObject(String from, String to, String expectedFrom, String expectedTo) {
        VersionRange versionRange = new VersionRange(from, to);

        assertEquals(Version.of(expectedFrom), versionRange.getFrom());
        assertEquals(Version.of(expectedTo), versionRange.getTo());
    }

    private static Stream<Arguments> provideArgumentsFor_whenVersionRangeBoundariesAreProvided_create_versionRangeObject() {
        return Stream.of(
                Arguments.of("v1.0", "v1.0", "v1.0", "v1.0"),
                Arguments.of("v1.0", "v1.1", "v1.0", "v1.1"),
                Arguments.of("v1.0", "v2.0", "v1.0", "v2.0"),
                Arguments.of("v1.0", "v2.1", "v1.0", "v2.1"),
                Arguments.of("v1.0", "v2147483647.2147483647", "v1.0", "v2147483647.2147483647"),
                Arguments.of("v1.0", "", "v1.0", "v2147483647.2147483647"),
                Arguments.of("v1.0", null, "v1.0", "v2147483647.2147483647"),
                Arguments.of("", "v2.0", "v0.0", "v2.0"),
                Arguments.of(null, "v2.0", "v0.0", "v2.0")
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_whenIncorrectVersionRangeAreProvided_throw_incorrectVersionException")
    public void whenIncorrectVersionRangeAreProvided_throw_incorrectVersionException(String from, String to, Exception expectedException) {
        Exception actualException = assertThrows(Exception.class, () -> new VersionRange(from, to));

        assertEquals(expectedException.getClass(), actualException.getClass());
        assertEquals(expectedException.getMessage(), actualException.getMessage());
    }

    public static Stream<Arguments> provideArgumentsFor_whenIncorrectVersionRangeAreProvided_throw_incorrectVersionException() {
        return Stream.of(
                Arguments.of(null, null, new IncorrectVersionRangeException("'from' or 'to' must be specified")),
                Arguments.of("", null, new IncorrectVersionRangeException("'from' or 'to' must be specified")),
                Arguments.of(null, "", new IncorrectVersionRangeException("'from' or 'to' must be specified")),
                Arguments.of("", "", new IncorrectVersionRangeException("'from' or 'to' must be specified")),
                Arguments.of("v2.0", "v1.0", new IncorrectVersionRangeException("'from' version cannot be greater then 'to' version (from: v2.0, to: v1.0)")),
                Arguments.of("v1.1", "v1.0", new IncorrectVersionRangeException("'from' version cannot be greater then 'to' version (from: v1.1, to: v1.0)"))
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