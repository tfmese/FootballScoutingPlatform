package com.scouting.desktopgui.ui;

import com.scouting.desktopgui.client.ScoutingApiClient;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    public MainFrame(ScoutingApiClient client) {
        setTitle("Football Scouting Platform");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Players", new PlayerPanel(client));
        tabs.addTab("Scout Reports", new ScoutReportPanel(client));

        add(tabs, BorderLayout.CENTER);
    }
}
