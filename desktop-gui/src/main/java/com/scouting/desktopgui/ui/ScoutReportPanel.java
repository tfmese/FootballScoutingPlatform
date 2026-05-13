package com.scouting.desktopgui.ui;

import com.scouting.desktopgui.client.ScoutingApiClient;
import com.scouting.desktopgui.model.Player;
import com.scouting.desktopgui.model.ScoutReport;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.Objects;

public class ScoutReportPanel extends JPanel {
    private final ScoutingApiClient client;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField idField;
    private final JComboBox<PlayerChoice> playerCombo;
    private final JTextField playerNameField;
    private final JTextField positionField;
    private final JTextField playerAgeField;
    private final JTextField clubField;
    private final JTextField technicalScoreField;
    private final JTextField physicalScoreField;
    private final JTextField tacticalScoreField;
    private final JTextField mentalScoreField;
    private final JTextField overallScoreField;
    private final JTextField expectedFeeField;
    private final JComboBox<String> recommendationCombo;
    private final JTextArea notesArea;
    private final JLabel statusLabel;
    private final JLabel reportCountValue;
    private final JLabel averagePotentialValue;
    private final ScoutAttributeOverviewPanel overviewPanel;
    private final TableRowSorter<DefaultTableModel> rowSorter;
    private final JTextField searchField;
    private boolean suppressPlayerSelectionEvents;

    public ScoutReportPanel(ScoutingApiClient client) {
        this.client = client;
        setLayout(new BorderLayout(16, 16));
        UiTheme.stylePanel(this);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(new String[]{"ID", "Player", "Position", "Age", "Overall", "Fee", "Recommendation"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rowSorter = new TableRowSorter<>(tableModel);
        rowSorter.setComparator(5, (left, right) -> Long.compare(
                parseExpectedFeeValue(String.valueOf(left)),
                parseExpectedFeeValue(String.valueOf(right))
        ));
        table.setRowSorter(rowSorter);

        JScrollPane tableScroll = new JScrollPane(table);
        UiTheme.styleTable(table, tableScroll);

        idField = new JTextField();
        playerCombo = new JComboBox<>();
        playerNameField = new JTextField();
        positionField = new JTextField();
        playerAgeField = new JTextField();
        clubField = new JTextField();
        technicalScoreField = new JTextField("75");
        physicalScoreField = new JTextField("75");
        tacticalScoreField = new JTextField("75");
        mentalScoreField = new JTextField("75");
        overallScoreField = new JTextField("75");
        expectedFeeField = new JTextField("0");
        recommendationCombo = new JComboBox<>(new String[]{"Sign", "Monitor", "Develop", "Reject"});
        UiTheme.styleCombo(recommendationCombo);
        notesArea = new JTextArea(6, 20);
        statusLabel = new JLabel("Ready");
        reportCountValue = UiTheme.metricValue("0", UiTheme.SUCCESS);
        averagePotentialValue = UiTheme.metricValue("0", UiTheme.ACCENT);
        overviewPanel = new ScoutAttributeOverviewPanel();
        searchField = new JTextField();
        UiTheme.styleField(searchField);
        searchField.setToolTipText("Ara...");
        searchField.setPreferredSize(new Dimension(280, 38));

        styleFields();

        JPanel top = new JPanel(new BorderLayout(16, 16));
        top.setOpaque(false);
        top.add(createHeader(), BorderLayout.NORTH);
        top.add(createMetrics(), BorderLayout.CENTER);

        JPanel center = UiTheme.createCard();
        center.setLayout(new BorderLayout(12, 12));
        center.add(createTableHeader(), BorderLayout.NORTH);
        center.add(tableScroll, BorderLayout.CENTER);

        JPanel right = createWorkspace();
        right.setPreferredSize(new Dimension(430, 0));

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        playerCombo.addActionListener(e -> syncFieldsFromSelectedPlayer());
        technicalScoreField.getDocument().addDocumentListener(SimpleDocumentListener.onChange(this::updateDerivedScore));
        physicalScoreField.getDocument().addDocumentListener(SimpleDocumentListener.onChange(this::updateDerivedScore));
        tacticalScoreField.getDocument().addDocumentListener(SimpleDocumentListener.onChange(this::updateDerivedScore));
        mentalScoreField.getDocument().addDocumentListener(SimpleDocumentListener.onChange(this::updateDerivedScore));
        searchField.getDocument().addDocumentListener(SimpleDocumentListener.onChange(this::applySearchFilter));
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());

        refreshData(true);
    }

    public void refreshData(boolean silentError) {
        reloadPlayerCombo();
        refreshReports(silentError);
    }

    private void styleFields() {
        for (JTextField field : List.of(idField, playerNameField, positionField, playerAgeField, clubField, technicalScoreField, physicalScoreField, tacticalScoreField, mentalScoreField, overallScoreField, expectedFeeField)) {
            UiTheme.styleField(field);
        }
        UiTheme.styleCombo(playerCombo);
        UiTheme.styleTextArea(notesArea);
        idField.setEditable(false);
        overallScoreField.setEditable(false);
        playerAgeField.setEditable(false);
        clubField.setEditable(false);
        UiTheme.styleReadOnly(idField);
        UiTheme.styleReadOnly(overallScoreField);
        UiTheme.styleReadOnly(playerAgeField);
        UiTheme.styleReadOnly(clubField);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        JLabel title = new JLabel("Scouting Intelligence Desk");
        title.setFont(new java.awt.Font("Segoe UI Semibold", java.awt.Font.PLAIN, 28));
        title.setForeground(UiTheme.TEXT);
        panel.add(title);
        return panel;
    }

    private JPanel createMetrics() {
        JPanel metrics = new JPanel(new GridLayout(1, 2, 12, 12));
        metrics.setOpaque(false);

        JPanel reportsCard = UiTheme.createCard();
        reportsCard.setLayout(new BorderLayout(0, 6));
        reportsCard.add(reportCountValue, BorderLayout.CENTER);
        reportsCard.add(UiTheme.metricCaption("Scout reports"), BorderLayout.SOUTH);

        JPanel overallCard = UiTheme.createSoftCard();
        overallCard.setLayout(new BorderLayout(0, 6));
        overallCard.add(averagePotentialValue, BorderLayout.CENTER);
        overallCard.add(UiTheme.metricCaption("Average overall"), BorderLayout.SOUTH);

        metrics.add(reportsCard);
        metrics.add(overallCard);
        return metrics;
    }

    private JPanel createTableHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Report Queue");
        UiTheme.styleLabel(title, false);
        titleBlock.add(title);
        header.add(titleBlock, BorderLayout.WEST);
        header.add(searchField, BorderLayout.CENTER);
        return header;
    }

    private JPanel createWorkspace() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setOpaque(false);

        JPanel card = UiTheme.createCard();
        card.setLayout(new BorderLayout(12, 12));

        JPanel title = new JPanel();
        title.setOpaque(false);
        JLabel formTitle = new JLabel("Scout Workspace");
        UiTheme.styleLabel(formTitle, false);
        title.add(formTitle);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.add(labeledField("Report ID", idField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledCombo("Linked Player", playerCombo));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledField("Player Name", playerNameField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledField("Position", positionField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledField("Age", playerAgeField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledField("Club", clubField));
        form.add(Box.createVerticalStrut(10));
        form.add(scoreGrid());
        form.add(Box.createVerticalStrut(10));
        form.add(labeledField("Overall Potential", overallScoreField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledField("Expected Fee (EUR)", expectedFeeField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledCombo("Recommendation", recommendationCombo));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledTextArea("Scout Notes", notesArea));

        JPanel actions = new JPanel(new GridLayout(2, 3, 8, 8));
        actions.setOpaque(false);
        JButton refreshButton = new JButton("Refresh");
        JButton createButton = new JButton("Create");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        UiTheme.styleButton(refreshButton, UiTheme.ACCENT);
        UiTheme.styleButton(createButton, UiTheme.SUCCESS);
        UiTheme.styleButton(updateButton, new java.awt.Color(37, 99, 235));
        UiTheme.styleButton(deleteButton, UiTheme.DANGER);
        UiTheme.styleButton(clearButton, UiTheme.adjust(UiTheme.MUTED, -20));

        refreshButton.addActionListener(e -> refreshData(false));
        createButton.addActionListener(e -> createReport());
        updateButton.addActionListener(e -> updateReport());
        deleteButton.addActionListener(e -> deleteReport());
        clearButton.addActionListener(e -> clearForm());

        actions.add(refreshButton);
        actions.add(createButton);
        actions.add(updateButton);
        actions.add(deleteButton);
        actions.add(clearButton);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(form);
        center.add(Box.createVerticalStrut(12));
        center.add(overviewPanel);
        center.add(Box.createVerticalStrut(12));
        center.add(actions);

        card.add(title, BorderLayout.NORTH);
        card.add(center, BorderLayout.CENTER);

        UiTheme.styleLabel(statusLabel, true);
        JScrollPane formScroll = new JScrollPane(card);
        formScroll.setBorder(null);
        formScroll.getViewport().setBackground(UiTheme.APP_BG);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(formScroll, BorderLayout.CENTER);
        wrapper.add(statusLabel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel scoreGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.setOpaque(false);
        grid.add(labeledField("Technical", technicalScoreField));
        grid.add(labeledField("Physical", physicalScoreField));
        grid.add(labeledField("Tactical", tacticalScoreField));
        grid.add(labeledField("Mental", mentalScoreField));
        return grid;
    }

    private JPanel labeledField(String labelText, JTextField field) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel label = new JLabel(labelText);
        UiTheme.styleLabel(label, false);
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel labeledCombo(String labelText, JComboBox<?> comboBox) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel label = new JLabel(labelText);
        UiTheme.styleLabel(label, false);
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(comboBox, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel labeledTextArea(String labelText, JTextArea area) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
        JLabel label = new JLabel(labelText);
        UiTheme.styleLabel(label, false);
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER));
        scrollPane.setPreferredSize(new Dimension(200, 140));
        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private void applySearchFilter() {
        String query = searchField.getText().trim();
        if (query.isBlank()) {
            rowSorter.setRowFilter(null);
            return;
        }
        rowSorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query), 1, 2, 4, 5));
    }

    private void reloadPlayerCombo() {
        try {
            String previousId = selectedPlayerId();
            suppressPlayerSelectionEvents = true;
            playerCombo.removeAllItems();
            playerCombo.addItem(PlayerChoice.manualEntry());
            for (Player player : client.getPlayers()) {
                String label = player.getName() + " | " + player.getClub() + " | " + player.getPosition() + " | " + player.getAge();
                playerCombo.addItem(new PlayerChoice(player.getId(), player.getName(), player.getPosition(), player.getAge(), player.getClub(), label));
            }
            selectPlayerInCombo(previousId);
            syncFieldsFromSelectedPlayer();
        } catch (Exception ex) {
            statusLabel.setText("Player list could not be loaded: " + ex.getMessage());
        } finally {
            suppressPlayerSelectionEvents = false;
        }
    }

    private String selectedPlayerId() {
        PlayerChoice choice = (PlayerChoice) playerCombo.getSelectedItem();
        return choice == null ? null : choice.playerId;
    }

    private void selectPlayerInCombo(String playerId) {
        if (playerId == null || playerId.isBlank()) {
            playerCombo.setSelectedIndex(0);
            return;
        }
        for (int i = 0; i < playerCombo.getItemCount(); i++) {
            PlayerChoice c = playerCombo.getItemAt(i);
            if (c != null && Objects.equals(playerId, c.playerId)) {
                playerCombo.setSelectedIndex(i);
                return;
            }
        }
        playerCombo.setSelectedIndex(0);
    }

    private void syncFieldsFromSelectedPlayer() {
        if (suppressPlayerSelectionEvents) {
            return;
        }
        PlayerChoice choice = (PlayerChoice) playerCombo.getSelectedItem();
        boolean manualMode = choice == null || choice.playerId == null;
        playerNameField.setEditable(manualMode);
        positionField.setEditable(manualMode);
        if (manualMode) {
            if (table.getSelectedRow() < 0) {
                playerNameField.setText("");
                positionField.setText("");
                playerAgeField.setText("");
                clubField.setText("");
            }
            return;
        }
        playerNameField.setText(choice.playerName);
        positionField.setText(choice.position);
        playerAgeField.setText(String.valueOf(choice.playerAge));
        clubField.setText(choice.club);
    }

    private void refreshReports(boolean silentError) {
        try {
            List<ScoutReport> reports = client.getScoutReports();
            tableModel.setRowCount(0);
            int totalOverall = 0;
            for (ScoutReport report : reports) {
                tableModel.addRow(new Object[]{
                        report.getId(),
                        report.getPlayerName(),
                        report.getPosition(),
                        resolveDisplayAge(report),
                        report.getPotentialScore(),
                        formatExpectedFee(report.getExpectedFee()),
                        report.getRecommendation(),
                });
                totalOverall += report.getPotentialScore();
            }
            reportCountValue.setText(String.valueOf(reports.size()));
            averagePotentialValue.setText(reports.isEmpty() ? "0" : String.valueOf(Math.round(totalOverall / (double) reports.size())));
            statusLabel.setText("Scouting queue synced. " + reports.size() + " reports loaded.");
        } catch (Exception ex) {
            statusLabel.setText("Connection issue: " + ex.getMessage());
            if (!silentError) {
                showError(ex);
            }
        }
    }

    private void createReport() {
        try {
            validateScoutReportInput();
            client.createScoutReport(
                    selectedPlayerId(),
                    playerNameField.getText().trim(),
                    positionField.getText().trim(),
                    Integer.parseInt(playerAgeField.getText().trim()),
                    parseScore(technicalScoreField),
                    parseScore(physicalScoreField),
                    parseScore(tacticalScoreField),
                    parseScore(mentalScoreField),
                    parseExpectedFee(),
                    String.valueOf(recommendationCombo.getSelectedItem()),
                    notesArea.getText().trim()
            );
            refreshReports(false);
            clearForm();
            JOptionPane.showMessageDialog(this, "Scout report created.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updateReport() {
        try {
            validateScoutReportInput();
            client.updateScoutReport(
                    idField.getText().trim(),
                    selectedPlayerId(),
                    playerNameField.getText().trim(),
                    positionField.getText().trim(),
                    Integer.parseInt(playerAgeField.getText().trim()),
                    parseScore(technicalScoreField),
                    parseScore(physicalScoreField),
                    parseScore(tacticalScoreField),
                    parseScore(mentalScoreField),
                    parseExpectedFee(),
                    String.valueOf(recommendationCombo.getSelectedItem()),
                    notesArea.getText().trim()
            );
            refreshReports(false);
            JOptionPane.showMessageDialog(this, "Scout report updated.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteReport() {
        try {
            client.deleteScoutReport(idField.getText().trim());
            refreshReports(false);
            clearForm();
            JOptionPane.showMessageDialog(this, "Scout report removed.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void fillFormFromSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        int row = table.convertRowIndexToModel(viewRow);
        String reportId = String.valueOf(tableModel.getValueAt(row, 0));
        try {
            ScoutReport report = client.getScoutReports().stream()
                    .filter(item -> Objects.equals(item.getId(), reportId))
                    .findFirst()
                    .orElse(null);
            if (report == null) {
                return;
            }
            idField.setText(report.getId());
            selectPlayerInCombo(report.getPlayerId());
            playerNameField.setText(report.getPlayerName());
            positionField.setText(report.getPosition());
            playerAgeField.setText(String.valueOf(resolveDisplayAge(report)));
            technicalScoreField.setText(String.valueOf(report.getTechnicalScore()));
            physicalScoreField.setText(String.valueOf(report.getPhysicalScore()));
            tacticalScoreField.setText(String.valueOf(report.getTacticalScore()));
            mentalScoreField.setText(String.valueOf(report.getMentalScore()));
            overallScoreField.setText(String.valueOf(report.getPotentialScore()));
            expectedFeeField.setText(formatExpectedFee(report.getExpectedFee()));
            recommendationCombo.setSelectedItem(report.getRecommendation());
            notesArea.setText(report.getNotes());
            syncFieldsFromSelectedPlayer();
            updateDerivedScore();
            statusLabel.setText("Selected report for " + report.getPlayerName());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void clearForm() {
        idField.setText("");
        playerCombo.setSelectedIndex(0);
        playerAgeField.setText("");
        clubField.setText("");
        technicalScoreField.setText("75");
        physicalScoreField.setText("75");
        tacticalScoreField.setText("75");
        mentalScoreField.setText("75");
        expectedFeeField.setText("0");
        recommendationCombo.setSelectedItem("Monitor");
        notesArea.setText("");
        table.clearSelection();
        syncFieldsFromSelectedPlayer();
        updateDerivedScore();
        statusLabel.setText("Workspace reset. Ready for a fresh report.");
    }

    private void updateDerivedScore() {
        int technical = safeScore(technicalScoreField);
        int physical = safeScore(physicalScoreField);
        int tactical = safeScore(tacticalScoreField);
        int mental = safeScore(mentalScoreField);
        int overall = Math.round((technical + physical + tactical + mental) / 4.0f);
        overallScoreField.setText(String.valueOf(overall));
        overviewPanel.updateScores(technical, physical, tactical, mental);
    }

    private int safeScore(JTextField field) {
        try {
            return clamp(Integer.parseInt(field.getText().trim()));
        } catch (Exception ex) {
            return 0;
        }
    }

    private int parseScore(JTextField field) {
        int value = Integer.parseInt(field.getText().trim());
        if (value < 1 || value > 100) {
            throw new IllegalArgumentException("All attribute scores must be between 1 and 100.");
        }
        return value;
    }

    private long parseExpectedFee() {
        String raw = expectedFeeField.getText().trim();
        if (raw.isBlank()) {
            throw new IllegalArgumentException("Expected fee cannot be empty.");
        }
        return parseExpectedFeeValue(raw);
    }

    private long parseExpectedFeeValue(String raw) {
        String normalized = raw.toLowerCase()
                .replace("eur", "")
                .replace("euro", "")
                .replace("euros", "")
                .replace("€", "")
                .replace("_", "")
                .trim();

        long multiplier = 1L;
        if (normalized.endsWith("milyon")) {
            multiplier = 1_000_000L;
            normalized = normalized.substring(0, normalized.length() - "milyon".length()).trim();
        } else if (normalized.endsWith("million")) {
            multiplier = 1_000_000L;
            normalized = normalized.substring(0, normalized.length() - "million".length()).trim();
        } else if (normalized.endsWith("mil")) {
            multiplier = 1_000_000L;
            normalized = normalized.substring(0, normalized.length() - "mil".length()).trim();
        } else if (normalized.endsWith("mn")) {
            multiplier = 1_000_000L;
            normalized = normalized.substring(0, normalized.length() - "mn".length()).trim();
        } else if (normalized.endsWith("m")) {
            multiplier = 1_000_000L;
            normalized = normalized.substring(0, normalized.length() - 1).trim();
        } else if (normalized.endsWith("bin")) {
            multiplier = 1_000L;
            normalized = normalized.substring(0, normalized.length() - "bin".length()).trim();
        } else if (normalized.endsWith("k")) {
            multiplier = 1_000L;
            normalized = normalized.substring(0, normalized.length() - 1).trim();
        }

        String numericPart = normalized.replace(" ", "").replace(",", ".");
        double parsed;
        try {
            parsed = Double.parseDouble(numericPart);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Expected fee formatini ornek olarak 13000000, 13 mil, 13m veya 750k seklinde gir.");
        }

        long value = Math.round(parsed * multiplier);
        if (value < 0) {
            throw new IllegalArgumentException("Expected fee cannot be negative.");
        }
        return value;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private void validateScoutReportInput() {
        if (playerNameField.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
        if (positionField.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Position cannot be empty.");
        }
        parseScore(technicalScoreField);
        parseScore(physicalScoreField);
        parseScore(tacticalScoreField);
        parseScore(mentalScoreField);
        if (playerAgeField.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Player age cannot be empty.");
        }
        parseExpectedFee();
        if (notesArea.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Scout note cannot be empty.");
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String formatExpectedFee(long expectedFee) {
        if (expectedFee >= 1_000_000L && expectedFee % 1_000_000L == 0) {
            return (expectedFee / 1_000_000L) + " mil";
        }
        if (expectedFee >= 1_000L && expectedFee % 1_000L == 0) {
            return (expectedFee / 1_000L) + "k";
        }
        return String.valueOf(expectedFee);
    }

    private int resolveDisplayAge(ScoutReport report) {
        if (report.getPlayerAge() > 0) {
            return report.getPlayerAge();
        }
        String playerId = report.getPlayerId();
        if (playerId == null || playerId.isBlank()) {
            return 0;
        }
        for (int i = 0; i < playerCombo.getItemCount(); i++) {
            PlayerChoice choice = playerCombo.getItemAt(i);
            if (choice != null && Objects.equals(playerId, choice.playerId) && choice.playerAge > 0) {
                return choice.playerAge;
            }
        }
        return 0;
    }

    private static final class PlayerChoice {
        private final String playerId;
        private final String playerName;
        private final String position;
        private final int playerAge;
        private final String club;
        private final String label;

        private PlayerChoice(String playerId, String playerName, String position, int playerAge, String club, String label) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.position = position;
            this.playerAge = playerAge;
            this.club = club;
            this.label = label;
        }

        private static PlayerChoice manualEntry() {
            return new PlayerChoice(null, null, null, 0, "", "Manual scouting target");
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
