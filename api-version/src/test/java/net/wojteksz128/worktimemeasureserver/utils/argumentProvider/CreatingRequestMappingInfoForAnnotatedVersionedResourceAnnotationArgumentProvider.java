package net.wojteksz128.worktimemeasureserver.utils.argumentProvider;

import net.wojteksz128.worktimemeasureserver.api.version.Version;
import net.wojteksz128.worktimemeasureserver.api.version.VersionRange;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResource;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResourceRequestCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.List;
import java.util.stream.Stream;

public class CreatingRequestMappingInfoForAnnotatedVersionedResourceAnnotationArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        VersionedResourceRequestCondition versionedResourceRequestCondition = new VersionedResourceRequestCondition("application/vnd.app", List.of(new VersionRange("v1.0", Version.MAX_VERSION)));
        return Stream.of(
                Arguments.of(OnlyClassVersionedResourceTestClass.class.getMethod("testMap1"), OnlyClassVersionedResourceTestClass.class, RequestMappingInfo.paths("/test").methods(RequestMethod.GET).customCondition(versionedResourceRequestCondition).build(), versionedResourceRequestCondition),
                Arguments.of(OnlyMethodVersionedResourceTestClass.class.getMethod("testMap1"), OnlyMethodVersionedResourceTestClass.class, RequestMappingInfo.paths("/test").methods(RequestMethod.GET).customCondition(versionedResourceRequestCondition).build(), versionedResourceRequestCondition),
                Arguments.of(BothVersionedResourceTestClass.class.getMethod("testMap1"), BothVersionedResourceTestClass.class, RequestMappingInfo.paths("/test").methods(RequestMethod.GET).customCondition(versionedResourceRequestCondition).build(), versionedResourceRequestCondition)
        );
    }
}

@VersionedResource(media = "application/vnd.app", from = "v1.0")
class OnlyClassVersionedResourceTestClass {

    @GetMapping("/test")
    public void testMap1() {}
}

class OnlyMethodVersionedResourceTestClass {

    @GetMapping("/test")
    @VersionedResource(media = "application/vnd.app", from = "v1.0")
    public void testMap1() {}
}

@VersionedResource(media = "application/vnd.app")
class BothVersionedResourceTestClass {

    @GetMapping("/test")
    @VersionedResource(from = "v1.0")
    public void testMap1() {}
}