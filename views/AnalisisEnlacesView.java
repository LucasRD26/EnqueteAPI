package views;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import controllers.AffaireController;
import controllers.PreuveController;
import controllers.SuspectController;
import controllers.TemoinsController;
import models.Affaire;
import models.Preuve;
import models.Suspect;
import models.Temoins;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

public class AnalisisEnlacesView extends JFrame {
    private AffaireController affaireController;
    private SuspectController suspectController;
    private TemoinsController temoinsController;
    private PreuveController preuveController;

    public AnalisisEnlacesView(AffaireController affaireController, SuspectController suspectController, TemoinsController temoinsController, PreuveController preuveController) {
        this.affaireController = affaireController;
        this.suspectController = suspectController;
        this.temoinsController = temoinsController;
        this.preuveController = preuveController;

        // Crear un grafo
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        // Agregar nodos al grafo
        graph.getModel().beginUpdate();
        try {
            List<Suspect> suspects = suspectController.obtenerSuspects();
            List<Temoins> temoins = temoinsController.obtenerTemoins();
            List<Preuve> preuves = preuveController.obtenerPreuves();

            int x = 20;
            int y = 20;
            int ancho = 80;
            int alto = 30;

            for (Suspect suspect : suspects) {
                graph.insertVertex(parent, null, suspect.getNombre(), x, y, ancho, alto);
                x += ancho + 10; // Ajusta la posición x para el siguiente nodo
                if (x > 700) { // Ajusta para que no se salga de la ventana
                    x = 20;
                    y += alto + 10; // Pasa a la siguiente fila
                }
            }

            // Reiniciar posición para testigos
            x = 20;
            y += alto + 20; // Salto entre secciones

            for (Temoins temoins1 : temoins) {
                graph.insertVertex(parent, null, temoins1.getNombre(), x, y, ancho, alto);
                x += ancho + 10; // Ajusta la posición x para el siguiente nodo
                if (x > 700) { // Ajusta para que no se salga de la ventana
                    x = 20;
                    y += alto + 10; // Pasa a la siguiente fila
                }
            }

            // Reiniciar posición para pruebas
            x = 20;
            y += alto + 20; // Salto entre secciones

            for (Preuve preuve : preuves) {
                graph.insertVertex(parent, null, preuve.getTipo(), x, y, ancho, alto);
                x += ancho + 10; // Ajusta la posición x para el siguiente nodo
                if (x > 700) { // Ajusta para que no se salga de la ventana
                    x = 20;
                    y += alto + 10; // Pasa a la siguiente fila
                }
            }

            // Agregar aristas según las conexiones
            for (Object cell1 : graph.getChildCells(parent)) {
                for (Object cell2 : graph.getChildCells(parent)) {
                    if (cell1 != cell2) {
                        String texto1 = (String) graph.getModel().getValue(cell1);
                        String texto2 = (String) graph.getModel().getValue(cell2);

                        // Buscar similitudes entre los textos
                        if (tieneSimilitudes(texto1, texto2)) {
                            graph.insertEdge(parent, null, "Conexión", cell1, cell2);
                        }
                    }
                }
            }
        } finally {
            graph.getModel().endUpdate();
        }

        // Visualizar el grafo
        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        add(graphComponent);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private boolean tieneSimilitudes(String texto1, String texto2) {
        // Implementar lógica para buscar similitudes entre los textos
        // Por ejemplo, buscar palabras comunes
        String[] palabras1 = texto1.split("\\s+");
        String[] palabras2 = texto2.split("\\s+");

        for (String palabra1 : palabras1) {
            for (String palabra2 : palabras2) {
                if (palabra1.equalsIgnoreCase(palabra2)) {
                    return true;
                }
            }
        }
        return false;
    }
}


