package models;

import java.util.ArrayList;
import java.util.List;

public class Temoins {
    private int id;
    private String nombre;
    private int edad;
    private String declaracion;
    private List<Affaire> affaires;

    public Temoins(int id, String nombre, int edad, String declaracion) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
        this.declaracion = declaracion;
        this.affaires = new ArrayList<>();
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getDeclaracion() { return declaracion; }
    public void setDeclaracion(String declaracion) { this.declaracion = declaracion; }
    public List<Affaire> getAffaires() { return affaires; }
    public void setAffaires(List<Affaire> affaires) { this.affaires = affaires; }

    // Método para agregar un affaire al testigo
    public void agregarAffaire(Affaire affaire) { this.affaires.add(affaire); }
}

