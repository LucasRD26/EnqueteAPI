package views;

import javax.swing.*;

import controllers.AffaireController;
import controllers.PreuveController;
import controllers.SuspectController;
import controllers.TemoinsController;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaginaPrincipal extends JFrame {
    private AffaireController affaireController;
    private SuspectController suspectController;
    private TemoinsController temoinsController;
    private PreuveController preuveController;

    public PaginaPrincipal(AffaireController affaireController, SuspectController suspectController, TemoinsController temoinsController, PreuveController preuveController) {
        this.affaireController = affaireController;
        this.suspectController = suspectController;
        this.temoinsController = temoinsController;
        this.preuveController = preuveController;
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Barra de menú
        JMenuBar menuBar = new JMenuBar();
        JMenu modeloMenu = new JMenu("Modelos");
        JMenuItem affaireItem = new JMenuItem("Affaires");
        affaireItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AffaireView(affaireController);
            }
        });

        modeloMenu.add(affaireItem);
        JMenuItem suspectItem = new JMenuItem("Suspects");
        suspectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SuspectView(suspectController);
            }
        });
        modeloMenu.add(suspectItem);

        JMenuItem temoinsItem = new JMenuItem("Testigos");
        temoinsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TemoinsView(temoinsController);
            }
        });
        modeloMenu.add(temoinsItem);

        JMenuItem preuveItem = new JMenuItem("Pruebas");
        preuveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PreuveView(preuveController);
            }
        });
        modeloMenu.add(preuveItem);

        menuBar.add(modeloMenu);

        JMenu funcionalidadesMenu = new JMenu("Funcionalidades");
        JMenuItem busquedaAvanzadaItem = new JMenuItem("Búsqueda Avanzada");
        busquedaAvanzadaItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BusquedaAvanzadaView(affaireController);
            }
        });
        funcionalidadesMenu.add(busquedaAvanzadaItem);

        JMenuItem analisisEnlacesItem = new JMenuItem("Análisis de Enlaces");
        analisisEnlacesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AnalisisEnlacesView(affaireController, suspectController, temoinsController, preuveController);
            }
        });
        funcionalidadesMenu.add(analisisEnlacesItem);

        menuBar.add(funcionalidadesMenu);

        setJMenuBar(menuBar);
        setVisible(true);
    }
}

