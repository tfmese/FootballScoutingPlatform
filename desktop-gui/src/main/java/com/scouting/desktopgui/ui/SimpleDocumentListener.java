package com.scouting.desktopgui.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@FunctionalInterface
interface SimpleDocumentListener extends DocumentListener {

    void update();

    static SimpleDocumentListener onChange(Runnable runnable) {
        return runnable::run;
    }

    @Override
    default void insertUpdate(DocumentEvent e) {
        update();
    }

    @Override
    default void removeUpdate(DocumentEvent e) {
        update();
    }

    @Override
    default void changedUpdate(DocumentEvent e) {
        update();
    }
}
