package views;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controllers.AffaireController;
import models.Affaire;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BusquedaAvanzadaView extends JFrame {
    private AffaireController controller;

    public BusquedaAvanzadaView(AffaireController controller) {
        this.controller = controller;
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel para formulario de búsqueda
        JPanel formularioPanel = new JPanel();
        formularioPanel.setLayout(new BoxLayout(formularioPanel, BoxLayout.Y_AXIS));

        // Campos para criterios de búsqueda
        JTextField fechaField = new JTextField();
        JTextField ubicacionField = new JTextField();
        JTextField tipoDelitoField = new JTextField();

        // Tabla para mostrar resultados
        DefaultTableModel resultadosModel = new DefaultTableModel();
        resultadosModel.addColumn("ID");
        resultadosModel.addColumn("Date");
        resultadosModel.addColumn("Lieu");
        resultadosModel.addColumn("Type de Crime");
        resultadosModel.addColumn("Status Affaire");

        JTable resultadosTable = new JTable(resultadosModel);

        // Botón para buscar
        JButton buscarButton = new JButton("Rechercher");
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para buscar affaires
                String fecha = fechaField.getText();
                String ubicacion = ubicacionField.getText();
                String tipoDelito = tipoDelitoField.getText();

                List<Affaire> resultados = controller.buscarAffairesAvanzado(fecha, ubicacion, tipoDelito);

                // Mostrar resultados en la tabla
                resultadosModel.setRowCount(0); // Limpiar tabla
                for (Affaire affaire : resultados) {
                    resultadosModel.addRow(new Object[] {
                            affaire.getId(),
                            affaire.getFecha(),
                            affaire.getUbicacion(),
                            affaire.getTipoDelito(),
                            affaire.getEstadoCaso()
                    });
                }
            }
        });

        formularioPanel.add(new JLabel("Date:"));
        formularioPanel.add(fechaField);
        formularioPanel.add(new JLabel("Lieu:"));
        formularioPanel.add(ubicacionField);
        formularioPanel.add(new JLabel("Type de crime:"));
        formularioPanel.add(tipoDelitoField);
        formularioPanel.add(buscarButton);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(formularioPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultadosTable), BorderLayout.CENTER);

        add(panel);
        setVisible(true);
    }
}


