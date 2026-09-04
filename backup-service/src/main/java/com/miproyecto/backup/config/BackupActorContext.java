package com.miproyecto.backup.config;

import org.springframework.stereotype.Component;

@Component
public class BackupActorContext {

    private static final ThreadLocal<String> ACTOR = new ThreadLocal<>();

    private static final String DEFAULT_ACTOR = "UNKNOWN";

    public void setActor(String actor) {
        ACTOR.set(sanitize(actor));
    }

    private String sanitize(String actor) {
        if (actor == null || actor.isBlank()) {
            return DEFAULT_ACTOR;
        }
        // Solo se permiten caracteres seguros (letras, digitos y unos pocos
        // simbolos). Se descarta cualquier otro caracter para que un valor
        // forjado del header X-Actor no pueda inyectar contenido arbitrario
        // en el log de auditoria.
        String cleaned = actor.replaceAll("[^A-Za-z0-9@._\\- ]", "").trim();
        if (cleaned.length() > 100) {
            cleaned = cleaned.substring(0, 100);
        }
        return cleaned.isBlank() ? DEFAULT_ACTOR : cleaned;
    }

    public String getActor() {
        String actor = ACTOR.get();
        return actor != null ? actor : DEFAULT_ACTOR;
    }

    public void clear() {
        ACTOR.remove();
    }
}
