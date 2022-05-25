package com.github.vkremianskii.pits.processes.logic.fsm;

import com.github.vkremianskii.pits.processes.logic.MutableHaulCycle;

@FunctionalInterface
public interface HaulCycleFsmSink {

    void append(MutableHaulCycle haulCycle);
}
