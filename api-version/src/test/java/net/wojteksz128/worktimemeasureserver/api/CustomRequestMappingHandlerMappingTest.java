package net.wojteksz128.worktimemeasureserver.api;

import java.lang.reflect.Method;

import net.wojteksz128.worktimemeasureserver.api.version.VersionedResourceRequestCondition;
import net.wojteksz128.worktimemeasureserver.utils.argumentProvider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import static org.junit.jupiter.api.Assertions.*;

class CustomRequestMappingHandlerMappingTest {

    private CustomRequestMappingHandlerMapping requestMappingHandlerMapping;

    @BeforeEach
    public void before() {
        requestMappingHandlerMapping = new CustomRequestMappingHandlerMapping();
    }

    @ParameterizedTest
    @ArgumentsSource(CreatingCustomTypeConditionPositiveTestArgumentProvider.class)
    public void gettingCustomTypeCondition_returns_expectedRequestCondition(Class<?> webClass, VersionedResourceRequestCondition expectedRequestCondition) {
        RequestCondition<?> actualRequestCondition = requestMappingHandlerMapping.getCustomTypeCondition(webClass);

        assertTrue(actualRequestCondition instanceof VersionedResourceRequestCondition);
        assertEquals(expectedRequestCondition, actualRequestCondition);
    }

    @ParameterizedTest
    @ArgumentsSource(CreatingCustomTypeConditionNegativeTestArgumentProvider.class)
    public void gettingCustomTypeConditionForClassAnnotatedByWrongUsedVersionedResource_throws_illegalStateException(Class<?> webClass, Exception expectedException) {
        Exception actualException = assertThrows(Exception.class, () -> requestMappingHandlerMapping.getCustomTypeCondition(webClass));

        assertEquals(expectedException.getClass(), actualException.getClass());
        assertEquals(expectedException.getMessage(), actualException.getMessage());
    }

    @ParameterizedTest
    @ArgumentsSource(CreatingCustomMethodConditionPositiveTestArgumentProvider.class)
    public void gettingCustomMethodCondition_returns_expectedRequestCondition(Method method, VersionedResourceRequestCondition expectedRequestCondition) {
        RequestCondition<?> actualRequestCondition = requestMappingHandlerMapping.getCustomMethodCondition(method);

        assertTrue(actualRequestCondition instanceof VersionedResourceRequestCondition);
        assertEquals(expectedRequestCondition, actualRequestCondition);
    }

    @ParameterizedTest
    @ArgumentsSource(CreatingCustomMethodConditionNegativeTestArgumentProvider.class)
    public void gettingCustomMethodConditionForClassAnnotatedByWrongUsedVersionedResource_throws_illegalStateException(Method method, Exception expectedException) {
        Exception actualException = assertThrows(Exception.class, () -> requestMappingHandlerMapping.getCustomMethodCondition(method));

        assertEquals(expectedException.getClass(), actualException.getClass());
        assertEquals(expectedException.getMessage(), actualException.getMessage());
    }

    @ParameterizedTest
    @ArgumentsSource(CreatingRequestMappingInfoForMethodWithoutVersionedResourceAnnotationArgumentProvider.class)
    public void creatingRequestMappingInfoWithoutVersionedResourceAnnotation_creates_requestMappingInfoWithoutVersionedResourceRequestCondition(Method method, Class<?> handlerClass, RequestMappingInfo expectedRequestMappingInfo) {
        RequestMappingInfo actualRequestMappingInfo = requestMappingHandlerMapping.getMappingForMethod(method, handlerClass);

        assertNotNull(actualRequestMappingInfo);
        assertEquals(expectedRequestMappingInfo, actualRequestMappingInfo);
        assertNull(actualRequestMappingInfo.getCustomCondition());
    }

    @ParameterizedTest
    @ArgumentsSource(CreatingRequestMappingInfoForAnnotatedVersionedResourceAnnotationArgumentProvider.class)
    public void creatingRequestMappingInfoForAnnotatedVersionedResource_creates_requestMappingInfoWithVersionedResourceRequestCondition(Method method, Class<?> handlerClass, RequestMappingInfo expectedRequestMappingInfo, VersionedResourceRequestCondition expectedVersionedResourceRequestCondition) {
        RequestMappingInfo actualRequestMappingInfo = requestMappingHandlerMapping.getMappingForMethod(method, handlerClass);

        assertNotNull(actualRequestMappingInfo);
        assertEquals(expectedRequestMappingInfo, actualRequestMappingInfo);
        assertEquals(expectedVersionedResourceRequestCondition, actualRequestMappingInfo.getCustomCondition());
    }
}
