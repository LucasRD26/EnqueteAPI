package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controllers.PreuveController;
import helpers.ButtonRenderer;
import helpers.PreuveButtonEditor;
import models.Preuve;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class PreuveView extends JFrame {
    private PreuveController controller;

    public PreuveView(PreuveController controller) {
        this.controller = controller;
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel para mostrar pruebas existentes
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Tabla para mostrar pruebas
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Type");
        model.addColumn("Description");
        model.addColumn("Actualiser");
        model.addColumn("Eliminer");

        JTable table = new JTable(model);
        List<Preuve> preuves = controller.obtenerPreuves();
        for (Preuve preuve : preuves) {
            model.addRow(new Object[] {
                    preuve.getId(),
                    preuve.getTipo(),
                    preuve.getDescripcion(),
                    "Actualiser",
                    "Eliminer"
            });
        }

        // Agregar botones de acción a la tabla
        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new PreuveButtonEditor(controller, table, 3));
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new PreuveButtonEditor(controller, table, 4));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel para agregar nueva prueba
        JPanel nuevoPanel = new JPanel();
        nuevoPanel.setLayout(new BoxLayout(nuevoPanel, BoxLayout.Y_AXIS));

        JTextField tipoField = new JTextField();
        JTextField descripcionField = new JTextField();

        JButton agregarButton = new JButton("Ajouter Preuve");
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preuve preuve = new Preuve(
                        controller.obtenerPreuves().size() + 1,
                        tipoField.getText(),
                        descripcionField.getText()
                );
                controller.agregarPreuve(preuve);
                // Actualizar la tabla con la nueva prueba
                model.addRow(new Object[] {
                        preuve.getId(),
                        preuve.getTipo(),
                        preuve.getDescripcion(),
                        "Actualiser",
                        "Eliminer"
                });
            }
        });

        nuevoPanel.add(new JLabel("Type:"));
        nuevoPanel.add(tipoField);
        nuevoPanel.add(new JLabel("Description:"));
        nuevoPanel.add(descripcionField);
        nuevoPanel.add(agregarButton);

        panel.add(nuevoPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}

