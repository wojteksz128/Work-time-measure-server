package net.wojteksz128.worktimemeasureserver.api.version;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VersionRangeTest {

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