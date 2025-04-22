package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import models.Suspect;

public class SuspectController {
    private List<Suspect> suspects;

    public SuspectController() {
        this.suspects = new ArrayList<>();
        // Cargar datos desde un archivo txt o fuente de datos
    }

    public void cargarDatosDesdeArchivo(String rutaArchivo) {
        try (Scanner scanner = new Scanner(new File(rutaArchivo))) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                // Procesar cada línea para crear objetos de Suspect
                // Suponiendo que cada línea tenga el formato: id,nombre,edad,historial
                String[] partes = linea.split(",");
                if (partes.length == 4) {
                    Suspect suspect = new Suspect(
                            Integer.parseInt(partes[0]),
                            partes[1],
                            Integer.parseInt(partes[2]),
                            partes[3]
                    );
                    agregarSuspect(suspect);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado.");
        }
    }


    public void agregarSuspect(Suspect suspect) {
        suspects.add(suspect);
    }

    public void eliminarSuspect(int id) {
        suspects.removeIf(s -> s.getId() == id);
    }

    public void actualizarSuspect(Suspect suspect) {
        for (Suspect s : suspects) {
            if (s.getId() == suspect.getId()) {
                s.setNombre(suspect.getNombre());
                s.setEdad(suspect.getEdad());
                s.setHistorial(suspect.getHistorial());
                break;
            }
        }
    }

    public List<Suspect> obtenerSuspects() {
        return suspects;
    }

    public Suspect obtenerSuspectPorId(int id) {
        for (Suspect suspect : suspects) {
            if (suspect.getId() == id) {
                return suspect;
            }
        }
        return null;
    }

    // Método para buscar suspects por nombre o edad
    public List<Suspect> buscarSuspects(String nombre, int edad) {
        List<Suspect> resultados = new ArrayList<>();
        for (Suspect suspect : suspects) {
            if ((nombre.isEmpty() || suspect.getNombre().contains(nombre))
                    && (edad == 0 || suspect.getEdad() == edad)) {
                resultados.add(suspect);
            }
        }
        return resultados;
    }

    public Optional<Suspect> obtenerSuspect(String nombre) {
    return suspects.stream()
        .filter(s -> s.getNombre().equals(nombre))
        .findFirst();
}

}

