package com.github.vkremianskii.pits.core.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        CoreExceptionHandler.class,
        RequestLoggingFilter.class})
public class CoreAutoConfiguration {
}
