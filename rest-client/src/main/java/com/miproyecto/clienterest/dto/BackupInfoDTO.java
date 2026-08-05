package com.miproyecto.clienterest.dto;

import java.time.LocalDateTime;

public class BackupInfoDTO {

    private String fileName;

    private long size;

    private LocalDateTime created;


    public BackupInfoDTO() {
    }


    public BackupInfoDTO(String fileName, long size, LocalDateTime created) {
        this.fileName = fileName;
        this.size = size;
        this.created = created;
    }


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