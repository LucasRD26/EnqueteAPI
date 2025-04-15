package helpers;

import models.Affaire;
import models.Preuve;
import models.Suspect;
import models.Temoins;

import java.util.*;
import java.util.stream.Collectors;

public class AffaireRelation {
    // Casos abiertos y resueltos (corregido para persistencia)
    private final Map<Integer, AffaireData> openCases = new HashMap<>();
    private final Map<Integer, AffaireData> resolvedCases = new HashMap<>();
    private static final Set<String> CONECTORES = Set.of("de", "le", "la", "et", "du", "des", "un", "une", "en", "a");

    public static class AffaireData {
        public Set<String> temoins = new HashSet<>();
        public Set<String> preuves = new HashSet<>();
        public Map<String, Set<String>> suspects = new HashMap<>();
        public String guiltySuspect;
        public String ubicacion;
        public String tipoDelito;
        public String fecha;
    }

    // Mantener datos entre regeneraciones (corregido)
    public void addAffaire(Affaire affaire) {
        if (!openCases.containsKey(affaire.getId()) && !resolvedCases.containsKey(affaire.getId())) {
            AffaireData data = new AffaireData();
            data.ubicacion = affaire.getUbicacion();
            data.tipoDelito = affaire.getTipoDelito();
            data.fecha = affaire.getFecha();
            openCases.put(affaire.getId(), data);
        }
    }

    public void generarRelaciones(List<Temoins> temoins, List<Preuve> preuves, List<Suspect> suspects) {
        // Limpiar relaciones anteriores manteniendo casos resueltos
        openCases.values().forEach(data -> {
            data.temoins.clear();
            data.preuves.clear();
            data.suspects.clear();
        });

        // Procesar nuevas relaciones
        for (AffaireData data : openCases.values()) {
            // Témoins
            temoins.stream()
                .filter(t -> tieneRelacion(data, t.getDeclaracion()))
                .forEach(t -> data.temoins.add(t.getNombre()));

            // Preuves
            preuves.stream()
                .filter(p -> tieneRelacion(data, p.getDescripcion()) || tieneRelacion(data, p.getTipo()))
                .forEach(p -> data.preuves.add(p.getTipo()));

            // Suspects
            suspects.stream()
                .filter(s -> !data.suspects.containsKey(s.getNombre()))
                .forEach(s -> {
                    Set<String> palabrasClave = palabrasRelacion(data, s);
                    if (!palabrasClave.isEmpty()) {
                        data.suspects.put(s.getNombre(), palabrasClave);
                    }
                });
        }
    }

    public void resolverCaso(int affaireId, String culpable) {
        AffaireData data = openCases.remove(affaireId);
        if (data != null) {
            data.guiltySuspect = culpable;
            resolvedCases.put(affaireId, data);
        }
    }

    // Métodos auxiliares (sin cambios)
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

        for (String temoin : affaire.temoins) {
            Set<String> palabrasTemoin = filtrarPalabras(temoin);
            palabrasClave.addAll(palabrasSuspect.stream()
                .filter(palabrasTemoin::contains)
                .collect(Collectors.toSet()));
        }

        for (String preuve : affaire.preuves) {
            Set<String> palabrasPreuve = filtrarPalabras(preuve);
            palabrasClave.addAll(palabrasSuspect.stream()
                .filter(palabrasPreuve::contains)
                .collect(Collectors.toSet()));
        }
        return palabrasClave;
    }

    private Set<String> filtrarPalabras(String texto) {
        if (texto == null) return Set.of();
        return Arrays.stream(texto.toLowerCase().split("\\s+"))
            .filter(palabra -> !CONECTORES.contains(palabra))
            .collect(Collectors.toSet());
    }

    // Getters mejorados
    public Map<Integer, AffaireData> getOpenCases() {
        return Collections.unmodifiableMap(openCases);
    }

    public Map<Integer, AffaireData> getResolvedCases() {
        return Collections.unmodifiableMap(resolvedCases);
    }
}

