package com.miproyecto.backup;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.miproyecto.backup.config.BackupActorContext;

// Verifica que el actor reportado por la app cliente (header X-Actor) se
// sanee antes de escribirse en el log de auditoria: se descartan caracteres
// peligrosos (filtrado por lista blanca) y se limita la longitud.
class BackupActorContextTests {

    @Test
    void setActorStoresSanitizedValue() {
        BackupActorContext ctx = new BackupActorContext();
        ctx.setActor("test-user@empresa.com");
        assertEquals("test-user@empresa.com", ctx.getActor());
    }

    @Test
    void setActorDropsUnsafeCharacters() {
        BackupActorContext ctx = new BackupActorContext();
        // Se inyectan saltos de linea, tabulaciones y control chars: deben
        // eliminarse (evita log injection en el archivo de auditoria).
        ctx.setActor("admin\r\nDELETE\r\nX-Evil: 1");
        assertEquals("adminDELETEX-Evil 1", ctx.getActor());
    }

    @Test
    void setActorDefaultsToUnknownWhenBlank() {
        BackupActorContext ctx = new BackupActorContext();
        ctx.setActor("   ");
        assertEquals("UNKNOWN", ctx.getActor());
    }

    @Test
    void setActorTruncatesLongValues() {
        BackupActorContext ctx = new BackupActorContext();
        ctx.setActor("a".repeat(500));
        assertEquals(100, ctx.getActor().length());
    }
}
