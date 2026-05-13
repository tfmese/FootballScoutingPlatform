package com.scouting.desktopgui.ui;

import com.scouting.desktopgui.client.ScoutingApiClient;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;

public class MainFrame extends JFrame {

    public MainFrame(ScoutingApiClient client) {
        setTitle("Football Scouting Platform");
        setSize(1440, 860);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UiTheme.APP_BG);
        setLayout(new BorderLayout());

        add(createHeroHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        PlayerPanel playerPanel = new PlayerPanel(client);
        ScoutReportPanel scoutReportPanel = new ScoutReportPanel(client);
        tabs.addTab("Players", playerPanel);
        tabs.addTab("Scout Reports", scoutReportPanel);
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == scoutReportPanel) {
                scoutReportPanel.refreshData(true);
            } else if (tabs.getSelectedComponent() == playerPanel) {
                playerPanel.refreshData(true);
            }
        });
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 18, 18, 18));
        tabs.setBackground(UiTheme.APP_BG);

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createHeroHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiTheme.APP_BG);
        header.setBorder(BorderFactory.createEmptyBorder(18, 18, 8, 18));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                BorderFactory.createEmptyBorder(18, 22, 18, 22)
        ));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BorderLayout());
        JLabel title = new JLabel("Football Scouting Platform");
        title.setForeground(UiTheme.TEXT);
        title.setFont(new java.awt.Font("Segoe UI Semibold", java.awt.Font.PLAIN, 26));
        text.add(title, BorderLayout.NORTH);

        card.add(text, BorderLayout.WEST);
        header.add(card, BorderLayout.CENTER);
        return header;
    }
}
