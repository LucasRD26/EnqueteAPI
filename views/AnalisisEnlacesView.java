package views;

import com.mxgraph.layout.mxCircleLayout;
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
import java.util.Arrays;
import java.util.List;

public class AnalisisEnlacesView extends JFrame {

    private AffaireController affaireController;
    private SuspectController suspectController;
    private TemoinsController temoinsController;
    private PreuveController preuveController;

    public AnalisisEnlacesView(AffaireController affaireController, SuspectController suspectController,
                                TemoinsController temoinsController, PreuveController preuveController) {
        this.affaireController = affaireController;
        this.suspectController = suspectController;
        this.temoinsController = temoinsController;
        this.preuveController = preuveController;

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            List<Suspect> suspects = suspectController.obtenerSuspects();
            List<Temoins> temoins = temoinsController.obtenerTemoins();
            List<Preuve> preuves = preuveController.obtenerPreuves();

            // Crear nodos
            Object[] suspectNodes = new Object[suspects.size()];
            for (int i = 0; i < suspects.size(); i++) {
                Suspect suspect = suspects.get(i);
                suspectNodes[i] = graph.insertVertex(parent, null, suspect.getNombre(), 0, 0, 80, 30);
            }

            Object[] temoinsNodes = new Object[temoins.size()];
            for (int i = 0; i < temoins.size(); i++) {
                Temoins temoins1 = temoins.get(i);
                temoinsNodes[i] = graph.insertVertex(parent, null, temoins1.getNombre(), 0, 0, 80, 30);
            }

            Object[] preuvesNodes = new Object[preuves.size()];
            for (int i = 0; i < preuves.size(); i++) {
                Preuve preuve = preuves.get(i);
                preuvesNodes[i] = graph.insertVertex(parent, null, preuve.getTipo(), 0, 0, 80, 30);
            }

            // Crear aristas basadas en relaciones textuales
            for (int i = 0; i < suspects.size(); i++) {
                Suspect suspect = suspects.get(i);
                for (int j = 0; j < temoins.size(); j++) {
                    Temoins temoins1 = temoins.get(j);
                    if (tieneSimilitudes(suspect.getNombre(), temoins1.getDeclaracion()) ||
                            tieneSimilitudes(suspect.getHistorial(), temoins1.getDeclaracion())) {
                        graph.insertEdge(parent, null, "Suspect-Temoins", suspectNodes[i], temoinsNodes[j]);
                    }
                }
                for (int k = 0; k < preuves.size(); k++) {
                    Preuve preuve = preuves.get(k);
                    if (tieneSimilitudes(suspect.getNombre(), preuve.getDescripcion()) ||
                            tieneSimilitudes(suspect.getNombre(), preuve.getTipo()) ||
                            tieneSimilitudes(suspect.getHistorial(), preuve.getDescripcion())) {
                        graph.insertEdge(parent, null, "Suspect-Preuve", suspectNodes[i], preuvesNodes[k]);
                    }
                }
            }
        
        } finally {
            graph.getModel().endUpdate();
        }

        // Layout
        mxCircleLayout layout = new mxCircleLayout(graph);
        layout.setRadius(200); //Ajusta el radio del círculo
        layout.setDisableEdgeStyle(false);
        layout.execute(parent);

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private boolean tieneSimilitudes(String texto1, String texto2) {
        if (texto1 == null || texto2 == null) {
            return false;
        }

        String[] palabras1 = texto1.toLowerCase().split("\\s+");
        String[] palabras2 = texto2.toLowerCase().split("\\s+");
        List<String> list1 = Arrays.asList(palabras1);
        List<String> list2 = Arrays.asList(palabras2);

        for (String palabra1 : list1) {
            if (list2.contains(palabra1)) {
                return true;
            }
        }
        return false;
    }
}




