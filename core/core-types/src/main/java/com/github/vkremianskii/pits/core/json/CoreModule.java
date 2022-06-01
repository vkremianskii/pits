package com.github.vkremianskii.pits.core.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.github.vkremianskii.pits.core.json.serializer.MicrotypeSerializer;

import java.util.List;

public class CoreModule extends SimpleModule {

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new SimpleSerializers(List.of(
            new MicrotypeSerializer()
        )));
    }
}
