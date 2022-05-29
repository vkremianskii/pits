package com.github.vkremianskii.pits.frontends.ui;

import javax.swing.SwingUtilities;

public class ViewUtils {

    private ViewUtils() {
    }

    public static void uiThread(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }
}
