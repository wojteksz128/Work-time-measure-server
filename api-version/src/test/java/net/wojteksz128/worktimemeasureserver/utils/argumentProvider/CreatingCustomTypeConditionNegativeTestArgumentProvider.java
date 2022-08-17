package net.wojteksz128.worktimemeasureserver.utils.argumentProvider;

import java.util.stream.Stream;

import net.wojteksz128.worktimemeasureserver.api.version.IncorrectVersionRangeException;
import net.wojteksz128.worktimemeasureserver.api.version.IncorrectVersionedResourceException;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResource;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class CreatingCustomTypeConditionNegativeTestArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.of(
                Arguments.of(NegativeTypeConditionTestClass1.class, new IncorrectVersionedResourceException("VersionedResource annotation must define media type or version range for resource")),
                Arguments.of(NegativeTypeConditionTestClass2.class, new IncorrectVersionRangeException("'from' version cannot be greater then 'to' version (from: v2.0, to: v1.0)"))
                        );
    }
}

@VersionedResource
class NegativeTypeConditionTestClass1 {
}

@VersionedResource(media = "application/vnd.app", from = "v2.0", to = "v1.0")
class NegativeTypeConditionTestClass2 {
}