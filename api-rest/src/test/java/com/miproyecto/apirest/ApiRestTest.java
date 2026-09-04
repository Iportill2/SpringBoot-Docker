package com.miproyecto.apirest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Anotacion compuesta para todos los tests de integracion de la API.
 *
 * <p>Centraliza la configuracion comun de cada test:</p>
 * <ul>
 *   <li><b>@{@link SpringBootTest}</b>: arranca el contexto completo de
 *       Spring (controladores, servicios, repositorios y seguridad).</li>
 *   <li><b>@{@link AutoConfigureMockMvc}</b>: inyecta el cliente
 *       {@code MockMvc} para invocar los endpoints sin levantar un
 *       servidor real.</li>
 *   <li><b>@{@link ActiveProfiles}("test")</b>: activa el perfil de
 *       prueba, que usa una base H2 en memoria y el script
 *       {@code data-test.sql} (datos maestros propios, sin tocar el
 *       {@code data.sql} de produccion).</li>
 *   <li><b>@{@link Transactional}</b>: cada test corre dentro de una
 *       transaccion que se revierte al terminar, de modo que los datos
 *       que crea un test no contaminan al resto (tests aislados).</li>
 * </ul>
 *
 * <p>Los roles base (1=EMPLEADO, 2=ADMIN, 3=PENDIENTE) ya los carga
 * {@code data-test.sql} al crear el contexto, por lo que no hace falta
 * insertarlos aqui (y asi se evita el choque de clave primaria).</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public @interface ApiRestTest {
}
