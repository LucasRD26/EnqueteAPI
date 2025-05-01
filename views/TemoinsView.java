package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controllers.TemoinsController;
import helpers.ButtonRenderer;
import helpers.TemoinsButtonEditor;
import models.Temoins;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class TemoinsView extends JFrame {
    private TemoinsController controller;

    public TemoinsView(TemoinsController controller) {
        this.controller = controller;
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel para mostrar testigos existentes
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Tabla para mostrar testigos
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nom");
        model.addColumn("Age");
        model.addColumn("Declaration");
        model.addColumn("Actualiser");
        model.addColumn("Eliminer");

        JTable table = new JTable(model);
        List<Temoins> temoins = controller.obtenerTemoins();
        for (Temoins temoins1 : temoins) {
            model.addRow(new Object[] {
                    temoins1.getId(),
                    temoins1.getNombre(),
                    temoins1.getEdad(),
                    temoins1.getDeclaracion(),
                    "Actualiser",
                    "Eliminer"
            });
        }

        // Agregar botones de acción a la tabla
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new TemoinsButtonEditor(controller, table, 4));
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new TemoinsButtonEditor(controller, table, 5));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel para agregar nuevo testigo
        JPanel nuevoPanel = new JPanel();
        nuevoPanel.setLayout(new BoxLayout(nuevoPanel, BoxLayout.Y_AXIS));

        JTextField nombreField = new JTextField();
        JTextField edadField = new JTextField();
        JTextField declaracionField = new JTextField();

        JButton agregarButton = new JButton("Ajouter Temoin");
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Temoins temoins1 = new Temoins(
                        controller.obtenerTemoins().size() + 1,
                        nombreField.getText(),
                        Integer.parseInt(edadField.getText()),
                        declaracionField.getText()
                );
                controller.agregarTemoins(temoins1);
                // Actualizar la tabla con el nuevo testigo
                model.addRow(new Object[] {
                        temoins1.getId(),
                        temoins1.getNombre(),
                        temoins1.getEdad(),
                        temoins1.getDeclaracion(),
                        "Actualiser",
                        "Eliminer"
                });
            }
        });

        nuevoPanel.add(new JLabel("Nom:"));
        nuevoPanel.add(nombreField);
        nuevoPanel.add(new JLabel("Age:"));
        nuevoPanel.add(edadField);
        nuevoPanel.add(new JLabel("Declaration:"));
        nuevoPanel.add(declaracionField);
        nuevoPanel.add(agregarButton);

        panel.add(nuevoPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}

