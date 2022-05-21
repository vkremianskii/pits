package com.github.vkremianskii.pits.frontends;

import javax.swing.*;

public class Application {

    public static void main(String[] args) {
        final var frame = new JFrame("Frontends");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
