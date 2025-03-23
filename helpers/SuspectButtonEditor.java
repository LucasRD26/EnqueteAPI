package helpers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import controllers.SuspectController;
import models.Suspect;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SuspectButtonEditor extends DefaultCellEditor {
    private JButton button;
    private boolean isPushed;
    private SuspectController controller;
    private JTable table;
    private int columnIndex;

    public SuspectButtonEditor(SuspectController controller, JTable table, int columnIndex) {
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
                if (columnIndex == 4) { // Actualizar
                    int row = table.getSelectedRow();
                    int id = (int) table.getValueAt(row, 0);
                    // Mostrar diálogo para editar suspect
                    Suspect suspect = controller.obtenerSuspectPorId(id);
                    JTextField nombreField = new JTextField(suspect.getNombre());
                    JTextField edadField = new JTextField(String.valueOf(suspect.getEdad()));
                    JTextField historialField = new JTextField(suspect.getHistorial());

                    JPanel dialogPanel = new JPanel();
                    dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
                    dialogPanel.add(new JLabel("Nombre:"));
                    dialogPanel.add(nombreField);
                    dialogPanel.add(new JLabel("Edad:"));
                    dialogPanel.add(edadField);
                    dialogPanel.add(new JLabel("Historial:"));
                    dialogPanel.add(historialField);

                    int result = JOptionPane.showConfirmDialog(null, dialogPanel, "Editar Suspect", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        suspect.setNombre(nombreField.getText());
                        suspect.setEdad(Integer.parseInt(edadField.getText()));
                        suspect.setHistorial(historialField.getText());
                        controller.actualizarSuspect(suspect);
                        // Actualizar la tabla
                        table.setValueAt(suspect.getNombre(), row, 1);
                        table.setValueAt(suspect.getEdad(), row, 2);
                        table.setValueAt(suspect.getHistorial(), row, 3);
                    }
                } else if (columnIndex == 5) { // Eliminar
                    int row = table.getSelectedRow();
                    int id = (int) table.getValueAt(row, 0);
                    controller.eliminarSuspect(id);
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

