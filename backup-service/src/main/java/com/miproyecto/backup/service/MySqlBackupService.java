package com.miproyecto.backup.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.miproyecto.backup.config.BackupProperties;
import com.miproyecto.backup.config.MySqlProperties;
import com.miproyecto.backup.model.BackupInfo;

@Service
public class MySqlBackupService implements BackupService {

    private final BackupProperties backupProperties;
    private final MySqlProperties mySqlProperties;


    public MySqlBackupService(
            BackupProperties backupProperties,
            MySqlProperties mySqlProperties) {

        this.backupProperties = backupProperties;
        this.mySqlProperties = mySqlProperties;
    }


    @Override
    public String createBackup() {

        String fileName =
                "aplicacion_"
                + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
                + ".sql.gz";


        String path =
                backupProperties.getDirectory()
                + "/"
                + fileName;


        String command = String.format(
                "set -o pipefail; mysqldump -h %s -P %d -u %s -p%s %s | gzip > %s",
                mySqlProperties.getHost(),
                mySqlProperties.getPort(),
                mySqlProperties.getUser(),
                mySqlProperties.getPassword(),
                mySqlProperties.getDatabase(),
                path
        );


        ProcessBuilder builder =
                new ProcessBuilder(
                        "sh",
                        "-c",
                        command
                );


        try {

            Process process = builder.start();


            StringBuilder error = new StringBuilder();


            try (BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    process.getErrorStream()))) {


                String line;

                while ((line = reader.readLine()) != null) {

                    error.append(line)
                         .append("\n");
                }
            }


            int exitCode = process.waitFor();


            if (exitCode != 0) {

                throw new RuntimeException(
                        "Error creando backup:\n"
                        + error
                );
            }


            return path;


        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo ejecutar mysqldump",
                    e
            );


        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "El proceso de backup fue interrumpido",
                    e
            );
        }
    }
    @Override
    public List<BackupInfo> listBackups() {

        List<BackupInfo> backups = new ArrayList<>();

        Path directory = Path.of(
                backupProperties.getDirectory()
        );

        try {

            Files.list(directory)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".sql.gz"))
                    .forEach(path -> {

                        try {

                            backups.add(
                                new BackupInfo(
                                    path.getFileName().toString(),
                                    Files.size(path),
                                    Files.getLastModifiedTime(path)
                                         .toInstant()
                                         .atZone(
                                             java.time.ZoneId.systemDefault()
                                         )
                                         .toLocalDateTime()
                                )
                            );

                        } catch (Exception e) {
                            throw new RuntimeException(
                                "Error leyendo backup: "
                                + path.getFileName(),
                                e
                            );
                        }

                    });


        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo leer el directorio de backups",
                    e
            );
        }


        return backups;
    }
    @Override
    public Resource downloadBackup(String fileName) {

        Path file = Path.of(
                backupProperties.getDirectory(),
                fileName
        );


        if (!Files.exists(file)) {

            throw new RuntimeException(
                    "El backup no existe: " + fileName
            );
        }


        return new FileSystemResource(file);
    }
    


    @Override
    public Boolean restoreBackup(String fileName) {

        Path file = Path.of(
                backupProperties.getDirectory(),
                fileName
        );

        if (!Files.exists(file)) {
            return false;
        }

        String createDatabaseCommand = String.format(
                "mysql -h %s -P %d -u %s -p%s -e \"CREATE DATABASE IF NOT EXISTS %s\"",
                mySqlProperties.getHost(),
                mySqlProperties.getPort(),
                mySqlProperties.getUser(),
                mySqlProperties.getPassword(),
                mySqlProperties.getDatabase()
        );

        String restoreCommand = String.format(
                "gunzip -c %s | mysql -h %s -P %d -u %s -p%s %s",
                file.toAbsolutePath(),
                mySqlProperties.getHost(),
                mySqlProperties.getPort(),
                mySqlProperties.getUser(),
                mySqlProperties.getPassword(),
                mySqlProperties.getDatabase()
        );

        try {

            String createError = ejecutarComando(createDatabaseCommand);

            if (createError != null) {
                throw new RuntimeException(
                        "Error verificando la base de datos:\n"
                        + createError
                );
            }

            String restoreError = ejecutarComando(restoreCommand);

            if (restoreError != null) {
                throw new RuntimeException(
                        "Error restaurando el backup "
                        + fileName + ":\n"
                        + restoreError
                );
            }

            return true;

        } catch (IOException e) {

            throw new RuntimeException(
                    "No se pudo ejecutar mysql",
                    e
            );

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(
                    "La restauración fue interrumpida",
                    e
            );
        }
    }

    private String ejecutarComando(String command)
            throws IOException, InterruptedException {

        Process process = new ProcessBuilder(
                "sh",
                "-c",
                command
        ).start();

        StringBuilder error = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                error.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            return error.toString();
        }

        return null;
    }
}