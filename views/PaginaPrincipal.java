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
    private String userRole;

    public PaginaPrincipal(AffaireController affaireController, SuspectController suspectController,
                           TemoinsController temoinsController, PreuveController preuveController,String userRole) {
        this.affaireController = affaireController;
        this.suspectController = suspectController;
        this.temoinsController = temoinsController;
        this.preuveController = preuveController;
        this.userRole = userRole;
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        configurarAccesosSegunRol();
        setVisible(true);
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
    private void configurarAccesosSegunRol() {
        // Añadir label de estado
        JLabel lblEstado = new JLabel("Conectado como: " + 
            (userRole.equals("admin") ? "Administrador" : "Oficial"));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(lblEstado, BorderLayout.EAST);
        getContentPane().add(panelSuperior, BorderLayout.NORTH);

        // Deshabilitar funcionalidades para oficiales
        if(!userRole.equals("admin")) {
            deshabilitarFuncionalidadesEdicion();
        }
    }

    private void deshabilitarFuncionalidadesEdicion() {
        // Deshabilitar botones de acción
        Component[] componentes = getContentPane().getComponents();
        for(Component comp : componentes) {
            if(comp instanceof JButton) {
                ((JButton)comp).setEnabled(false);
            }
        }

        // Deshabilitar menús sensibles
        JMenuBar menuBar = getJMenuBar();
        for(int i=0; i<menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if(menu.getText().equals("Modelos") || 
               menu.getText().equals("Análisis Avanzado")) {
                menu.setEnabled(false);
            }
        }
    }
    
    private void configurarMenu(JMenuBar menuBar) {
        // Menú Modelos
        JMenu modeloMenu = new JMenu("Modelos");
        modeloMenu.setEnabled(userRole.equals("admin"));
        modeloMenu.add(crearMenuItem("Affaires", () -> new AffaireView(affaireController)));
        modeloMenu.add(crearMenuItem("Suspects", () -> new SuspectView(suspectController)));
        modeloMenu.add(crearMenuItem("Testigos", () -> new TemoinsView(temoinsController)));
        modeloMenu.add(crearMenuItem("Pruebas", () -> new PreuveView(preuveController)));

        // Menú Funcionalidades
        JMenu funcMenu = new JMenu("Funcionalidades");
        funcMenu.add(crearMenuItem("Analyse des Liens", () -> 
        new AnalyseLiensView(suspectController, temoinsController, preuveController)));
        funcMenu.add(crearMenuItem("Busqueda Avanzada", () -> new BusquedaAvanzadaView(affaireController)));
        funcMenu.add(crearMenuItem("Analisis de Enlaces", () ->
            new AnalisisEnlacesView(suspectController, temoinsController, preuveController)));
        
        JMenu analisisMenu = new JMenu("Análisis Avanzado");
        analisisMenu.add(crearMenuItem("Priorización de Suspects", () -> 
            new PriorizacionSuspectsView(affaireController, suspectController)));

        menuBar.add(modeloMenu);
        menuBar.add(funcMenu);
        menuBar.add(analisisMenu);
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
        new String[]{"ID Affaire", "Ubicacion", "Tipo Delito", "Testigos", "Pruebas", "Sospechosos", "Accion", "Resolver caso automáticamente"}, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 || column == 7; // Columnas editables: Accion y Resolver automático
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
                "Resolver caso",
                "Resolver caso automáticamente"
            });
        });

        tablaResultados.setModel(model);
        tablaResultados.getColumn("Accion").setCellRenderer(new ButtonRenderer());
        tablaResultados.getColumn("Accion").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        tablaResultados.getColumn("Resolver caso automáticamente").setCellRenderer(new ButtonRenderer());
        tablaResultados.getColumn("Resolver caso automáticamente").setCellEditor(new AutoResolverButtonEditor(new JCheckBox()));

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
            setText(value.toString());
            setBackground(column == 6 ? Color.LIGHT_GRAY : Color.ORANGE); // Color diferenciado
            return this;
        }
    }
    class AutoResolverButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow;
    
        public AutoResolverButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Resolver caso automáticamente");
            button.addActionListener(e -> resolverCasoAutomaticamente());
        }
    
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }
    
        private void resolverCasoAutomaticamente() {

            if(!userRole.equals("admin")) {
                JOptionPane.showMessageDialog(button,
                    "Acceso restringido: Solo administradores pueden resolver casos automáticamente",
                    "Permiso denegado",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int affaireId = (int) tablaResultados.getValueAt(currentRow, 0);
            
            // Obtener suspect con máximo score
            Suspect topSuspect = suspectController.obtenerSuspects().stream()
                .max(Comparator.comparing(s -> calcularScore(
                    affaireController.obtenerAffaire(affaireId).orElseThrow(),
                    s
                )))
                .orElse(null);
    
            if(topSuspect != null) {
                relationManager.resolverCaso(affaireId, topSuspect.getNombre());
                ((DefaultTableModel)tablaResultados.getModel()).removeRow(currentRow);
                actualizarTablas();
                
                JOptionPane.showMessageDialog(button,
                    "Caso #" + affaireId + " resuelto automáticamente\n" +
                    "Culpable: " + topSuspect.getNombre(),
                    "Resolución Automática",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        }
    
        private int calcularScore(Affaire affaire, Suspect suspect) {
            // Usar misma lógica de cálculo que en PriorizacionSuspectsView
            int score = 0;
            
            // Coincidencias léxicas
            Set<String> palabrasAffaire = relationManager.filtrarPalabras(
                affaire.getUbicacion() + " " + affaire.getTipoDelito() + " " + affaire.getFecha()
            );
            Set<String> palabrasSuspect = relationManager.filtrarPalabras(
                suspect.getNombre() + " " + suspect.getHistorial()
            );
            score += (int) palabrasAffaire.stream()
                .filter(palabrasSuspect::contains)
                .count() * 5;
    
            // Relaciones existentes
            AffaireRelation.AffaireData data = relationManager.getOpenCases().get(affaire.getId());
            if(data != null && data.suspects.containsKey(suspect.getNombre())) {
                score += data.suspects.get(suspect.getNombre()).size() * 3;
            }
    
            // Casos resueltos relacionados
            score += relationManager.getResolvedCases().values().stream()
                .filter(d -> d.guiltySuspect != null && d.guiltySuspect.equals(suspect.getNombre()))
                .count() * 10;
    
            return score;
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

            if(!userRole.equals("admin")) {
                JOptionPane.showMessageDialog(button,
                    "Acceso restringido: Solo administradores pueden resolver casos",
                    "Permiso denegado",
                    JOptionPane.WARNING_MESSAGE);
                return "No autorizado";
            }

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





