package com.scouting.desktopgui.ui;

import com.scouting.desktopgui.client.ScoutingApiClient;
import com.scouting.desktopgui.model.Player;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

public class PlayerPanel extends JPanel {
    private final ScoutingApiClient client;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private JTextField searchField;
    private JTextField idField;
    private JTextField nameField;
    private JTextField positionField;
    private JTextField ageField;
    private JTextField clubField;
    private JComboBox<String> preferredFootCombo;
    private JLabel statusLabel;
    private JLabel playerCountValue;
    private JLabel averageAgeValue;
    private final TableRowSorter<DefaultTableModel> rowSorter;

    public PlayerPanel(ScoutingApiClient client) {
        this.client = client;
        setLayout(new BorderLayout(16, 16));
        UiTheme.stylePanel(this);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Position", "Age", "Club", "Foot"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane tableScroll = new JScrollPane(table);
        UiTheme.styleTable(table, tableScroll);

        JPanel top = new JPanel(new BorderLayout(16, 16));
        top.setOpaque(false);
        top.add(createHeader(), BorderLayout.NORTH);
        top.add(createMetrics(), BorderLayout.CENTER);

        JPanel center = UiTheme.createCard();
        center.setLayout(new BorderLayout(12, 12));
        center.add(createToolbar(), BorderLayout.NORTH);
        center.add(tableScroll, BorderLayout.CENTER);

        JPanel right = createFormCard();
        right.setPreferredSize(new Dimension(330, 0));

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);

        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());

        refreshPlayers(true);
    }

    public void refreshData(boolean silentError) {
        refreshPlayers(silentError);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        JLabel title = new JLabel("Player Command Center");
        title.setFont(new java.awt.Font("Segoe UI Semibold", java.awt.Font.PLAIN, 28));
        title.setForeground(UiTheme.TEXT);
        panel.add(title);
        return panel;
    }

    private JPanel createMetrics() {
        JPanel metrics = new JPanel(new GridLayout(1, 2, 12, 12));
        metrics.setOpaque(false);

        JPanel countCard = UiTheme.createCard();
        countCard.setLayout(new BorderLayout(0, 6));
        playerCountValue = UiTheme.metricValue("0", UiTheme.ACCENT);
        countCard.add(playerCountValue, BorderLayout.CENTER);
        countCard.add(UiTheme.metricCaption("Registered players"), BorderLayout.SOUTH);

        JPanel ageCard = UiTheme.createSoftCard();
        ageCard.setLayout(new BorderLayout(0, 6));
        averageAgeValue = UiTheme.metricValue("0", UiTheme.WARNING);
        ageCard.add(averageAgeValue, BorderLayout.CENTER);
        ageCard.add(UiTheme.metricCaption("Average age"), BorderLayout.SOUTH);

        metrics.add(countCard);
        metrics.add(ageCard);
        return metrics;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(12, 0));
        toolbar.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Squad Table");
        UiTheme.styleLabel(title, false);
        titleBlock.add(title);

        searchField = new JTextField();
        UiTheme.styleField(searchField);
        searchField.setToolTipText("Ara...");
        searchField.getDocument().addDocumentListener(SimpleDocumentListener.onChange(this::applySearchFilter));

        searchField.setPreferredSize(new Dimension(280, 38));
        toolbar.add(titleBlock, BorderLayout.WEST);
        toolbar.add(searchField, BorderLayout.CENTER);
        return toolbar;
    }

    private JPanel createFormCard() {
        JPanel card = UiTheme.createCard();
        card.setLayout(new BorderLayout(12, 12));

        JPanel title = new JPanel();
        title.setOpaque(false);
        JLabel formTitle = new JLabel("Player Details");
        UiTheme.styleLabel(formTitle, false);
        title.add(formTitle);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        idField = new JTextField();
        nameField = new JTextField();
        positionField = new JTextField();
        ageField = new JTextField();
        clubField = new JTextField();
        preferredFootCombo = new JComboBox<>(new String[]{"Right", "Left", "Both"});
        UiTheme.styleField(idField);
        UiTheme.styleField(nameField);
        UiTheme.styleField(positionField);
        UiTheme.styleField(ageField);
        UiTheme.styleField(clubField);
        UiTheme.styleCombo(preferredFootCombo);
        idField.setEditable(false);
        UiTheme.styleReadOnly(idField);

        form.add(labeledField("Player ID", idField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledField("Full Name", nameField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledField("Primary Position", positionField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledField("Age", ageField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledField("Club", clubField));
        form.add(Box.createVerticalStrut(10));
        form.add(labeledCombo("Preferred Foot", preferredFootCombo));

        JPanel actions = new JPanel(new GridLayout(3, 2, 10, 10));
        actions.setOpaque(false);
        JButton refreshButton = new JButton("Refresh");
        JButton createButton = new JButton("Add Player");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        UiTheme.styleButton(refreshButton, UiTheme.ACCENT);
        UiTheme.styleButton(createButton, UiTheme.SUCCESS);
        UiTheme.styleButton(updateButton, new java.awt.Color(37, 99, 235));
        UiTheme.styleButton(deleteButton, UiTheme.DANGER);
        UiTheme.styleButton(clearButton, UiTheme.adjust(UiTheme.MUTED, -20));

        refreshButton.addActionListener(e -> refreshPlayers(false));
        createButton.addActionListener(e -> createPlayer());
        updateButton.addActionListener(e -> updatePlayer());
        deleteButton.addActionListener(e -> deletePlayer());
        clearButton.addActionListener(e -> clearForm());

        actions.add(refreshButton);
        actions.add(createButton);
        actions.add(updateButton);
        actions.add(deleteButton);
        actions.add(clearButton);

        statusLabel = new JLabel("Ready");
        UiTheme.styleLabel(statusLabel, true);

        card.add(title, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setOpaque(false);
        JScrollPane formScroll = new JScrollPane(card);
        formScroll.setBorder(null);
        formScroll.getViewport().setBackground(UiTheme.APP_BG);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        wrapper.add(formScroll, BorderLayout.CENTER);
        wrapper.add(statusLabel, BorderLayout.SOUTH);
        return wrapper;
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

    private void applySearchFilter() {
        String query = searchField.getText().trim();
        if (query.isBlank()) {
            rowSorter.setRowFilter(null);
            return;
        }
        rowSorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(query), 1, 2, 4));
    }

    private void refreshPlayers(boolean silentError) {
        try {
            List<Player> players = client.getPlayers();
            tableModel.setRowCount(0);
            int totalAge = 0;
            for (Player player : players) {
                tableModel.addRow(new Object[]{
                        player.getId(),
                        player.getName(),
                        player.getPosition(),
                        player.getAge(),
                        player.getClub(),
                        player.getPreferredFoot()
                });
                totalAge += player.getAge();
            }
            playerCountValue.setText(String.valueOf(players.size()));
            averageAgeValue.setText(players.isEmpty() ? "0" : String.valueOf(Math.round(totalAge / (double) players.size())));
            statusLabel.setText("Player pool synced. " + players.size() + " records loaded.");
        } catch (Exception ex) {
            statusLabel.setText("Connection issue: " + ex.getMessage());
            if (!silentError) {
                showError(ex);
            }
        }
    }

    private void createPlayer() {
        try {
            validatePlayerInput();
            client.createPlayer(
                    nameField.getText().trim(),
                    positionField.getText().trim(),
                    Integer.parseInt(ageField.getText().trim()),
                    clubField.getText().trim(),
                    String.valueOf(preferredFootCombo.getSelectedItem())
            );
            refreshPlayers(false);
            clearForm();
            JOptionPane.showMessageDialog(this, "Player added successfully.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updatePlayer() {
        try {
            validatePlayerInput();
            client.updatePlayer(
                    idField.getText().trim(),
                    nameField.getText().trim(),
                    positionField.getText().trim(),
                    Integer.parseInt(ageField.getText().trim()),
                    clubField.getText().trim(),
                    String.valueOf(preferredFootCombo.getSelectedItem())
            );
            refreshPlayers(false);
            JOptionPane.showMessageDialog(this, "Player updated successfully.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deletePlayer() {
        try {
            client.deletePlayer(idField.getText().trim());
            refreshPlayers(false);
            clearForm();
            JOptionPane.showMessageDialog(this, "Player removed from the pool.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void validatePlayerInput() {
        if (nameField.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Player name cannot be empty.");
        }
        if (positionField.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Position cannot be empty.");
        }
        int age = Integer.parseInt(ageField.getText().trim());
        if (age < 13 || age > 45) {
            throw new IllegalArgumentException("Age should be between 13 and 45.");
        }
        if (clubField.getText().trim().isBlank()) {
            throw new IllegalArgumentException("Club cannot be empty.");
        }
    }

    private void fillFormFromSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        int row = table.convertRowIndexToModel(viewRow);
        idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        positionField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        ageField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        clubField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        preferredFootCombo.setSelectedItem(String.valueOf(tableModel.getValueAt(row, 5)));
        statusLabel.setText("Selected player: " + nameField.getText());
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        positionField.setText("");
        ageField.setText("");
        clubField.setText("");
        preferredFootCombo.setSelectedItem("Right");
        table.clearSelection();
        statusLabel.setText("Form cleared. Ready for a new entry.");
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
