package net.wojteksz128.worktimemeasureserver.utils.argumentProvider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.stream.Stream;

public class CreatingRequestMappingInfoForMethodWithoutVersionedResourceAnnotationArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(WithoutVersionedResourceTestClass.class.getMethod("testMap1"), WithoutVersionedResourceTestClass.class, RequestMappingInfo.paths("/test").methods(RequestMethod.GET).build()),
                Arguments.of(WithoutVersionedResourceTestClass.class.getMethod("testMap2", String.class), WithoutVersionedResourceTestClass.class, RequestMappingInfo.paths("/test").methods(RequestMethod.POST).params("test").build())
        );
    }
}

class WithoutVersionedResourceTestClass {

    @GetMapping("/test")
    public void testMap1() {}

    @SuppressWarnings("unused")
    @PostMapping(value = "/test", params = "test")
    public void testMap2(String test) {}
}