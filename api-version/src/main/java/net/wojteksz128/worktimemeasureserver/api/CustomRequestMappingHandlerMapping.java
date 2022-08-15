package net.wojteksz128.worktimemeasureserver.api;

import net.wojteksz128.worktimemeasureserver.api.version.IncorrectVersionedResourceException;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResource;
import net.wojteksz128.worktimemeasureserver.api.version.VersionedResourceRequestCondition;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

public class CustomRequestMappingHandlerMapping extends RequestMappingHandlerMapping {

    @Override
    protected RequestCondition<?> getCustomTypeCondition(Class<?> handlerType) {
        VersionedResource typeAnnotation = AnnotationUtils.findAnnotation(handlerType, VersionedResource.class);
        return createCondition(typeAnnotation);
    }

    @Override
    protected RequestCondition<?> getCustomMethodCondition(Method method) {
        VersionedResource methodAnnotation = AnnotationUtils.findAnnotation(method, VersionedResource.class);
        return createCondition(methodAnnotation);
    }

    private RequestCondition<?> createCondition(VersionedResource versionMapping) {
        if (versionMapping == null) {
            return null;
        }
        if (!StringUtils.hasText(versionMapping.media()) && !StringUtils.hasText(versionMapping.from())) {
            throw new IncorrectVersionedResourceException("VersionedResource annotation must define media type or version range for resource");
        }
        return new VersionedResourceRequestCondition(versionMapping.media(), versionMapping.from(), versionMapping.to());
    }
}
