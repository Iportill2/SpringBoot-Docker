package com.miproyecto.backup.service;

import java.util.List;

import org.springframework.core.io.Resource;

import com.miproyecto.backup.model.BackupInfo;

public interface BackupService {

    String createBackup();
    List<BackupInfo> listBackups();
    Resource downloadBackup(String fileName);
}
