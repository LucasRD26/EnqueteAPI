import javax.swing.*;

import controllers.AffaireController;
import controllers.PreuveController;
import controllers.SuspectController;
import controllers.TemoinsController;
import helpers.LoginDialog;
import views.PaginaPrincipal;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame parentFrame = new JFrame();
            
            // Mostrar el diálogo de login
            LoginDialog loginDialog = new LoginDialog(parentFrame);
            loginDialog.setVisible(true);
            
            if (loginDialog.isSucceeded()) {
                String userRole = loginDialog.getUserRole();
                // Inicializar controladores
                AffaireController affaireController = new AffaireController();
                SuspectController suspectController = new SuspectController();
                TemoinsController temoinsController = new TemoinsController();
                PreuveController preuveController = new PreuveController();
                
                // Cargar datos iniciales
                String rutaData = "data/";
                affaireController.cargarDatosDesdeArchivo(rutaData + "affaires.txt");
                suspectController.cargarDatosDesdeArchivo(rutaData + "suspects.txt");
                temoinsController.cargarDatosDesdeArchivo(rutaData + "temoins.txt");
                preuveController.cargarDatosDesdeArchivo(rutaData + "preuves.txt");
                
                // Mostrar la ventana principal
                new PaginaPrincipal(
                    affaireController,
                    suspectController,
                    temoinsController,
                    preuveController,
                    userRole
                ).setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}

