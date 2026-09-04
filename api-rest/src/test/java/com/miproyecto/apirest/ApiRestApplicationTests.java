package com.miproyecto.apirest;

import org.junit.jupiter.api.Test;

/**
 * Prueba de humo: comprueba que el contexto de Spring arranca sin errores.
 *
 * <p>Usa la misma anotacion compuesta {@link ApiRestTest} que el resto de
 * tests de integracion para que toda la suite comparta UN UNICO contexto
 * (y una unica base H2 en memoria). De este modo, el {@code data-test.sql}
 * se ejecuta una sola vez y no hay conflictos por varias bases en memoria
 * compartiendo el mismo nombre ({@code mem:testdb}).</p>
 */
@ApiRestTest
class ApiRestApplicationTests {

    @Test
    void contextLoads() {
    }

}
