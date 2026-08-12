package com.miproyecto.backup.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.miproyecto.backup.config.BackupActorContext;

@Component
@ConditionalOnProperty(
        prefix = "backup",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class BackupScheduler {

    private static final Logger log = LoggerFactory.getLogger(BackupScheduler.class);

    private final BackupService backupService;
    private final BackupActorContext actorContext;

    public BackupScheduler(
            BackupService backupService,
            BackupActorContext actorContext) {
        this.backupService = backupService;
        this.actorContext = actorContext;
    }

    @Scheduled(cron = "${backup.cron}")
    public void scheduledBackup() {
        try {
            actorContext.setActor("SYSTEM (programado)");
            String file = backupService.createBackup();
            int deleted = backupService.cleanupOldBackups();
            log.info(
                    "Backup automático creado: {} (retención eliminó {} archivos)",
                    file,
                    deleted
            );
        } catch (Exception e) {
            log.error("Error en el backup automático", e);
        } finally {
            actorContext.clear();
        }
    }
}
