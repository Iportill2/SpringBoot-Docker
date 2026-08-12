package com.miproyecto.backup.config;

import org.springframework.stereotype.Component;

@Component
public class BackupActorContext {

    private static final ThreadLocal<String> ACTOR = new ThreadLocal<>();

    private static final String DEFAULT_ACTOR = "DESCONOCIDO";

    public void setActor(String actor) {
        ACTOR.set(actor != null && !actor.isBlank() ? actor : DEFAULT_ACTOR);
    }

    public String getActor() {
        String actor = ACTOR.get();
        return actor != null ? actor : DEFAULT_ACTOR;
    }

    public void clear() {
        ACTOR.remove();
    }
}
