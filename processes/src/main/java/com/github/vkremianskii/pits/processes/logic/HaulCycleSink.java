package com.github.vkremianskii.pits.processes.logic;

@FunctionalInterface
public interface HaulCycleSink {
    void append(MutableHaulCycle haulCycle);
}
