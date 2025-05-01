// 1. Nueva clase PriorizacionSuspectsView.java
package views;

import controllers.AffaireController;
import controllers.SuspectController;
import helpers.AffaireRelation;
import models.Affaire;
import models.Suspect;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PriorizacionSuspectsView extends JFrame {
    private final AffaireController affaireController;
    private final SuspectController suspectController;
    private final AffaireRelation relationManager;
    private final DefaultTableModel model;
    private final Map<Integer, AffaireRelation.AffaireData> relaciones;

    public PriorizacionSuspectsView(AffaireController affaireController, 
                                   SuspectController suspectController) {
        this.affaireController = affaireController;
        this.suspectController = suspectController;
        this.relationManager = new AffaireRelation();
        this.relaciones = new HashMap<>();

        setTitle("Priorización de Suspects por Affaire");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        this.model = new DefaultTableModel(
            new Object[]{"ID Affaire", "Suspect Priorizado", "Score", "Detalles"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        
        initUI();
        cargarDatos();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initUI() {
        JTable tablaPriorizacion = new JTable(model);
        tablaPriorizacion.getColumn("Detalles").setCellRenderer(new ButtonRenderer());
        tablaPriorizacion.getColumn("Detalles").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        add(new JScrollPane(tablaPriorizacion), BorderLayout.CENTER);
    }

    private void cargarDatos() {
        List<Affaire> affaires = affaireController.obtenerAffaires();
        List<Suspect> suspects = suspectController.obtenerSuspects();

        // Cargar datos en relationManager
        affaires.forEach(relationManager::addAffaire);
        relationManager.generarRelaciones(
            List.of(), // Temoins (no necesario para esta vista)
            List.of(), // Preuves (no necesario para esta vista)
            suspects
        );

        // Procesar casos no resueltos
        affaires.stream()
            .filter(a -> !relationManager.getResolvedCases().containsKey(a.getId()))
            .forEach(affaire -> {
                Map<Suspect, Integer> scores = new HashMap<>();
                
                suspects.forEach(suspect -> {
                    int score = calcularScore(affaire, suspect);
                    scores.put(suspect, score);
                });
                
                Suspect topSuspect = scores.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
                
                if(topSuspect != null) {
                    relaciones.put(affaire.getId(), relationManager.getOpenCases().get(affaire.getId()));
                    model.addRow(new Object[]{
                        affaire.getId(),
                        topSuspect.getNombre(),
                        scores.get(topSuspect),
                        "🔍 Detalles"
                    });
                }
            });
    }

    private int calcularScore(Affaire affaire, Suspect suspect) {
        int score = 0;
        
        // 1. Coincidencia de palabras clave
        Set<String> palabrasAffaire = relationManager.filtrarPalabras(
            affaire.getUbicacion() + " " + affaire.getTipoDelito() + " " + affaire.getFecha()
        );
        Set<String> palabrasSuspect = relationManager.filtrarPalabras(
            suspect.getNombre() + " " + suspect.getHistorial()
        );
        score += (int) palabrasAffaire.stream()
            .filter(palabrasSuspect::contains)
            .count() * 5;

        // 2. Relaciones existentes
        AffaireRelation.AffaireData data = relationManager.getOpenCases().get(affaire.getId());
        if(data != null && data.suspects.containsKey(suspect.getNombre())) {
            score += data.suspects.get(suspect.getNombre()).size() * 3;
        }

        // 3. Casos resueltos relacionados
        score += relationManager.getResolvedCases().values().stream()
            .filter(d -> d.guiltySuspect != null && d.guiltySuspect.equals(suspect.getNombre()))
            .count() * 10;

        return score;
    }

    // Clases para el renderizado de botones
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("🔍 Detalles");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("🔍 Detalles");
            button.addActionListener(e -> mostrarDetalles());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        private void mostrarDetalles() {
            int affaireId = (int) model.getValueAt(currentRow, 0);
            String suspect = (String) model.getValueAt(currentRow, 1);
            
            JDialog dialog = new JDialog(
                PriorizacionSuspectsView.this, 
                "Detalles de Priorización - Affaire " + affaireId, 
                true
            );
            dialog.setLayout(new BorderLayout());
            dialog.setSize(600, 400);
            
            JTextArea detalles = new JTextArea(generarDetallePriorizacion(affaireId, suspect));
            detalles.setEditable(false);
            
            dialog.add(new JScrollPane(detalles), BorderLayout.CENTER);
            dialog.setLocationRelativeTo(PriorizacionSuspectsView.this);
            dialog.setVisible(true);
        }

        private String generarDetallePriorizacion(int affaireId, String suspect) {
            StringBuilder sb = new StringBuilder();
            AffaireRelation.AffaireData data = relaciones.get(affaireId);
            
            sb.append("=== Critères de priorisation ===\n");
            sb.append("Affaire ID: ").append(affaireId).append("\n");
            sb.append("Suspect: ").append(suspect).append("\n\n");
            
            // 1. Coincidencias léxicas
            Set<String> palabrasAffaire = relationManager.filtrarPalabras(
                affaireController.obtenerAffaire(affaireId)
                    .map(a -> a.getUbicacion() + " " + a.getTipoDelito() + " " + a.getFecha())
                    .orElse("")
            );
            
            Set<String> palabrasSuspect = relationManager.filtrarPalabras(
                suspect + " " + suspectController.obtenerSuspect(suspect)
                    .map(Suspect::getHistorial)
                    .orElse("")
            );
            
            long coincidencias = palabrasAffaire.stream()
                .filter(palabrasSuspect::contains)
                .count();
            
            sb.append("1. Coïncidences lexicales (").append(coincidencias).append("):\n");
            palabrasAffaire.stream()
                .filter(palabrasSuspect::contains)
                .forEach(p -> sb.append("   - ").append(p).append("\n"));
            sb.append("\n");

            // 2. Relaciones detectadas
            if(data != null && data.suspects.containsKey(suspect)) {
                sb.append("2. Relations détectées (").append(data.suspects.get(suspect).size()).append("):\n");
                data.suspects.get(suspect).forEach(p -> sb.append("   - ").append(p).append("\n"));
                sb.append("\n");
            }

            // 3. Casos resueltos
            long casosResueltos = relationManager.getResolvedCases().values().stream()
                .filter(d -> d.guiltySuspect != null && d.guiltySuspect.equals(suspect))
                .count();
            
            sb.append("3. Cas résolus associés: ").append(casosResueltos).append("\n");
            
            return sb.toString();
        }
    }
}


