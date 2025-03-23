package models;

import java.util.ArrayList;
import java.util.List;

public class Suspect {
    private int id;
    private String nombre;
    private int edad;
    private String historial;
    private List<Affaire> affaires;

    public Suspect(int id, String nombre, int edad, String historial) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.historial = historial;
        this.affaires = new ArrayList<>();
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getHistorial() { return historial; }
    public void setHistorial(String historial) { this.historial = historial; }
    public List<Affaire> getAffaires() { return affaires; }
    public void setAffaires(List<Affaire> affaires) { this.affaires = affaires; }

    // Método para agregar un affaire al suspect
    public void agregarAffaire(Affaire affaire) { this.affaires.add(affaire); }
}

