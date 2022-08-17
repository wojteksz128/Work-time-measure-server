package net.wojteksz128.worktimemeasureserver.utils.argumentProvider;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import net.wojteksz128.worktimemeasureserver.api.version.IncorrectVersionRangeException;
import net.wojteksz128.worktimemeasureserver.api.version.IncorrectVersionedResourceException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class CreatingCustomMethodConditionNegativeTestArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Method test1;
        Method test9;
        try {
            test1 = TestMethodClass.class.getMethod("test1");
            test9 = TestMethodClass.class.getMethod("test9");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return Stream.of(
                Arguments.of(test1, new IncorrectVersionedResourceException("VersionedResource annotation must define media type or version range for resource")),
                Arguments.of(test9, new IncorrectVersionRangeException("'from' version cannot be greater then 'to' version (from: v2.0, to: v1.0)"))
                        );
    }
}
