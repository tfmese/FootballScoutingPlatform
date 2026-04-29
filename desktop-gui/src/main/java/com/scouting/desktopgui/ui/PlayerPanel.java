package com.scouting.desktopgui.ui;

import com.scouting.desktopgui.client.ScoutingApiClient;
import com.scouting.desktopgui.model.Player;

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

public class PlayerPanel extends JPanel {
    private final ScoutingApiClient client;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField idField;
    private final JTextField nameField;
    private final JTextField positionField;
    private final JTextField ageField;
    private final JLabel statusLabel;

    public PlayerPanel(ScoutingApiClient client) {
        this.client = client;
        this.setLayout(new BorderLayout());

        this.tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Position", "Age"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(tableModel);
        this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        idField = new JTextField();
        idField.setEditable(false);
        nameField = new JTextField();
        positionField = new JTextField();
        ageField = new JTextField();

        form.add(new JLabel("ID (update/delete):"));
        form.add(idField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Position:"));
        form.add(positionField);
        form.add(new JLabel("Age:"));
        form.add(ageField);

        JButton refreshButton = new JButton("Listeyi Yenile");
        JButton createButton = new JButton("Oyuncu Ekle");
        JButton updateButton = new JButton("Oyuncu Guncelle");
        JButton deleteButton = new JButton("Oyuncu Sil");
        JButton clearButton = new JButton("Formu Temizle");

        JPanel actions = new JPanel(new GridLayout(1, 5, 8, 8));
        actions.add(refreshButton);
        actions.add(createButton);
        actions.add(updateButton);
        actions.add(deleteButton);
        actions.add(clearButton);

        statusLabel = new JLabel("Hazır");

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(actions, BorderLayout.SOUTH);
        south.add(statusLabel, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> refreshPlayers(false));
        createButton.addActionListener(e -> createPlayer());
        updateButton.addActionListener(e -> updatePlayer());
        deleteButton.addActionListener(e -> deletePlayer());
        clearButton.addActionListener(e -> clearForm());
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelection());

        refreshPlayers(true);
    }

    private void refreshPlayers(boolean silentError) {
        try {
            List<Player> players = client.getPlayers();
            tableModel.setRowCount(0);
            for (Player player : players) {
                tableModel.addRow(new Object[]{
                        player.getId(),
                        player.getName(),
                        player.getPosition(),
                        player.getAge()
                });
            }
            statusLabel.setText("Oyuncu listesi guncellendi. Kayit sayisi: " + players.size());
        } catch (Exception ex) {
            statusLabel.setText("Baglanti hatasi: " + ex.getMessage());
            if (!silentError) {
                showError(ex);
            }
        }
    }

    private void createPlayer() {
        try {
            client.createPlayer(
                    nameField.getText().trim(),
                    positionField.getText().trim(),
                    Integer.parseInt(ageField.getText().trim())
            );
            refreshPlayers(false);
            clearForm();
            JOptionPane.showMessageDialog(this, "Oyuncu basariyla eklendi.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void updatePlayer() {
        try {
            client.updatePlayer(
                    idField.getText().trim(),
                    nameField.getText().trim(),
                    positionField.getText().trim(),
                    Integer.parseInt(ageField.getText().trim())
            );
            refreshPlayers(false);
            JOptionPane.showMessageDialog(this, "Oyuncu basariyla guncellendi.");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void deletePlayer() {
        try {
            client.deletePlayer(idField.getText().trim());
            refreshPlayers(false);
            clearForm();
            JOptionPane.showMessageDialog(this, "Oyuncu basariyla silindi.");
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
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        positionField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        ageField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        statusLabel.setText("Secilen oyuncu ID: " + idField.getText());
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        positionField.setText("");
        ageField.setText("");
        table.clearSelection();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
    }
}
