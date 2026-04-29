package com.scouting.desktopgui;

import com.scouting.desktopgui.client.ScoutingApiClient;
import com.scouting.desktopgui.ui.MainFrame;

import javax.swing.SwingUtilities;

public class DesktopGuiApplication {

    public static void main(String[] args) {
        String gatewayBaseUrl = System.getProperty("gateway.base-url", "http://localhost:8080");
        String playerServiceBaseUrl = System.getProperty("player.base-url", "http://localhost:8081");
        String scoutingServiceBaseUrl = System.getProperty("scouting.base-url", "http://localhost:8082");
        boolean useGateway = Boolean.parseBoolean(System.getProperty("use.gateway", "false"));
        ScoutingApiClient client = new ScoutingApiClient(
                gatewayBaseUrl,
                playerServiceBaseUrl,
                scoutingServiceBaseUrl,
                useGateway
        );

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(client);
            frame.setVisible(true);
        });
    }
}
