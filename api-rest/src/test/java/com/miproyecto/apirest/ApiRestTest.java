package com.miproyecto.apirest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(statements = {
        "INSERT INTO roles (id, name) VALUES (1, 'EMPLEADO')",
        "INSERT INTO roles (id, name) VALUES (2, 'ADMIN')",
        "INSERT INTO roles (id, name) VALUES (3, 'PENDIENTE')"
})
public @interface ApiRestTest {
}
