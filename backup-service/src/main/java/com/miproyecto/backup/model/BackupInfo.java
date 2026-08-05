package com.miproyecto.backup.model;

import java.time.LocalDateTime;

public class BackupInfo {

    private String fileName;
    private long size;
    private LocalDateTime created;


    public BackupInfo(String fileName, long size, LocalDateTime created) {
        this.fileName = fileName;
        this.size = size;
        this.created = created;
    }


    public String getFileName() {
        return fileName;
    }


    public long getSize() {
        return size;
    }


    public LocalDateTime getCreated() {
        return created;
    }
}