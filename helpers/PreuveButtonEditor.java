package helpers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import controllers.PreuveController;
import models.Preuve;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PreuveButtonEditor extends DefaultCellEditor {
    private JButton button;
    private boolean isPushed;
    private PreuveController controller;
    private JTable table;
    private int columnIndex;

    public PreuveButtonEditor(PreuveController controller, JTable table, int columnIndex) {
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
                if (columnIndex == 3) { // Actualizar
                    int row = table.getSelectedRow();
                    int id = (int) table.getValueAt(row, 0);
                    // Mostrar diálogo para editar prueba
                    Preuve preuve = controller.obtenerPreuvePorId(id);
                    JTextField tipoField = new JTextField(preuve.getTipo());
                    JTextField descripcionField = new JTextField(preuve.getDescripcion());

                    JPanel dialogPanel = new JPanel();
                    dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
                    dialogPanel.add(new JLabel("Tipo:"));
                    dialogPanel.add(tipoField);
                    dialogPanel.add(new JLabel("Descripción:"));
                    dialogPanel.add(descripcionField);

                    int result = JOptionPane.showConfirmDialog(null, dialogPanel, "Editar Prueba", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        preuve.setTipo(tipoField.getText());
                        preuve.setDescripcion(descripcionField.getText());
                        controller.actualizarPreuve(preuve);
                        // Actualizar la tabla
                        table.setValueAt(preuve.getTipo(), row, 1);
                        table.setValueAt(preuve.getDescripcion(), row, 2);
                    }
                } else if (columnIndex == 4) { // Eliminar
                    int row = table.getSelectedRow();
                    int id = (int) table.getValueAt(row, 0);
                    int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar esta prueba?", "Eliminar Prueba", JOptionPane.YES_NO_OPTION);
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        controller.eliminarPreuve(id);
                        // Eliminar la fila de la tabla
                        DefaultTableModel model = (DefaultTableModel) table.getModel();
                        model.removeRow(row);
                    }
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
        if (isSelected) {
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } else {
            button.setForeground(table.getForeground());
            button.setBackground(UIManager.getColor("Button.background"));
        }
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return button.getText();
    }
}
