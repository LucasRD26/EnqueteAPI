package views;

import controllers.AffaireController;
import controllers.PreuveController;
import controllers.SuspectController;
import controllers.TemoinsController;
import helpers.AffaireRelation;
import models.Affaire;
import models.Preuve;
import models.Suspect;
import models.Temoins;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class PaginaPrincipal extends JFrame {
    private AffaireController affaireController;
    private SuspectController suspectController;
    private TemoinsController temoinsController;
    private PreuveController preuveController;
    private JTable tablaResultados;
    private AffaireRelation relationManager = new AffaireRelation();

    public PaginaPrincipal(AffaireController affaireController, SuspectController suspectController,
                           TemoinsController temoinsController, PreuveController preuveController) {
        this.affaireController = affaireController;
        this.suspectController = suspectController;
        this.temoinsController = temoinsController;
        this.preuveController = preuveController;
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Barra de menú (igual que antes)
        JMenuBar menuBar = new JMenuBar();
        JMenu modeloMenu = new JMenu("Modelos");
        JMenuItem affaireItem = new JMenuItem("Affaires");
        affaireItem.addActionListener(e -> new AffaireView(affaireController));
        modeloMenu.add(affaireItem);

        JMenuItem suspectItem = new JMenuItem("Suspects");
        suspectItem.addActionListener(e -> new SuspectView(suspectController));
        modeloMenu.add(suspectItem);

        JMenuItem temoinsItem = new JMenuItem("Testigos");
        temoinsItem.addActionListener(e -> new TemoinsView(temoinsController));
        modeloMenu.add(temoinsItem);

        JMenuItem preuveItem = new JMenuItem("Pruebas");
        preuveItem.addActionListener(e -> new PreuveView(preuveController));
        modeloMenu.add(preuveItem);

        menuBar.add(modeloMenu);

        JMenu funcionalidadesMenu = new JMenu("Funcionalidades");
        JMenuItem busquedaAvanzadaItem = new JMenuItem("Busqueda Avanzada");
        busquedaAvanzadaItem.addActionListener(e -> new BusquedaAvanzadaView(affaireController));
        funcionalidadesMenu.add(busquedaAvanzadaItem);

        JMenuItem analisisEnlacesItem = new JMenuItem("Analisis de Enlaces");
        analisisEnlacesItem.addActionListener(e -> new AnalisisEnlacesView(affaireController, suspectController, temoinsController, preuveController));
        funcionalidadesMenu.add(analisisEnlacesItem);

        menuBar.add(funcionalidadesMenu);

        setJMenuBar(menuBar);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Boton para generar correspondencias
        JButton btnGenerar = new JButton("Generar correspondencias");
        btnGenerar.addActionListener(e -> generarYMostrarCorrespondencias());

        JPanel topPanel = new JPanel();
        topPanel.add(btnGenerar);

        // Tabla de resultados
        tablaResultados = new JTable();
        JScrollPane scrollPane = new JScrollPane(tablaResultados);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private void generarYMostrarCorrespondencias() {
        relationManager = new AffaireRelation();

        List<Affaire> affaires = affaireController.obtenerAffaires();
        List<Temoins> temoins = temoinsController.obtenerTemoins();
        List<Preuve> preuves = preuveController.obtenerPreuves();
        List<Suspect> suspects = suspectController.obtenerSuspects();

        affaires.forEach(relationManager::addAffaire);
        relationManager.generarRelaciones(temoins, preuves, suspects);

        // Modelo de tabla con columna de boton
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID Affaire", "Ubicacion", "Tipo Delito", "Temoins", "Preuves", "Suspects (palabras clave)", "Informe"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo la columna del boton es editable
            }
        };

        List<Integer> affaireIds = new ArrayList<>(relationManager.getRelations().keySet());
        for (Integer id : affaireIds) {
            AffaireRelation.AffaireData data = relationManager.getRelations().get(id);

            // Suspects con palabras clave
            String suspectsStr = data.suspects.entrySet().stream()
                .map(e -> e.getKey() + " (" + String.join(" ", e.getValue()) + ")")
                .collect(Collectors.joining(", "));

            model.addRow(new Object[]{
                id,
                data.ubicacion,
                data.tipoDelito,
                String.join(", ", data.temoins),
                String.join(", ", data.preuves),
                suspectsStr,
                "Ver informe"
            });
        }

        tablaResultados.setModel(model);

        // Renderizador y editor para el boton
        tablaResultados.getColumn("Informe").setCellRenderer(new ButtonRenderer());
        tablaResultados.getColumn("Informe").setCellEditor(new ButtonEditor(new JCheckBox(), affaireIds, relationManager));
    }

    // Renderizador de boton
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Ver informe" : value.toString());
            return this;
        }
    }

    // Editor de boton
    static class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private List<Integer> affaireIds;
        private AffaireRelation relationManager;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox, List<Integer> affaireIds, AffaireRelation relationManager) {
            super(checkBox);
            this.affaireIds = affaireIds;
            this.relationManager = relationManager;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Ver informe" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int affaireId = affaireIds.get(currentRow);
                AffaireRelation.AffaireData data = relationManager.getRelations().get(affaireId);
                StringBuilder sb = new StringBuilder();
                sb.append("Affaire ID: ").append(affaireId).append("\n");
                sb.append("Ubicacion: ").append(data.ubicacion).append("\n");
                sb.append("Tipo Delito: ").append(data.tipoDelito).append("\n");
                sb.append("Fecha: ").append(data.fecha).append("\n\n");
                sb.append("Temoins:\n");
                data.temoins.forEach(t -> sb.append("  - ").append(t).append("\n"));
                sb.append("Preuves:\n");
                data.preuves.forEach(p -> sb.append("  - ").append(p).append("\n"));
                sb.append("Suspects:\n");
                data.suspects.forEach((s, palabras) -> sb.append("  - ").append(s).append(" (palabras clave: ").append(String.join(" ", palabras)).append(")\n"));
                JOptionPane.showMessageDialog(button, sb.toString(), "Informe detallado", JOptionPane.INFORMATION_MESSAGE);
            }
            isPushed = false;
            return label;
        }
    }
}



