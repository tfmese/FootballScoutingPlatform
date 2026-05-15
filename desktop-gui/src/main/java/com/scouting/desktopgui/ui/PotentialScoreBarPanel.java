package com.scouting.desktopgui.ui;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Swing: {@link #paintComponent(Graphics)} ile çizilen potansiyel skor çubuğu bileşeni.
 */
public class PotentialScoreBarPanel extends JPanel {

    private int scorePercent;

    public PotentialScoreBarPanel(JTextField scoreField) {
        setPreferredSize(new Dimension(120, 24));
        setOpaque(false);
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                syncFromField(scoreField);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                syncFromField(scoreField);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                syncFromField(scoreField);
            }
        };
        scoreField.getDocument().addDocumentListener(listener);
        syncFromField(scoreField);
    }

    public void setScorePercent(int scorePercent) {
        this.scorePercent = Math.max(0, Math.min(100, scorePercent));
        repaint();
    }

    private void syncFromField(JTextField scoreField) {
        try {
            int v = Integer.parseInt(scoreField.getText().trim());
            setScorePercent(v);
        } catch (NumberFormatException ex) {
            setScorePercent(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int pad = 2;
        g2.setColor(new Color(40, 40, 40));
        g2.fillRoundRect(pad, pad, w - 2 * pad, h - 2 * pad, 8, 8);

        int innerW = w - 2 * pad - 4;
        int fillW = (int) Math.round(innerW * (scorePercent / 100.0));
        Color fill = scorePercent >= 80 ? new Color(46, 204, 113)
                : scorePercent >= 50 ? new Color(241, 196, 15)
                : new Color(231, 76, 60);
        g2.setColor(fill);
        g2.fillRoundRect(pad + 2, pad + 2, Math.max(0, fillW), h - 2 * pad - 4, 6, 6);

        g2.setColor(Color.WHITE);
        g2.drawString(scorePercent + "%", pad + 6, h - pad - 6);
        g2.dispose();
    }
}
