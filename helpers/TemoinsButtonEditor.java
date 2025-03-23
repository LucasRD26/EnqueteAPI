package helpers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import controllers.TemoinsController;
import models.Temoins;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TemoinsButtonEditor extends DefaultCellEditor {
    private JButton button;
    private boolean isPushed;
    private TemoinsController controller;
    private JTable table;
    private int columnIndex;

    public TemoinsButtonEditor(TemoinsController controller, JTable table, int columnIndex) {
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
                    // Mostrar diálogo para editar testigo
                    Temoins temoins = controller.obtenerTemoinsPorId(id);
                    JTextField nombreField = new JTextField(temoins.getNombre());
                    JTextField edadField = new JTextField(String.valueOf(temoins.getEdad()));
                    JTextField declaracionField = new JTextField(temoins.getDeclaracion());

                    JPanel dialogPanel = new JPanel();
                    dialogPanel.setLayout(new BoxLayout(dialogPanel, BoxLayout.Y_AXIS));
                    dialogPanel.add(new JLabel("Nombre:"));
                    dialogPanel.add(nombreField);
                    dialogPanel.add(new JLabel("Edad:"));
                    dialogPanel.add(edadField);
                    dialogPanel.add(new JLabel("Declaración:"));
                    dialogPanel.add(declaracionField);

                    int result = JOptionPane.showConfirmDialog(null, dialogPanel, "Editar Testigo", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        temoins.setNombre(nombreField.getText());
                        temoins.setEdad(Integer.parseInt(edadField.getText()));
                        temoins.setDeclaracion(declaracionField.getText());
                        controller.actualizarTemoins(temoins);
                        // Actualizar la tabla
                        table.setValueAt(temoins.getNombre(), row, 1);
                        table.setValueAt(temoins.getEdad(), row, 2);
                        table.setValueAt(temoins.getDeclaracion(), row, 3);
                    }
                } else if (columnIndex == 5) { // Eliminar
                    int row = table.getSelectedRow();
                    int id = (int) table.getValueAt(row, 0);
                    controller.eliminarTemoins(id);
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

