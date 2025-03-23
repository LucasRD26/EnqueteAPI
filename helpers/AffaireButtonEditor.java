package helpers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import controllers.AffaireController;
import models.Affaire;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AffaireButtonEditor extends DefaultCellEditor {
    private JButton button;
    private boolean isPushed;
    private AffaireController controller;
    private JTable table;
    private int columnIndex;

    public AffaireButtonEditor(AffaireController controller, JTable table, int columnIndex) {
        super(new JCheckBox());
        this.controller = controller;
        this.table = table;
        this.columnIndex = columnIndex;
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                if (columnIndex == 5) { // Actualizar
                    int row = table.getSelectedRow();
                    int id = (int) table.getValueAt(row, 0);
                    // Mostrar diálogo para editar affaire
                    Affaire affaire = controller.obtenerAffairePorId(id);
                    JTextField fechaField = new JTextField(affaire.getFecha());
                    JTextField ubicacionField = new JTextField(affaire.getUbicacion());
                    JTextField tipoDelitoField = new JTextField(affaire.getTipoDelito());
                    JTextField estadoCasoField = new JTextField(affaire.getEstadoCaso());

                    JPanel dialogPanel = new JPanel();
                    dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
                    dialogPanel.add(new JLabel("Fecha:"));
                    dialogPanel.add(fechaField);
                    dialogPanel.add(new JLabel("Ubicación:"));
                    dialogPanel.add(ubicacionField);
                    dialogPanel.add(new JLabel("Tipo de Delito:"));
                    dialogPanel.add(tipoDelitoField);
                    dialogPanel.add(new JLabel("Estado del Caso:"));
                    dialogPanel.add(estadoCasoField);

                    int result = JOptionPane.showConfirmDialog(null, dialogPanel, "Editar Affaire", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        affaire.setFecha(fechaField.getText());
                        affaire.setUbicacion(ubicacionField.getText());
                        affaire.setTipoDelito(tipoDelitoField.getText());
                        affaire.setEstadoCaso(estadoCasoField.getText());
                        controller.actualizarAffaire(affaire);
                        // Actualizar la tabla
                        table.setValueAt(affaire.getFecha(), row, 1);
                        table.setValueAt(affaire.getUbicacion(), row, 2);
                        table.setValueAt(affaire.getTipoDelito(), row, 3);
                        table.setValueAt(affaire.getEstadoCaso(), row, 4);
                    }
                } else if (columnIndex == 6) { // Eliminar
                    int row = table.getSelectedRow();
                    int id = (int) table.getValueAt(row, 0);
                    controller.eliminarAffaire(id);
                    // Eliminar la fila de la tabla
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.removeRow(row);
                }
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (value == null) {
            button.setText("Actualizar");
        } else {
            button.setText((String) value);
        }
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return button.getText();
    }
}

