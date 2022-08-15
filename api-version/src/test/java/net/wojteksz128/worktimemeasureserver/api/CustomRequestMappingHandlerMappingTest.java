package net.wojteksz128.worktimemeasureserver.api;

import net.wojteksz128.worktimemeasureserver.api.version.IncorrectVersionedResourceException;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResource;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResourceRequestCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import java.lang.reflect.Method;
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
        IncorrectVersionedResourceException incorrectVersionedResourceException = assertThrows(IncorrectVersionedResourceException.class, () -> requestMappingHandlerMapping.getCustomTypeCondition(webClass));
        assertEquals(expectedMessage, incorrectVersionedResourceException.getMessage());
    }

    private static Stream<Arguments> provideArgumentsFor_gettingCustomTypeConditionForClassAnnotatedByWrongUsedVersionedResource_throws_illegalStateException() {
        return Stream.of(
                Arguments.of(TestClass1.class, "VersionedResource annotation must define media type or version range for resource"),
                Arguments.of(TestClass4.class, "VersionedResource annotation must define media type or version range for resource")
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_gettingCustomMethodCondition_returns_expectedRequestCondition")
    public void gettingCustomMethodCondition_returns_expectedRequestCondition(Method method, VersionedResourceRequestCondition expectedRequestCondition) {
        RequestCondition<?> actualRequestCondition = requestMappingHandlerMapping.getCustomMethodCondition(method);

        assertTrue(actualRequestCondition instanceof VersionedResourceRequestCondition);
        assertEquals(expectedRequestCondition, actualRequestCondition);
    }

    public static Stream<Arguments> provideArgumentsFor_gettingCustomMethodCondition_returns_expectedRequestCondition() {
        Method test2;
        Method test3;
        Method test5;
        Method test6;
        Method test7;
        Method test8;
        try {
            test2 = TestMethodClass.class.getMethod("test2");
            test3 = TestMethodClass.class.getMethod("test3");
            test5 = TestMethodClass.class.getMethod("test5");
            test6 = TestMethodClass.class.getMethod("test6");
            test7 = TestMethodClass.class.getMethod("test7");
            test8 = TestMethodClass.class.getMethod("test8");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return Stream.of(
                Arguments.of(test2, new VersionedResourceRequestCondition("application/vnd.app", Collections.emptyList())),
                Arguments.of(test3, new VersionedResourceRequestCondition("", "v1.0", "v2147483647.2147483647")),
                Arguments.of(test5, new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2147483647.2147483647")),
                Arguments.of(test6, new VersionedResourceRequestCondition("", "v1.0", "v2.0")),
                Arguments.of(test7, new VersionedResourceRequestCondition("application/vnd.app", Collections.emptyList())),
                Arguments.of(test8, new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2.0"))
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_gettingCustomMethodConditionForClassAnnotatedByWrongUsedVersionedResource_throws_illegalStateException")
    public void gettingCustomMethodConditionForClassAnnotatedByWrongUsedVersionedResource_throws_illegalStateException(Method method, String expectedMessage) {
        IncorrectVersionedResourceException incorrectVersionedResourceException = assertThrows(IncorrectVersionedResourceException.class, () -> requestMappingHandlerMapping.getCustomMethodCondition(method));
        assertEquals(expectedMessage, incorrectVersionedResourceException.getMessage());
    }

    private static Stream<Arguments> provideArgumentsFor_gettingCustomMethodConditionForClassAnnotatedByWrongUsedVersionedResource_throws_illegalStateException() {
        Method test1;
        Method test4;
        try {
            test1 = TestMethodClass.class.getMethod("test1");
            test4 = TestMethodClass.class.getMethod("test4");

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return Stream.of(
                Arguments.of(test1, "VersionedResource annotation must define media type or version range for resource"),
                Arguments.of(test4, "VersionedResource annotation must define media type or version range for resource")
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

class TestMethodClass {

    @VersionedResource
    public void test1() {}

    @VersionedResource(media = "application/vnd.app")
    public void test2() {}

    @VersionedResource(from = "v1.0")
    public void test3() {}

    @VersionedResource(to = "v2.0")
    public void test4() {}

    @VersionedResource(media = "application/vnd.app", from = "v1.0")
    public void test5() {}

    @VersionedResource(from = "v1.0", to = "v2.0")
    public void test6() {}

    @VersionedResource(media = "application/vnd.app", to = "v2.0")
    public void test7() {}

    @VersionedResource(media = "application/vnd.app", from = "v1.0", to = "v2.0")
    public void test8() {}
}