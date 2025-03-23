package models;

import java.util.ArrayList;
import java.util.List;

public class Affaire {
    private int id;
    private String fecha;
    private String ubicacion;
    private String tipoDelito;
    private String estadoCaso;
    private List<Suspect> suspects;
    private List<Temoins> temoins;
    private List<Preuve> preuves;

    public Affaire(int id, String fecha, String ubicacion, String tipoDelito, String estadoCaso) {
        this.id = id;
        this.fecha = fecha;
        this.ubicacion = ubicacion;
        this.tipoDelito = tipoDelito;
        this.estadoCaso = estadoCaso;
        this.suspects = new ArrayList<>();
        this.temoins = new ArrayList<>();
        this.preuves = new ArrayList<>();
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getTipoDelito() { return tipoDelito; }
    public void setTipoDelito(String tipoDelito) { this.tipoDelito = tipoDelito; }
    public String getEstadoCaso() { return estadoCaso; }
    public void setEstadoCaso(String estadoCaso) { this.estadoCaso = estadoCaso; }
    public List<Suspect> getSuspects() { return suspects; }
    public void setSuspects(List<Suspect> suspects) { this.suspects = suspects; }
    public List<Temoins> getTemoins() { return temoins; }
    public void setTemoins(List<Temoins> temoins) { this.temoins = temoins; }
    public List<Preuve> getPreuves() { return preuves; }
    public void setPreuves(List<Preuve> preuves) { this.preuves = preuves; }

    // Métodos para agregar y eliminar suspects, temoins y preuves
    public void agregarSuspect(Suspect suspect) { this.suspects.add(suspect); }
    public void eliminarSuspect(Suspect suspect) { this.suspects.remove(suspect); }
    public void agregarTemoins(Temoins temoins) { this.temoins.add(temoins); }
    public void eliminarTemoins(Temoins temoins) { this.temoins.remove(temoins); }
    public void agregarPreuve(Preuve preuve) { this.preuves.add(preuve); }
    public void eliminarPreuve(Preuve preuve) { this.preuves.remove(preuve); }
}
