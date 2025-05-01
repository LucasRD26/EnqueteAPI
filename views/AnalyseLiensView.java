package views;

import controllers.SuspectController;
import controllers.TemoinsController;
import controllers.PreuveController;
import models.Suspect;
import models.Temoins;
import models.Preuve;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AnalyseLiensView extends JFrame {
    private static final Set<String> CONECTORES = Set.of(
        "de", "le", "la", "et", "du", "des", "un", "une", "en", "a", "les", "que", "el", "los"
    );
    
    private SuspectController suspectController;
    private TemoinsController temoinsController;
    private PreuveController preuveController;
    private JTable tablaPrincipal;
    private DefaultTableModel model;

    public AnalyseLiensView(SuspectController suspectController, 
                           TemoinsController temoinsController, 
                           PreuveController preuveController) {
        this.suspectController = suspectController;
        this.temoinsController = temoinsController;
        this.preuveController = preuveController;
        
        setTitle("Analyse des Liens entre Suspects-Preuves-Temoins");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initUI();
        cargarDatos();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        model = new DefaultTableModel(new Object[]{"Suspect", "Preuves Associées", "Temoins Associés", "Détails"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        tablaPrincipal = new JTable(model);
        tablaPrincipal.getColumn("Détails").setCellRenderer(new ButtonRenderer());
        tablaPrincipal.getColumn("Détails").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(tablaPrincipal);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        List<Suspect> suspects = suspectController.obtenerSuspects();
        List<Temoins> temoins = temoinsController.obtenerTemoins();
        List<Preuve> preuves = preuveController.obtenerPreuves();

        for (Suspect suspect : suspects) {
            Set<String> palabrasSuspect = filtrarPalabras(suspect.getNombre() + " " + suspect.getHistorial());
            
            // Preuves associées
            Set<String> preuvesAsociadas = new HashSet<>();
            for (Preuve p : preuves) {
                Set<String> palabrasPreuve = filtrarPalabras(p.getDescripcion() + " " + p.getTipo());
                if (!Collections.disjoint(palabrasSuspect, palabrasPreuve)) {
                    preuvesAsociadas.add(p.getTipo());
                }
            }
            
            // Temoins associés
            Set<String> temoinsAsociados = new HashSet<>();
            for (Temoins t : temoins) {
                Set<String> palabrasTemoin = filtrarPalabras(t.getDeclaracion());
                if (!Collections.disjoint(palabrasSuspect, palabrasTemoin)) {
                    temoinsAsociados.add(t.getNombre());
                }
            }
            
            model.addRow(new Object[]{
                suspect.getNombre(),
                String.join(", ", preuvesAsociadas),
                String.join(", ", temoinsAsociados),
                "🔍 Détails"
            });
        }
    }

    private Set<String> filtrarPalabras(String texto) {
        if (texto == null) return Collections.emptySet();
        return Arrays.stream(texto.toLowerCase().split("\\s+"))
            .filter(palabra -> !CONECTORES.contains(palabra))
            .collect(Collectors.toSet());
    }

    // Clases para el renderizado de botones
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("🔍 Détails");
            button.addActionListener(e -> mostrarDetalles());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        private void mostrarDetalles() {
            String suspect = (String) model.getValueAt(currentRow, 0);
            String preuves = (String) model.getValueAt(currentRow, 1);
            String temoins = (String) model.getValueAt(currentRow, 2);
            
            JDialog dialog = new JDialog(AnalyseLiensView.this, "Détails Complets", true);
            dialog.setLayout(new BorderLayout());
            dialog.setSize(600, 400);
            
            JTextArea detalles = new JTextArea(
                "Suspect: " + suspect + "\n\n" +
                "Preuves Associées:\n- " + preuves.replace(", ", "\n- ") + "\n\n" +
                "Temoins Associés:\n- " + temoins.replace(", ", "\n- ")
            );
            detalles.setEditable(false);
            
            dialog.add(new JScrollPane(detalles), BorderLayout.CENTER);
            dialog.setLocationRelativeTo(AnalyseLiensView.this);
            dialog.setVisible(true);
        }
    }
}
