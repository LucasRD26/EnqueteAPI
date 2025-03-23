import javax.swing.*;

import controllers.AffaireController;
import controllers.PreuveController;
import controllers.SuspectController;
import controllers.TemoinsController;
import views.PaginaPrincipal;

public class Main {
    public static void main(String[] args) {
        // Inicializar controladores
        AffaireController affaireController = new AffaireController();
        SuspectController suspectController = new SuspectController();
        TemoinsController temoinsController = new TemoinsController();
        PreuveController preuveController = new PreuveController();

        // Cargar datos desde archivos o fuentes de datos
        // Especificar la ruta a la carpeta data
        String rutaData = "data/";

        affaireController.cargarDatosDesdeArchivo(rutaData + "affaires.txt");
        suspectController.cargarDatosDesdeArchivo(rutaData + "suspects.txt");
        temoinsController.cargarDatosDesdeArchivo(rutaData + "temoins.txt");
        preuveController.cargarDatosDesdeArchivo(rutaData + "preuves.txt");

        // Iniciar la aplicación
        SwingUtilities.invokeLater(() -> {
            new PaginaPrincipal(affaireController, suspectController, temoinsController, preuveController);
        });
    }
}

