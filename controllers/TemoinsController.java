package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import models.Temoins;

public class TemoinsController {
    private List<Temoins> temoins;

    public TemoinsController() {
        this.temoins = new ArrayList<>();
        // Cargar datos desde un archivo txt o fuente de datos
    }

    public void cargarDatosDesdeArchivo(String rutaArchivo) {
        try (Scanner scanner = new Scanner(new File(rutaArchivo))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                // Procesar cada línea para crear objetos de Temoins
                // Suponiendo que cada línea tenga el formato: id,nombre,edad,declaracion
                String[] partes = linea.split(",");
                if (partes.length == 4) {
                    Temoins temoins = new Temoins(
                            Integer.parseInt(partes[0]),
                            partes[1],
                            Integer.parseInt(partes[2]),
                            partes[3]
                    );
                    agregarTemoins(temoins);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado.");
        }
    }


    public void agregarTemoins(Temoins temoins) {
        this.temoins.add(temoins);
    }

    public void eliminarTemoins(int id) {
        temoins.removeIf(t -> t.getId() == id);
    }

    public void actualizarTemoins(Temoins temoins) {
        for (Temoins t : this.temoins) {
            if (t.getId() == temoins.getId()) {
                t.setNombre(temoins.getNombre());
                t.setEdad(temoins.getEdad());
                t.setDeclaracion(temoins.getDeclaracion());
                break;
            }
        }
    }

    public List<Temoins> obtenerTemoins() {
        return temoins;
    }

    public Temoins obtenerTemoinsPorId(int id) {
        for (Temoins temoins : this.temoins) {
            if (temoins.getId() == id) {
                return temoins;
            }
        }
        return null;
    }

    // Método para buscar testigos por nombre o edad
    public List<Temoins> buscarTemoins(String nombre, int edad) {
        List<Temoins> resultados = new ArrayList<>();
        for (Temoins temoins : this.temoins) {
            if ((nombre.isEmpty() || temoins.getNombre().contains(nombre))
                    && (edad == 0 || temoins.getEdad() == edad)) {
                resultados.add(temoins);
            }
        }
        return resultados;
    }
}

