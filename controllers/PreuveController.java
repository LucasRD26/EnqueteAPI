package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import models.Preuve;

public class PreuveController {
    private List<Preuve> preuves;

    public PreuveController() {
        this.preuves = new ArrayList<>();
        // Cargar datos desde un archivo txt o fuente de datos
    }

    public void cargarDatosDesdeArchivo(String rutaArchivo) {
        try (Scanner scanner = new Scanner(new File(rutaArchivo))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                // Procesar cada línea para crear objetos de Preuve
                // Suponiendo que cada línea tenga el formato: id,tipo,descripcion
                String[] partes = linea.split(",");
                if (partes.length == 3) {
                    Preuve preuve = new Preuve(
                            Integer.parseInt(partes[0]),
                            partes[1],
                            partes[2]
                    );
                    agregarPreuve(preuve);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado.");
        }
    }


    public void agregarPreuve(Preuve preuve) {
        preuves.add(preuve);
    }

    public void eliminarPreuve(int id) {
        preuves.removeIf(p -> p.getId() == id);
    }

    public void actualizarPreuve(Preuve preuve) {
        for (Preuve p : preuves) {
            if (p.getId() == preuve.getId()) {
                p.setTipo(preuve.getTipo());
                p.setDescripcion(preuve.getDescripcion());
                break;
            }
        }
    }

    public List<Preuve> obtenerPreuves() {
        return preuves;
    }

    public Preuve obtenerPreuvePorId(int id) {
        for (Preuve preuve : preuves) {
            if (preuve.getId() == id) {
                return preuve;
            }
        }
        return null;
    }

    // Método para buscar pruebas por tipo
    public List<Preuve> buscarPreuves(String tipo) {
        List<Preuve> resultados = new ArrayList<>();
        for (Preuve preuve : preuves) {
            if (tipo.isEmpty() || preuve.getTipo().equals(tipo)) {
                resultados.add(preuve);
            }
        }
        return resultados;
    }
}

