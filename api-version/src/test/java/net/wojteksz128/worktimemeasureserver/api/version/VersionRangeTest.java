package net.wojteksz128.worktimemeasureserver.api.version;

import net.wojteksz128.worktimemeasureserver.utils.VersionRangesRelation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static net.wojteksz128.worktimemeasureserver.utils.VersionRangesRelation.*;
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

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_checkVersionRangesRelations_returnExpectedResult")
    public void checkVersionRangesAreSeparate_returnExpectedResult(VersionRange versionRange1, VersionRange versionRange2, VersionRangesRelation expectedResult) {
        boolean vr1SeparateVr2 = versionRange1.isSeparate(versionRange2);
        boolean vr2SeparateVr1 = versionRange2.isSeparate(versionRange1);

        assertEquals(expectedResult.isSeparate(), vr1SeparateVr2);
        assertEquals(expectedResult.isSeparate(), vr2SeparateVr1);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_checkVersionRangesRelations_returnExpectedResult")
    public void checkVersionRangesAreTouchesYourself_returnExpectedResult(VersionRange versionRange1, VersionRange versionRange2, VersionRangesRelation expectedResult) {
        boolean vr1TouchingVr2 = versionRange1.isTouching(versionRange2);
        boolean vr2TouchingVr1 = versionRange2.isTouching(versionRange1);

        assertEquals(expectedResult.isTouching(), vr1TouchingVr2);
        assertEquals(expectedResult.isTouching(), vr2TouchingVr1);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_checkVersionRangesRelations_returnExpectedResult")
    public void checkFirstVersionRangeIsIntersectWithSecondVersionRange_returnExpectedResult(VersionRange versionRange1, VersionRange versionRange2, VersionRangesRelation expectedResult) {
        boolean vr1IntersectedVr2 = versionRange1.isIntersected(versionRange2);
        boolean vr2IntersectedVr1 = versionRange2.isIntersected(versionRange1);

        assertEquals(expectedResult.isIntersected(), vr1IntersectedVr2);
        assertEquals(expectedResult.isIntersected(), vr2IntersectedVr1);
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_checkVersionRangesRelations_returnExpectedResult")
    public void checkFirstVersionRangeIsInsideSecondVersionRange_returnExpectedResult(VersionRange versionRange1, VersionRange versionRange2, VersionRangesRelation expectedResult) {
        boolean inside = versionRange1.isInside(versionRange2);

        assertEquals(expectedResult.isInside(), inside);
    }

    private static Stream<Arguments> provideArgumentsFor_checkVersionRangesRelations_returnExpectedResult() {
        VersionRangesRelation separateRelation = new Builder().isSeparate().build();
        VersionRangesRelation touchOutsideRelation = new Builder().isTouching().isIntersected().build();
        VersionRangesRelation touchInsideRelation = new Builder().isTouching().isIntersected().isInside().build();
        VersionRangesRelation intersectRelation = new Builder().isIntersected().build();
        VersionRangesRelation insideRelation = new Builder().isIntersected().isInside().build();

        return Stream.of(
                Arguments.of(new VersionRange("v3.0", "v3.0"), new VersionRange("v1.0", "v2.0"), separateRelation),
                Arguments.of(new VersionRange("v2.1", "v2.1"), new VersionRange("v1.0", "v2.0"), separateRelation),
                Arguments.of(new VersionRange("v2.0", "v2.0"), new VersionRange("v1.0", "v2.0"), touchInsideRelation),
                Arguments.of(new VersionRange("v1.2147483647", "v1.2147483647"), new VersionRange("v1.0", "v2.0"), insideRelation),
                Arguments.of(new VersionRange("v1.1", "v1.1"), new VersionRange("v1.0", "v2.0"), insideRelation),
                Arguments.of(new VersionRange("v1.0", "v1.0"), new VersionRange("v1.0", "v2.0"), touchInsideRelation),
                Arguments.of(new VersionRange("v0.9", "v0.9"), new VersionRange("v1.0", "v2.0"), separateRelation),

                Arguments.of(new VersionRange("v3.0", "v3.0"), new VersionRange("v1.0", "v2.0", true), separateRelation),
                Arguments.of(new VersionRange("v2.1", "v2.1"), new VersionRange("v1.0", "v2.0", true), separateRelation),
                Arguments.of(new VersionRange("v2.0", "v2.0"), new VersionRange("v1.0", "v2.0", true), separateRelation),
                Arguments.of(new VersionRange("v1.2147483647", "v1.2147483647"), new VersionRange("v1.0", "v2.0", true), touchInsideRelation),
                Arguments.of(new VersionRange("v1.1", "v1.1"), new VersionRange("v1.0", "v2.0", true), insideRelation),
                Arguments.of(new VersionRange("v1.0", "v1.0"), new VersionRange("v1.0", "v2.0", true), touchInsideRelation),
                Arguments.of(new VersionRange("v0.9", "v0.9"), new VersionRange("v1.0", "v2.0", true), separateRelation),

                Arguments.of(new VersionRange("v3.0", "v4.0"), new VersionRange("v1.0", "v2.0"), separateRelation),
                Arguments.of(new VersionRange("v2.1", "v3.0"), new VersionRange("v1.0", "v2.0"), separateRelation),
                Arguments.of(new VersionRange("v2.0", "v3.0"), new VersionRange("v1.0", "v2.0"), touchOutsideRelation),
                Arguments.of(new VersionRange("v1.2147483647", "v3.0"), new VersionRange("v1.0", "v2.0"), intersectRelation),
                Arguments.of(new VersionRange("v1.2147483647", "v2.0"), new VersionRange("v1.0", "v2.0"), insideRelation),
                Arguments.of(new VersionRange("v1.1", "v3.0"), new VersionRange("v1.0", "v2.0"), intersectRelation),
                Arguments.of(new VersionRange("v1.1", "v2.0"), new VersionRange("v1.0", "v2.0"), insideRelation),
                Arguments.of(new VersionRange("v1.1", "v1.2147483647"), new VersionRange("v1.0", "v2.0"), insideRelation),
                Arguments.of(new VersionRange("v1.0", "v3.0"), new VersionRange("v1.0", "v2.0"), intersectRelation),
                Arguments.of(new VersionRange("v1.0", "v2.0"), new VersionRange("v1.0", "v2.0"), insideRelation),
                Arguments.of(new VersionRange("v1.0", "v1.2147483647"), new VersionRange("v1.0", "v2.0"), insideRelation),
                Arguments.of(new VersionRange("v1.0", "v1.1"), new VersionRange("v1.0", "v2.0"), insideRelation),
                Arguments.of(new VersionRange("v0.9", "v3.0"), new VersionRange("v1.0", "v2.0"), intersectRelation),

                Arguments.of(new VersionRange("v3.0", "v4.0"), new VersionRange("v1.0", "v2.0", true), separateRelation),
                Arguments.of(new VersionRange("v2.1", "v3.0"), new VersionRange("v1.0", "v2.0", true), separateRelation),
                Arguments.of(new VersionRange("v2.0", "v3.0"), new VersionRange("v1.0", "v2.0", true), separateRelation),
                Arguments.of(new VersionRange("v1.2147483647", "v3.0"), new VersionRange("v1.0", "v2.0", true), touchOutsideRelation),
                Arguments.of(new VersionRange("v1.2147483647", "v2.0"), new VersionRange("v1.0", "v2.0", true), touchOutsideRelation),
                Arguments.of(new VersionRange("v1.1", "v3.0"), new VersionRange("v1.0", "v2.0", true), intersectRelation),
                Arguments.of(new VersionRange("v1.1", "v2.0"), new VersionRange("v1.0", "v2.0", true), intersectRelation),
                Arguments.of(new VersionRange("v1.1", "v1.2147483647"), new VersionRange("v1.0", "v2.0", true), insideRelation),
                Arguments.of(new VersionRange("v1.0", "v3.0"), new VersionRange("v1.0", "v2.0", true), intersectRelation),
                Arguments.of(new VersionRange("v1.0", "v2.0"), new VersionRange("v1.0", "v2.0", true), intersectRelation),
                Arguments.of(new VersionRange("v1.0", "v1.2147483647"), new VersionRange("v1.0", "v2.0", true), insideRelation),
                Arguments.of(new VersionRange("v1.0", "v1.1"), new VersionRange("v1.0", "v2.0", true), insideRelation),
                Arguments.of(new VersionRange("v0.9", "v3.0"), new VersionRange("v1.0", "v2.0", true), intersectRelation)
        );
    }
}