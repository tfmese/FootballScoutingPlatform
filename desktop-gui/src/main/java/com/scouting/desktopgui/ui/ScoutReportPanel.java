package com.scouting.desktopgui.ui;

import com.scouting.desktopgui.client.ScoutingApiClient;
import com.scouting.desktopgui.model.ScoutReport;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

public class ScoutReportPanel extends JPanel {
    private final ScoutingApiClient client;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField idField;
    private final JTextField playerNameField;
    private final JTextField positionField;
    private final JTextField potentialScoreField;
    private final JTextField notesField;
    private final JLabel statusLabel;

    public ScoutReportPanel(ScoutingApiClient client) {
        this.client = client;
        this.setLayout(new BorderLayout());

        this.tableModel = new DefaultTableModel(new String[]{"ID", "Player", "Position", "Potential", "Notes"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(tableModel);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        idField = new JTextField();
        idField.setEditable(false);
        playerNameField = new JTextField();
        positionField = new JTextField();
        potentialScoreField = new JTextField();
        notesField = new JTextField();

        form.add(new JLabel("ID (update/delete):"));
        form.add(idField);
        form.add(new JLabel("Player Name:"));
        form.add(playerNameField);
        form.add(new JLabel("Position:"));
        form.add(positionField);
        form.add(new JLabel("Potential Score:"));
        form.add(potentialScoreField);
        form.add(new JLabel("Notes:"));
        form.add(notesField);

        JButton refreshButton = new JButton("Listeyi Yenile");
        JButton createButton = new JButton("Rapor Ekle");
        JButton updateButton = new JButton("Rapor Guncelle");
        JButton deleteButton = new JButton("Rapor Sil");
        JButton clearButton = new JButton("Formu Temizle");

        JPanel actions = new JPanel(new GridLayout(1, 5, 8, 8));
        actions.add(refreshButton);
        actions.add(createButton);
        actions.add(updateButton);
        actions.add(deleteButton);
        actions.add(clearButton);

        statusLabel = new JLabel("Hazir");

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(actions, BorderLayout.SOUTH);
        south.add(statusLabel, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshReports(false));
        createButton.addActionListener(e -> createReport());
        updateButton.addActionListener(e -> updateReport());
        deleteButton.addActionListener(e -> deleteReport());
        clearButton.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());

        refreshReports(true);
    }

    private void refreshReports(boolean silentError) {
        try {
            List<ScoutReport> reports = client.getScoutReports();
            tableModel.setRowCount(0);
            for (ScoutReport report : reports) {
                tableModel.addRow(new Object[]{
                        report.getId(),
                        report.getPlayerName(),
                        report.getPosition(),
                        report.getPotentialScore(),
                        report.getNotes()
                });
            }
            statusLabel.setText("Rapor listesi guncellendi. Kayit sayisi: " + reports.size());
        } catch (Exception ex) {
            statusLabel.setText("Baglanti hatasi: " + ex.getMessage());
            if (!silentError) {
                showError(ex);
            }
        }
    }

    private void createReport() {
        try {
            client.createScoutReport(
                    playerNameField.getText().trim(),
                    positionField.getText().trim(),
                    Integer.parseInt(potentialScoreField.getText().trim()),
                    notesField.getText().trim()
            );
            refreshReports(false);
            clearForm();
            JOptionPane.showMessageDialog(this, "Scout raporu basariyla eklendi.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updateReport() {
        try {
            client.updateScoutReport(
                    idField.getText().trim(),
                    playerNameField.getText().trim(),
                    positionField.getText().trim(),
                    Integer.parseInt(potentialScoreField.getText().trim()),
                    notesField.getText().trim()
            );
            refreshReports(false);
            JOptionPane.showMessageDialog(this, "Scout raporu basariyla guncellendi.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deleteReport() {
        try {
            client.deleteScoutReport(idField.getText().trim());
            refreshReports(false);
            clearForm();
            JOptionPane.showMessageDialog(this, "Scout raporu basariyla silindi.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        playerNameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        positionField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        potentialScoreField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        notesField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        statusLabel.setText("Secilen rapor ID: " + idField.getText());
    }

    private void clearForm() {
        idField.setText("");
        playerNameField.setText("");
        positionField.setText("");
        potentialScoreField.setText("");
        notesField.setText("");
        table.clearSelection();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
    }
}
