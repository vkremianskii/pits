package com.github.vkremianskii.pits.frontends.ui;

import javax.swing.SwingUtilities;

public class ViewUtil {

    public static void uiThread(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    private ViewUtil() {
    }
}
