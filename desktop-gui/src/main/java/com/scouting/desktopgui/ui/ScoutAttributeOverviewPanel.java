package com.scouting.desktopgui.ui;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ScoutAttributeOverviewPanel extends JPanel {

    private int technicalScore;
    private int physicalScore;
    private int tacticalScore;
    private int mentalScore;

    public ScoutAttributeOverviewPanel() {
        setOpaque(false);
        setPreferredSize(new Dimension(340, 200));
        setMinimumSize(new Dimension(300, 190));
    }

    public void updateScores(int technicalScore, int physicalScore, int tacticalScore, int mentalScore) {
        this.technicalScore = technicalScore;
        this.physicalScore = physicalScore;
        this.tacticalScore = tacticalScore;
        this.mentalScore = mentalScore;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cardX = 10;
        int cardY = 10;
        int cardW = getWidth() - 20;
        int cardH = getHeight() - 20;
        g2.setColor(new Color(248, 250, 252));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 18, 18);
        g2.setColor(new Color(226, 232, 240));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 18, 18);

        g2.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        g2.setColor(UiTheme.TEXT);
        g2.drawString("Scout Attribute Snapshot", cardX + 16, cardY + 26);

        drawMetric(g2, "Technical", technicalScore, new Color(14, 116, 144), cardX + 16, cardY + 52, cardW - 32);
        drawMetric(g2, "Physical", physicalScore, new Color(217, 119, 6), cardX + 16, cardY + 86, cardW - 32);
        drawMetric(g2, "Tactical", tacticalScore, new Color(37, 99, 235), cardX + 16, cardY + 120, cardW - 32);
        drawMetric(g2, "Mental", mentalScore, new Color(126, 34, 206), cardX + 16, cardY + 154, cardW - 32);
        g2.dispose();
    }

    private void drawMetric(Graphics2D g2, String label, int score, Color fill, int x, int y, int width) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(UiTheme.MUTED);
        g2.drawString(label, x, y);
        g2.setColor(UiTheme.TEXT);
        g2.drawString(score + "/100", x + width - 54, y);

        int barY = y + 8;
        int barW = width;
        g2.setColor(new Color(226, 232, 240));
        g2.fillRoundRect(x, barY, barW, 12, 12, 12);
        g2.setColor(fill);
        g2.fillRoundRect(x, barY, (int) Math.round(barW * (Math.max(0, Math.min(100, score)) / 100.0)), 12, 12, 12);
        g2.setStroke(new BasicStroke(1f));
    }
}
