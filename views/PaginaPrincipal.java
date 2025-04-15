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
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class PaginaPrincipal extends JFrame {
    private AffaireController affaireController;
    private SuspectController suspectController;
    private TemoinsController temoinsController;
    private PreuveController preuveController;
    private JTable tablaResultados;
    private JTable tablaResueltos;
    private DefaultTableModel modelResueltos;
    private AffaireRelation relationManager = new AffaireRelation();

    public PaginaPrincipal(AffaireController affaireController, SuspectController suspectController,
                           TemoinsController temoinsController, PreuveController preuveController) {
        this.affaireController = affaireController;
        this.suspectController = suspectController;
        this.temoinsController = temoinsController;
        this.preuveController = preuveController;
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        // Configuración de la barra de menú
        JMenuBar menuBar = new JMenuBar();
        configurarMenu(menuBar);
        setJMenuBar(menuBar);

        // Panel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);

        // Panel superior (casos abiertos)
        JPanel openPanel = new JPanel(new BorderLayout());
        tablaResultados = new JTable();
        openPanel.add(new JScrollPane(tablaResultados), BorderLayout.CENTER);

        // Panel inferior (casos resueltos)
        JPanel resolvedPanel = new JPanel(new BorderLayout());
        modelResueltos = new DefaultTableModel(
            new String[]{"ID Affaire", "Culpable", "Pruebas", "Testimonios", "Fecha Resolucion"}, 0
        );
        tablaResueltos = new JTable(modelResueltos);
        resolvedPanel.add(new JLabel("Casos Resueltos"), BorderLayout.NORTH);
        resolvedPanel.add(new JScrollPane(tablaResueltos), BorderLayout.CENTER);

        splitPane.setTopComponent(openPanel);
        splitPane.setBottomComponent(resolvedPanel);

        // Botón de generación
        JButton btnGenerar = new JButton("Generar correspondencias");
        btnGenerar.addActionListener(e -> generarYMostrarCorrespondencias());

        JPanel topPanel = new JPanel();
        topPanel.add(btnGenerar);

        // Layout final
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void configurarMenu(JMenuBar menuBar) {
        // Menú Modelos
        JMenu modeloMenu = new JMenu("Modelos");
        modeloMenu.add(crearMenuItem("Affaires", () -> new AffaireView(affaireController)));
        modeloMenu.add(crearMenuItem("Suspects", () -> new SuspectView(suspectController)));
        modeloMenu.add(crearMenuItem("Testigos", () -> new TemoinsView(temoinsController)));
        modeloMenu.add(crearMenuItem("Pruebas", () -> new PreuveView(preuveController)));

        // Menú Funcionalidades
        JMenu funcMenu = new JMenu("Funcionalidades");
        funcMenu.add(crearMenuItem("Busqueda Avanzada", () -> new BusquedaAvanzadaView(affaireController)));
        funcMenu.add(crearMenuItem("Analisis de Enlaces", () ->
            new AnalisisEnlacesView(affaireController, suspectController, temoinsController, preuveController)));

        menuBar.add(modeloMenu);
        menuBar.add(funcMenu);
    }

    private JMenuItem crearMenuItem(String texto, Runnable accion) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> accion.run());
        return item;
    }

    private void generarYMostrarCorrespondencias() {
        // ¡NO reinicialices relationManager aquí!
        List<Affaire> affaires = affaireController.obtenerAffaires();
        List<Temoins> temoins = temoinsController.obtenerTemoins();
        List<Preuve> preuves = preuveController.obtenerPreuves();
        List<Suspect> suspects = suspectController.obtenerSuspects();

        
        affaires.forEach(relationManager::addAffaire);
        relationManager.generarRelaciones(temoins, preuves, suspects);

        // Configurar modelo para casos abiertos
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID Affaire", "Ubicacion", "Tipo Delito", "Testigos", "Pruebas", "Sospechosos", "Accion"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        relationManager.getOpenCases().forEach((id, data) -> {
            String suspectsStr = data.suspects.keySet().stream()
                .map(s -> s + " (" + String.join(" ", data.suspects.get(s)) + ")")
                .collect(Collectors.joining(", "));

            model.addRow(new Object[]{
                id,
                data.ubicacion,
                data.tipoDelito,
                String.join(", ", data.temoins),
                String.join(", ", data.preuves),
                suspectsStr,
                "Resolver caso"
            });
        });

        tablaResultados.setModel(model);
        tablaResultados.getColumn("Accion").setCellRenderer(new ButtonRenderer());
        tablaResultados.getColumn("Accion").setCellEditor(new ButtonEditor(new JCheckBox()));
        actualizarTablas();
    }

    private void actualizarTablas() {
        // Actualizar casos resueltos
        modelResueltos.setRowCount(0);
        relationManager.getResolvedCases().forEach((id, data) -> {
            modelResueltos.addRow(new Object[]{
                id,
                data.guiltySuspect,
                String.join(", ", data.preuves),
                String.join(", ", data.temoins),
                new java.util.Date().toString()
            });
        });
    }

    // Clases internas para el renderizado y edición de botones
    static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() { setOpaque(true); }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                      boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow;
        private int affaireId;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            affaireId = (int) table.getValueAt(row, 0);
            button.setText("Resolver caso");
            return button;
        }

        public Object getCellEditorValue() {
            AffaireRelation.AffaireData data = relationManager.getOpenCases().get(affaireId);

            JDialog dialog = new JDialog();
            dialog.setLayout(new BorderLayout());
            dialog.setSize(600, 500);

            // Panel de detalles
            JTextArea details = new JTextArea(construirDetalles(data));
            details.setEditable(false);

            // Selector de culpable y palabras clave
            JComboBox<String> combo = new JComboBox<>(data.suspects.keySet().toArray(new String[0]));
            JTextArea keywordsArea = new JTextArea();
            keywordsArea.setEditable(false);

            combo.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String selected = (String) e.getItem();
                    keywordsArea.setText("Palabras clave: " +
                            String.join(", ", data.suspects.get(selected)));
                }
            });

            // Selección inicial
            if (combo.getItemCount() > 0) {
                combo.setSelectedIndex(0);
                keywordsArea.setText("Palabras clave: " +
                        String.join(", ", data.suspects.get(combo.getSelectedItem())));
            }

            JPanel controls = new JPanel(new BorderLayout());
            JPanel comboPanel = new JPanel(new FlowLayout());
            comboPanel.add(new JLabel("Sospechoso culpable:"));
            comboPanel.add(combo);
            controls.add(comboPanel, BorderLayout.NORTH);
            controls.add(new JScrollPane(keywordsArea), BorderLayout.CENTER);

            JButton resolverBtn = new JButton("Confirmar resolución");
            resolverBtn.addActionListener(e -> {
                String culpable = (String) combo.getSelectedItem();
                relationManager.resolverCaso(affaireId, culpable);
                actualizarTablas();
                generarYMostrarCorrespondencias();
                dialog.dispose();
            });
            controls.add(resolverBtn, BorderLayout.SOUTH);

            dialog.add(new JScrollPane(details), BorderLayout.CENTER);
            dialog.add(controls, BorderLayout.SOUTH);
            dialog.setLocationRelativeTo(button);
            dialog.setVisible(true);

            return "Resuelto";
        }

        private String construirDetalles(AffaireRelation.AffaireData data) {
            return String.format(
                    "Ubicacion: %s\nTipo de delito: %s\nFecha: %s\n\nTestigos:\n- %s\n\nPruebas:\n- %s",
                    data.ubicacion,
                    data.tipoDelito,
                    data.fecha,
                    String.join("\n- ", data.temoins),
                    String.join("\n- ", data.preuves)
            );
        }
    }
}





