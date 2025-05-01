package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controllers.SuspectController;
import helpers.ButtonRenderer;
import helpers.SuspectButtonEditor;
import models.Suspect;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SuspectView extends JFrame {
    private SuspectController controller;

    public SuspectView(SuspectController controller) {
        this.controller = controller;
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel para mostrar suspects existentes
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Tabla para mostrar suspects
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nom");
        model.addColumn("Age");
        model.addColumn("Antecedents");
        model.addColumn("Actualiser");
        model.addColumn("Eliminer");

        JTable table = new JTable(model);
        List<Suspect> suspects = controller.obtenerSuspects();
        for (Suspect suspect : suspects) {
            model.addRow(new Object[] {
                    suspect.getId(),
                    suspect.getNombre(),
                    suspect.getEdad(),
                    suspect.getHistorial(),
                    "Actualiser",
                    "Eliminer"
            });
        }

        // Agregar botones de acción a la tabla
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new SuspectButtonEditor(controller, table, 4));
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new SuspectButtonEditor(controller, table, 5));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel para agregar nuevo suspect
        JPanel nuevoPanel = new JPanel();
        nuevoPanel.setLayout(new BoxLayout(nuevoPanel, BoxLayout.Y_AXIS));

        JTextField nombreField = new JTextField();
        JTextField edadField = new JTextField();
        JTextField historialField = new JTextField();

        JButton agregarButton = new JButton("Ajouter Suspect");
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Suspect suspect = new Suspect(
                        controller.obtenerSuspects().size() + 1,
                        nombreField.getText(),
                        Integer.parseInt(edadField.getText()),
                        historialField.getText()
                );
                controller.agregarSuspect(suspect);
                // Actualizar la tabla con el nuevo suspect
                model.addRow(new Object[] {
                        suspect.getId(),
                        suspect.getNombre(),
                        suspect.getEdad(),
                        suspect.getHistorial(),
                        "Actualiser",
                        "Eliminer"
                });
            }
        });

        nuevoPanel.add(new JLabel("Nom:"));
        nuevoPanel.add(nombreField);
        nuevoPanel.add(new JLabel("Age:"));
        nuevoPanel.add(edadField);
        nuevoPanel.add(new JLabel("Antecedents:"));
        nuevoPanel.add(historialField);
        nuevoPanel.add(agregarButton);

        panel.add(nuevoPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}

