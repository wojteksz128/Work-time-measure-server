package net.wojteksz128.worktimemeasureserver.api;

import net.wojteksz128.worktimemeasureserver.api.version.VersionedResource;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResourceRequestCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CustomRequestMappingHandlerMappingTest {

    private CustomRequestMappingHandlerMapping requestMappingHandlerMapping;

    @BeforeEach
    public void before() {
        requestMappingHandlerMapping = new CustomRequestMappingHandlerMapping();
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_gettingCustomTypeCondition_returns_expectedRequestCondition")
    public void gettingCustomTypeCondition_returns_expectedRequestCondition(Class<?> webClass, VersionedResourceRequestCondition expectedRequestCondition) {
        RequestCondition<?> actualRequestCondition = requestMappingHandlerMapping.getCustomTypeCondition(webClass);

        assertTrue(actualRequestCondition instanceof VersionedResourceRequestCondition);
        assertEquals(expectedRequestCondition, actualRequestCondition);
    }

    private static Stream<Arguments> provideArgumentsFor_gettingCustomTypeCondition_returns_expectedRequestCondition() {
        return Stream.of(
                // Example of bad situation
                /*Arguments.of(
                        TestClass1.class,
                        new VersionedResourceRequestCondition("", Collections.emptyList())
                ),*/
                Arguments.of(
                        TestClass2.class,
                        new VersionedResourceRequestCondition("application/vnd.app", Collections.emptyList())
                ),
                Arguments.of(
                        TestClass3.class,
                        new VersionedResourceRequestCondition("", "v1.0", "v2147483647.2147483647")
                ),
                // Example of bad situation
                /*Arguments.of(
                        TestClass4.class,
                        new VersionedResourceRequestCondition("", Collections.emptyList())
                ),*/
                Arguments.of(
                        TestClass5.class,
                        new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2147483647.2147483647")
                ),
                Arguments.of(
                        TestClass6.class,
                        new VersionedResourceRequestCondition("", "v1.0", "v2.0")
                ),
                Arguments.of(
                        TestClass7.class,
                        new VersionedResourceRequestCondition("application/vnd.app", Collections.emptyList())
                ),
                Arguments.of(
                        TestClass8.class,
                        new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2.0")
                )
                );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_gettingCustomTypeConditionForClassAnnotatedByWrongUsedVersionedResource_throws_illegalStateException")
    public void gettingCustomTypeConditionForClassAnnotatedByWrongUsedVersionedResource_throws_illegalStateException(Class<?> webClass, String expectedMessage) {
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> requestMappingHandlerMapping.getCustomTypeCondition(webClass));
        assertEquals(expectedMessage, illegalStateException.getMessage());
    }

    private static Stream<Arguments> provideArgumentsFor_gettingCustomTypeConditionForClassAnnotatedByWrongUsedVersionedResource_throws_illegalStateException() {
        return Stream.of(
                Arguments.of(TestClass1.class, "VersionedResource annotation must define media type or version range for resource"),
                Arguments.of(TestClass4.class, "VersionedResource annotation must define media type or version range for resource")
        );
    }
}

@VersionedResource
class TestClass1 {
}

@VersionedResource(media = "application/vnd.app")
class TestClass2 {
}

@VersionedResource(from = "v1.0")
class TestClass3 {
}

@VersionedResource(to = "v2.0")
class TestClass4 {
}

@VersionedResource(media = "application/vnd.app", from = "v1.0")
class TestClass5 {
}

@VersionedResource(from = "v1.0", to = "v2.0")
class TestClass6 {
}

@VersionedResource(media = "application/vnd.app", to = "v2.0")
class TestClass7 {
}

@VersionedResource(media = "application/vnd.app", from = "v1.0", to = "v2.0")
class TestClass8 {
}