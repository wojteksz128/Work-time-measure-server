package net.wojteksz128.worktimemeasureserver.api.version;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VersionedResourceRequestConditionTest {

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_combiningTwoConditions_returnsCombinedObject")
    public void combiningTwoConditions_returnsCombinedObject(VersionedResourceRequestCondition condition1, VersionedResourceRequestCondition condition2, VersionedResourceRequestCondition expectedCondition) {
        VersionedResourceRequestCondition actualCondition = condition1.combine(condition2);

        assertEquals(expectedCondition, actualCondition);
    }

    private static Stream<Arguments> provideArgumentsFor_combiningTwoConditions_returnsCombinedObject() {
        return Stream.of(
                Arguments.of(
                        new VersionedResourceRequestCondition("", "v1.0", "v2.0"),
                        new VersionedResourceRequestCondition("application/vnd.app", Collections.emptyList()),
                        new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2.0")
                ),
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", Collections.emptyList()),
                        new VersionedResourceRequestCondition("", "v1.0", "v2.0"),
                        new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2.0")
                ),
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2.0"),
                        new VersionedResourceRequestCondition("", "v2.0", "v3.0"),
                        new VersionedResourceRequestCondition("application/vnd.app", List.of(new VersionRange("v1.0", "v2.0"), new VersionRange("v2.0", "v3.0")))
                ),
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2.0"),
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0"),
                        new VersionedResourceRequestCondition("application/vnd.app", List.of(new VersionRange("v1.0", "v2.0"), new VersionRange("v2.0", "v3.0")))
                )
        );
    }

    @Test
    public void combiningTwoConditionsWithDifferentMediaType_throwsIllegalArgumentException() {
        VersionedResourceRequestCondition condition1 = new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2.0");
        VersionedResourceRequestCondition condition2 = new VersionedResourceRequestCondition("application/vnd.other.app", "v2.0", "v3.0");

        assertThrows(IllegalArgumentException.class, () -> condition1.combine(condition2));
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_gettingMatchingCondition_returnsExpectedResults")
    public void gettingMatchingCondition_returnsExpectedResults() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        VersionedResourceRequestCondition expectedCondition = new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2.0");
        when(request.getHeader("Accept")).thenReturn("application/vnd.app-1.0+json");

        VersionedResourceRequestCondition actualCondition = expectedCondition.getMatchingCondition(request);
        assertEquals(expectedCondition, actualCondition);
    }

    private static Stream<Arguments> provideArgumentsFor_gettingMatchingCondition_returnsExpectedResults() {
        return Stream.of(
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0"),
                        "application/vnd.app-1.0+json",
                        null
                ),
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0"),
                        "application/vnd.app-1.2147483647+json",
                        null
                ),
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0"),
                        "application/vnd.app-2.0+json",
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0")
                ),
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0"),
                        "application/vnd.app-2.1+json",
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0")
                ),
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0"),
                        "application/vnd.app-2.2147483647+json",
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0")
                ),
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0"),
                        "application/vnd.app-3.0+json",
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0")
                ),
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0"),
                        "application/vnd.app-3.1+json",
                        null
                ),
                Arguments.of(
                        new VersionedResourceRequestCondition("application/vnd.app", "v2.0", "v3.0"),
                        "application/vnd.app-4.0+json",
                        null
                )
        );
    }
}