package com.github.vkremianskii.pits.core.data;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TransactionalJooq.class)
public class CoreDataAutoConfiguration {

}
