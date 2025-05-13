package com.streamTracker.api;

import lombok.NonNull;
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
}
