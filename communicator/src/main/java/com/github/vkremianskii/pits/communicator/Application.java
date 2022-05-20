package com.github.vkremianskii.pits.communicator;

import com.github.vkremianskii.pits.communicator.grpc.EquipmentServiceImpl;
import io.grpc.ServerBuilder;

public class Application {

    public static void main(String[] args) throws Exception {
        ServerBuilder.forPort(8081)
                .addService(new EquipmentServiceImpl())
                .build()
                .start()
                .awaitTermination();
    }
}
