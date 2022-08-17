package net.wojteksz128.worktimemeasureserver.utils.argumentProvider;

import java.util.Collections;
import java.util.stream.Stream;

import net.wojteksz128.worktimemeasureserver.api.version.Version;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResource;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResourceRequestCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class CreatingCustomTypeConditionPositiveTestArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(
                        PositiveTypeConditionTestClass1.class,
                        new VersionedResourceRequestCondition("application/vnd.app", Collections.emptyList())
                            ),
                Arguments.of(
                        PositiveTypeConditionTestClass2.class,
                        new VersionedResourceRequestCondition("", "v1.0", Version.MAX_VERSION)
                            ),
                Arguments.of(
                        PositiveTypeConditionTestClass3.class,
                        new VersionedResourceRequestCondition("", Version.MIN_VERSION, "v2.0")
                            ),
                Arguments.of(
                        PositiveTypeConditionTestClass4.class,
                        new VersionedResourceRequestCondition("application/vnd.app", "v1.0", Version.MAX_VERSION)
                            ),
                Arguments.of(
                        PositiveTypeConditionTestClass5.class,
                        new VersionedResourceRequestCondition("", "v1.0", "v2.0")
                            ),
                Arguments.of(
                        PositiveTypeConditionTestClass6.class,
                        new VersionedResourceRequestCondition("application/vnd.app", Version.MIN_VERSION, "v2.0")
                            ),
                Arguments.of(
                        PositiveTypeConditionTestClass7.class,
                        new VersionedResourceRequestCondition("application/vnd.app", "v1.0", "v2.0")
                            )
                        );
    }
}

@VersionedResource(media = "application/vnd.app")
class PositiveTypeConditionTestClass1 {
}

@VersionedResource(from = "v1.0")
class PositiveTypeConditionTestClass2 {
}

@VersionedResource(to = "v2.0")
class PositiveTypeConditionTestClass3 {
}

@VersionedResource(media = "application/vnd.app", from = "v1.0")
class PositiveTypeConditionTestClass4 {
}

@VersionedResource(from = "v1.0", to = "v2.0")
class PositiveTypeConditionTestClass5 {
}

@VersionedResource(media = "application/vnd.app", to = "v2.0")
class PositiveTypeConditionTestClass6 {
}

@VersionedResource(media = "application/vnd.app", from = "v1.0", to = "v2.0")
class PositiveTypeConditionTestClass7 {
}
