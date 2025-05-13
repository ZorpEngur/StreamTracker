package com.streamTracker.api;

import com.streamTracker.ApplicationProperties;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration of REST API.
 */
public class RestConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        WebMvcConfigurer.super.addViewControllers(registry);
        //Redirect from base url to swagger.
        registry.addRedirectViewController("/", "/swagger-ui/index.html");
    }

    @Bean
    @NonNull
    public VodResource vodResource(@NonNull ApplicationProperties properties) {
        return new VodResource(properties);
    }
}
