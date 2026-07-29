package com.miproyecto.clienterest.model;

import java.time.LocalDateTime;


public class BackupInfo {


    private String fileName;

    private long size;

    private LocalDateTime created;


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public long getSize() {
        return size;
    }


    public void setSize(long size) {
        this.size = size;
    }


    public LocalDateTime getCreated() {
        return created;
    }


    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}