package com.scouting.desktopgui.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

final class UiTheme {

    static final Color APP_BG = new Color(241, 245, 249);
    static final Color CARD_BG = Color.WHITE;
    static final Color CARD_ALT = new Color(248, 250, 252);
    static final Color TEXT = new Color(15, 23, 42);
    static final Color MUTED = new Color(100, 116, 139);
    static final Color ACCENT = new Color(14, 116, 144);
    static final Color SUCCESS = new Color(22, 163, 74);
    static final Color WARNING = new Color(217, 119, 6);
    static final Color DANGER = new Color(220, 38, 38);
    static final Color BORDER = new Color(203, 213, 225);

    private UiTheme() {
    }

    static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(compoundPadding(BorderFactory.createLineBorder(BORDER), 16));
        return panel;
    }

    static JPanel createSoftCard() {
        JPanel panel = createCard();
        panel.setBackground(CARD_ALT);
        return panel;
    }

    static Border compoundPadding(Border outer, int padding) {
        return BorderFactory.createCompoundBorder(
                outer,
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        );
    }

    static void stylePanel(JPanel panel) {
        panel.setBackground(APP_BG);
    }

    static void styleField(JTextField field) {
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(compoundPadding(BorderFactory.createLineBorder(BORDER), 8));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(220, 38));
    }

    static void styleTextArea(JTextArea area) {
        area.setBackground(Color.WHITE);
        area.setForeground(TEXT);
        area.setCaretColor(TEXT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(compoundPadding(BorderFactory.createLineBorder(BORDER), 10));
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    static void styleButton(JButton button, Color background) {
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        button.setMargin(new Insets(10, 14, 10, 14));
        button.setPreferredSize(new Dimension(120, 38));
        button.setBorder(new LineBorder(background.darker(), 1, true));
    }

    static void styleCombo(JComboBox<?> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setPreferredSize(new Dimension(220, 38));
    }

    static Color adjust(Color color, int delta) {
        return new Color(
                Math.max(0, Math.min(255, color.getRed() + delta)),
                Math.max(0, Math.min(255, color.getGreen() + delta)),
                Math.max(0, Math.min(255, color.getBlue() + delta))
        );
    }

    static void styleLabel(JLabel label, boolean secondary) {
        label.setForeground(secondary ? MUTED : TEXT);
        label.setFont(new Font("Segoe UI", secondary ? Font.PLAIN : Font.BOLD, secondary ? 12 : 13));
    }

    static JLabel metricValue(String value, Color color) {
        JLabel label = new JLabel(value);
        label.setForeground(color);
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 24));
        return label;
    }

    static JLabel metricCaption(String text) {
        JLabel label = new JLabel(text);
        styleLabel(label, true);
        return label;
    }

    static void styleTable(JTable table, JScrollPane scrollPane) {
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(226, 232, 240));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(224, 242, 254));
        table.setSelectionForeground(TEXT);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 12));
        header.setBackground(new Color(226, 232, 240));
        header.setForeground(TEXT);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(800, 300));
    }

    static void styleReadOnly(JComponent component) {
        component.setBackground(new Color(248, 250, 252));
        component.setForeground(TEXT);
    }
}
