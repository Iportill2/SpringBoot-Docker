package com.miproyecto.backup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.miproyecto.backup.config.BackupProperties;
import com.miproyecto.backup.config.MySqlProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    BackupProperties.class,
    MySqlProperties.class
})
public class BackupServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackupServiceApplication.class, args);
    }

}