package com.github.vkremianskii.pits.registry.infra;

import com.github.vkremianskii.pits.registry.ApiVersion;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static com.github.vkremianskii.pits.registry.ApiVersion.apiVersion;

@Component
public class StringToApiVersionConverter implements Converter<String, ApiVersion> {

    @Override
    public ApiVersion convert(@NotNull String source) {
        return apiVersion(Integer.parseInt(source));
    }
}
