package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controllers.AffaireController;
import helpers.AffaireButtonEditor;
import helpers.ButtonRenderer;
import models.Affaire;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AffaireView extends JFrame {
    private AffaireController controller;

    public AffaireView(AffaireController controller) {
        this.controller = controller;
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel para mostrar affaires existentes
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Tabla para mostrar affaires
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Fecha");
        model.addColumn("Ubicación");
        model.addColumn("Tipo de Delito");
        model.addColumn("Estado del Caso");
        model.addColumn("Actualizar");
        model.addColumn("Eliminar");

        JTable table = new JTable(model);
        List<Affaire> affaires = controller.obtenerAffaires();
        for (Affaire affaire : affaires) {
            model.addRow(new Object[] {
                    affaire.getId(),
                    affaire.getFecha(),
                    affaire.getUbicacion(),
                    affaire.getTipoDelito(),
                    affaire.getEstadoCaso(),
                    "Actualizar",
                    "Eliminar"
            });
        }

        // Agregar botones de acción a la tabla
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new AffaireButtonEditor(controller, table, 5));
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new AffaireButtonEditor(controller, table, 6));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Panel para agregar nuevo affaire
        JPanel nuevoPanel = new JPanel();
        nuevoPanel.setLayout(new BoxLayout(nuevoPanel, BoxLayout.Y_AXIS));

        JTextField fechaField = new JTextField();
        JTextField ubicacionField = new JTextField();
        JTextField tipoDelitoField = new JTextField();
        JTextField estadoCasoField = new JTextField();

        JButton agregarButton = new JButton("Agregar Affaire");
        agregarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Affaire affaire = new Affaire(
                        controller.obtenerAffaires().size() + 1,
                        fechaField.getText(),
                        ubicacionField.getText(),
                        tipoDelitoField.getText(),
                        estadoCasoField.getText()
                );
                controller.agregarAffaire(affaire);
                // Actualizar la tabla con el nuevo affaire
                model.addRow(new Object[] {
                        affaire.getId(),
                        affaire.getFecha(),
                        affaire.getUbicacion(),
                        affaire.getTipoDelito(),
                        affaire.getEstadoCaso(),
                        "Actualizar",
                        "Eliminar"
                });
            }
        });

        nuevoPanel.add(new JLabel("Fecha:"));
        nuevoPanel.add(fechaField);
        nuevoPanel.add(new JLabel("Ubicación:"));
        nuevoPanel.add(ubicacionField);
        nuevoPanel.add(new JLabel("Tipo de Delito:"));
        nuevoPanel.add(tipoDelitoField);
        nuevoPanel.add(new JLabel("Estado del Caso:"));
        nuevoPanel.add(estadoCasoField);
        nuevoPanel.add(agregarButton);

        panel.add(nuevoPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}

