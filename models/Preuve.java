package models;

import java.util.ArrayList;
import java.util.List;

public class Preuve {
    private int id;
    private String tipo;
    private String descripcion;
    private List<Affaire> affaires;

    public Preuve(int id, String tipo, String descripcion) {
        this.id = id;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.affaires = new ArrayList<>();
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public List<Affaire> getAffaires() { return affaires; }
    public void setAffaires(List<Affaire> affaires) { this.affaires = affaires; }

    // Método para agregar un affaire a la prueba
    public void agregarAffaire(Affaire affaire) { this.affaires.add(affaire); }
}

