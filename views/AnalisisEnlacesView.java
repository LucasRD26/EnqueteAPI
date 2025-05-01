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

public class AnalisisEnlacesView extends JFrame {
    private static final Set<String> CONECTORES = Set.of(
        "de", "le", "la", "et", "du", "des", "un", "une", "en", "a", "les", "que", "el", "los"
    );
    
    private SuspectController suspectController;
    private TemoinsController temoinsController;
    private PreuveController preuveController;
    private JTable tablaPrincipal;
    private DefaultTableModel model;

    public AnalisisEnlacesView(SuspectController suspectController, 
                              TemoinsController temoinsController, 
                              PreuveController preuveController) {
        this.suspectController = suspectController;
        this.temoinsController = temoinsController;
        this.preuveController = preuveController;
        
        setTitle("Liens entre Suspects");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        initUI();
        cargarDatos();
    }

    private void initUI() {
        model = new DefaultTableModel(new Object[]{"Suspect", "Suspects Associes", "Details"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        tablaPrincipal = new JTable(model);
        tablaPrincipal.getColumn("Details").setCellRenderer(new ButtonRenderer());
        tablaPrincipal.getColumn("Details").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(tablaPrincipal);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void cargarDatos() {
        List<Suspect> suspects = suspectController.obtenerSuspects();
        List<Temoins> temoins = temoinsController.obtenerTemoins();
        List<Preuve> preuves = preuveController.obtenerPreuves();

        Map<String, Map<String, Map<String, Set<String>>>> relaciones = new HashMap<>();

        // Preprocesar palabras para todos los suspects
        Map<String, Set<String>> palabrasSuspects = new HashMap<>();
        for (Suspect s : suspects) {
            palabrasSuspects.put(s.getNombre(), filtrarPalabras(s.getNombre() + " " + s.getHistorial()));
        }

        // Construir relaciones
        for (Suspect s1 : suspects) {
            Map<String, Map<String, Set<String>>> asociados = new HashMap<>();
            Set<String> palabrasS1 = palabrasSuspects.get(s1.getNombre());

            for (Suspect s2 : suspects) {
                if (s1 == s2) continue;
                
                Set<String> palabrasS2 = palabrasSuspects.get(s2.getNombre());
                Map<String, Set<String>> categorias = new HashMap<>();

                // Relaciones a través de pruebas
                Set<String> palabrasPreuve = new HashSet<>();
                for (Preuve p : preuves) {
                    Set<String> palabrasP = filtrarPalabras(p.getDescripcion() + " " + p.getTipo());
                    if (!Collections.disjoint(palabrasS1, palabrasP) && 
                        !Collections.disjoint(palabrasS2, palabrasP)) {
                        palabrasPreuve.addAll(palabrasS2);
                    }
                }

                // Relaciones a través de testigos
                Set<String> palabrasTemoin = new HashSet<>();
                for (Temoins t : temoins) {
                    Set<String> palabrasT = filtrarPalabras(t.getDeclaracion());
                    if (!Collections.disjoint(palabrasS1, palabrasT) && 
                        !Collections.disjoint(palabrasS2, palabrasT)) {
                        palabrasTemoin.addAll(palabrasS2);
                    }
                }

                if (!palabrasPreuve.isEmpty() || !palabrasTemoin.isEmpty()) {
                    if (!palabrasPreuve.isEmpty()) categorias.put("preuve", palabrasPreuve);
                    if (!palabrasTemoin.isEmpty()) categorias.put("temoin", palabrasTemoin);
                    asociados.put(s2.getNombre(), categorias);
                }
            }

            relaciones.put(s1.getNombre(), asociados);
            model.addRow(new Object[]{
                s1.getNombre(), 
                String.join(", ", asociados.keySet()), 
                "Voir details"
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
            setText("Voir details");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Voir details");
            button.addActionListener(e -> mostrarDetalles());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        private void mostrarDetalles() {
            String suspectPrincipal = (String) model.getValueAt(currentRow, 0);
            
            JDialog dialog = new JDialog(AnalisisEnlacesView.this, "Relations pour " + suspectPrincipal, true);
            dialog.setSize(800, 600);
            dialog.setLayout(new BorderLayout());
        
            // 1. Panel izquierdo: Lista de asociados
            JList<String> listaAsociados = new JList<>();
            DefaultListModel<String> listModel = new DefaultListModel<>();
            
            // 2. Panel derecho: Detalles de relaciones
            JTextArea detalles = new JTextArea();
            detalles.setEditable(false);
        
            // 3. Cargar datos del suspect principal
            Map<String, Map<String, Set<String>>> relaciones = new HashMap<>();
            List<Suspect> suspects = suspectController.obtenerSuspects();
            
            // Lógica para cargar relaciones (similar a cargarDatos() pero para 1 suspect)
            Suspect s1 = suspects.stream()
                .filter(s -> s.getNombre().equals(suspectPrincipal))
                .findFirst()
                .orElse(null);
            
            if (s1 != null) {
                Set<String> palabrasS1 = filtrarPalabras(s1.getNombre() + " " + s1.getHistorial());
                
                for (Suspect s2 : suspects) {
                    if (s1.equals(s2)) continue;
                    
                    Set<String> palabrasS2 = filtrarPalabras(s2.getNombre() + " " + s2.getHistorial());
                    Map<String, Set<String>> categorias = new HashMap<>();
        
                    // Relaciones por pruebas
                    Set<String> preuveWords = new HashSet<>();
                    for (Preuve p : preuveController.obtenerPreuves()) {
                        Set<String> palabrasP = filtrarPalabras(p.getDescripcion() + " " + p.getTipo());
                        preuveWords.addAll(interseccion(palabrasS1, palabrasP));
                        preuveWords.addAll(interseccion(palabrasS2, palabrasP));
                    }
        
                    // Relaciones por testigos
                    Set<String> temoinWords = new HashSet<>();
                    for (Temoins t : temoinsController.obtenerTemoins()) {
                        Set<String> palabrasT = filtrarPalabras(t.getDeclaracion());
                        temoinWords.addAll(interseccion(palabrasS1, palabrasT));
                        temoinWords.addAll(interseccion(palabrasS2, palabrasT));
                    }
        
                    if (!preuveWords.isEmpty()) categorias.put("preuve", preuveWords);
                    if (!temoinWords.isEmpty()) categorias.put("temoin", temoinWords);
                    
                    if (!categorias.isEmpty()) {
                        relaciones.put(s2.getNombre(), categorias);
                        listModel.addElement(s2.getNombre());
                    }
                }
            }
        
            listaAsociados.setModel(listModel);
            
            // 4. Listener para selección
            listaAsociados.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String asociado = listaAsociados.getSelectedValue();
                    if (asociado != null) {
                        Map<String, Set<String>> categorias = relaciones.get(asociado);
                        StringBuilder sb = new StringBuilder();
                        categorias.forEach((categoria, palabras) -> {
                            sb.append("Relations pour ")
                              .append(categoria.toUpperCase())
                              .append(":\n")
                              .append(String.join("\n", palabras))
                              .append("\n\n");
                        });
                        detalles.setText(sb.toString());
                    }
                }
            });
        
            // 5. Configurar layout
            JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(listaAsociados),
                new JScrollPane(detalles)
            );
            splitPane.setDividerLocation(250);
            dialog.add(splitPane, BorderLayout.CENTER);
            dialog.setLocationRelativeTo(AnalisisEnlacesView.this);
            dialog.setVisible(true); // ¡Importante!
        }
        
        // Método auxiliar para intersección de conjuntos
        private Set<String> interseccion(Set<String> set1, Set<String> set2) {
            Set<String> result = new HashSet<>(set1);
            result.retainAll(set2);
            return result;
        }
    }
}






