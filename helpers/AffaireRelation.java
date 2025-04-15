package helpers;

import java.util.*;

import models.Affaire;
import models.Preuve;
import models.Suspect;
import models.Temoins;

public class AffaireRelation {
    private Map<Integer, AffaireData> relations = new HashMap<>();
    private static final Set<String> CONECTORES = Set.of("de", "le", "la", "et", "du", "des", "un", "une", "en", "a", "d");

    public static class AffaireData {
        public Set<String> temoins = new HashSet<>();
        public Set<String> preuves = new HashSet<>();
        // Mapa: nombre del suspect -> palabras clave de la relación
        public Map<String, Set<String>> suspects = new HashMap<>();
        public String ubicacion;
        public String tipoDelito;
        public String fecha;
    }

    public void addAffaire(Affaire affaire) {
        AffaireData data = new AffaireData();
        data.ubicacion = affaire.getUbicacion();
        data.tipoDelito = affaire.getTipoDelito();
        data.fecha = affaire.getFecha();
        relations.put(affaire.getId(), data);
    }

    public void generarRelaciones(List<Temoins> temoins, List<Preuve> preuves, List<Suspect> suspects) {
        for (AffaireData affaire : relations.values()) {
            // Témoins
            for (Temoins temoin : temoins) {
                if (tieneRelacion(affaire, temoin.getDeclaracion())) {
                    affaire.temoins.add(temoin.getNombre());
                }
            }
            // Preuves
            for (Preuve preuve : preuves) {
                if (tieneRelacion(affaire, preuve.getDescripcion()) || tieneRelacion(affaire, preuve.getTipo())) {
                    affaire.preuves.add(preuve.getTipo());
                }
            }
            // Suspects
            for (Suspect suspect : suspects) {
                Set<String> palabrasClave = palabrasRelacion(affaire, suspect);
                if (!palabrasClave.isEmpty()) {
                    affaire.suspects.put(suspect.getNombre(), palabrasClave);
                }
            }
        }
    }

    private boolean tieneRelacion(AffaireData affaire, String texto) {
        Set<String> palabrasAffaire = filtrarPalabras(affaire.ubicacion + " " + affaire.tipoDelito + " " + affaire.fecha);
        Set<String> palabrasTexto = filtrarPalabras(texto);
        return !Collections.disjoint(palabrasAffaire, palabrasTexto);
    }

    private Set<String> palabrasRelacion(AffaireData affaire, Suspect suspect) {
        Set<String> palabrasAffaire = filtrarPalabras(affaire.ubicacion + " " + affaire.tipoDelito + " " + affaire.fecha);
        Set<String> palabrasSuspect = filtrarPalabras(suspect.getNombre() + " " + suspect.getHistorial());
        Set<String> palabrasClave = new HashSet<>(palabrasAffaire);
        palabrasClave.retainAll(palabrasSuspect);

        // También buscar coincidencias con temoins y preuves relacionados
        for (String temoin : affaire.temoins) {
            Set<String> palabrasTemoin = filtrarPalabras(temoin);
            Set<String> inter = new HashSet<>(palabrasSuspect);
            inter.retainAll(palabrasTemoin);
            palabrasClave.addAll(inter);
        }
        for (String preuve : affaire.preuves) {
            Set<String> palabrasPreuve = filtrarPalabras(preuve);
            Set<String> inter = new HashSet<>(palabrasSuspect);
            inter.retainAll(palabrasPreuve);
            palabrasClave.addAll(inter);
        }
        return palabrasClave;
    }

    private Set<String> filtrarPalabras(String texto) {
        if (texto == null) return Set.of();
        String[] palabras = texto.toLowerCase().split("\\s+");
        Set<String> resultado = new HashSet<>();
        for (String palabra : palabras) {
            if (!CONECTORES.contains(palabra)) resultado.add(palabra);
        }
        return resultado;
    }

    public Map<Integer, AffaireData> getRelations() {
        return relations;
    }
}
