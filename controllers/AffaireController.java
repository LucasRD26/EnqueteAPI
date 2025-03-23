package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import models.Affaire;

public class AffaireController {
    private List<Affaire> affaires;

    public AffaireController() {
        this.affaires = new ArrayList<>();
        // Cargar datos desde un archivo txt o fuente de datos
    }

    public void cargarDatosDesdeArchivo(String rutaArchivo) {
        try (Scanner scanner = new Scanner(new File(rutaArchivo))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                // Procesar cada línea para crear objetos de Affaire
                // Suponiendo que cada línea tenga el formato: id,fecha,ubicacion,tipoDelito,estadoCaso
                String[] partes = linea.split(",");
                if (partes.length == 5) {
                    Affaire affaire = new Affaire(
                            Integer.parseInt(partes[0]),
                            partes[1],
                            partes[2],
                            partes[3],
                            partes[4]
                    );
                    agregarAffaire(affaire);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado.");
        }
    }

    public void agregarAffaire(Affaire affaire) {
        affaires.add(affaire);
    }

    public void eliminarAffaire(int id) {
        affaires.removeIf(a -> a.getId() == id);
    }

    public void actualizarAffaire(Affaire affaire) {
        for (Affaire a : affaires) {
            if (a.getId() == affaire.getId()) {
                a.setFecha(affaire.getFecha());
                a.setUbicacion(affaire.getUbicacion());
                a.setTipoDelito(affaire.getTipoDelito());
                a.setEstadoCaso(affaire.getEstadoCaso());
                break;
            }
        }
    }

    public List<Affaire> obtenerAffaires() {
        return affaires;
    }

    public Affaire obtenerAffairePorId(int id) {
        for (Affaire affaire : affaires) {
            if (affaire.getId() == id) {
                return affaire;
            }
        }
        return null;
    }

    // Método para buscar affaires por criterios avanzados
    public List<Affaire> buscarAffairesAvanzado(String fecha, String ubicacion, String tipoDelito) {
        List<Affaire> resultados = new ArrayList<>();
        for (Affaire affaire : affaires) {
            if ((fecha.isEmpty() || affaire.getFecha().equals(fecha))
                    && (ubicacion.isEmpty() || affaire.getUbicacion().equals(ubicacion))
                    && (tipoDelito.isEmpty() || affaire.getTipoDelito().equals(tipoDelito))) {
                resultados.add(affaire);
            }
        }
        return resultados;
    }
}

