package net.wojteksz128.worktimemeasureserver.web;

import net.wojteksz128.worktimemeasureserver.api.CustomRequestMappingHandlerMapping;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

@Configuration
@ComponentScan(basePackages = {"net.wojteksz128.worktimemeasureserver.web"})
public class WebConfiguration extends WebMvcConfigurationSupport {

    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping(ContentNegotiationManager contentNegotiationManager, FormattingConversionService conversionService, ResourceUrlProvider resourceUrlProvider) {
        final CustomRequestMappingHandlerMapping handlerMapping = new CustomRequestMappingHandlerMapping();
        handlerMapping.setOrder(0);
        handlerMapping.setRemoveSemicolonContent(false);
        handlerMapping.setContentNegotiationManager(contentNegotiationManager);
        return handlerMapping;
    }
}
