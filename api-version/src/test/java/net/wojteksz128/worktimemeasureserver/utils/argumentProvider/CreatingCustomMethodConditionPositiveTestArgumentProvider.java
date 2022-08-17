package net.wojteksz128.worktimemeasureserver.utils.argumentProvider;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.stream.Stream;

import net.wojteksz128.worktimemeasureserver.api.version.Version;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResource;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResourceRequestCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class CreatingCustomMethodConditionPositiveTestArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Method test2;
        Method test3;
        Method test4;
        Method test5;
        Method test6;
        Method test7;
        Method test8;
        try {
            test2 = TestMethodClass.class.getMethod("test2");
            test3 = TestMethodClass.class.getMethod("test3");
            test4 = TestMethodClass.class.getMethod("test4");
            test5 = TestMethodClass.class.getMethod("test5");
            test6 = TestMethodClass.class.getMethod("test6");
            test7 = TestMethodClass.class.getMethod("test7");
            test8 = TestMethodClass.class.getMethod("test8");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return Stream.of(
                Arguments.of(test2, new VersionedResourceRequestCondition("application/vnd.app", Collections.emptyList())),
                Arguments.of(test3, new VersionedResourceRequestCondition("", "v1.0", Version.MAX_VERSION)),
                Arguments.of(test4, new VersionedResourceRequestCondition("", "v0.0", "v2.0")),
                Arguments.of(test5, new VersionedResourceRequestCondition("application/vnd.app", "v1.0", Version.MAX_VERSION)),
                Arguments.of(test6, new VersionedResourceRequestCondition("", "v1.0", "v2.0")),
                Arguments.of(test7, new VersionedResourceRequestCondition("application/vnd.app", Version.MIN_VERSION, "v2.0")),
                Arguments.of(test8, new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2.0"))
                        );
    }
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

    @VersionedResource(media = "application/vnd.app", from = "v2.0", to = "v1.0")
    public void test9() {}
}