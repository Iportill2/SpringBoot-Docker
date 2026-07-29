package com.miproyecto.clienterest.dto;

public class TimeEntryDTO {
    private Integer id;
    private String date;
    private String startTime;
    private String endTime;
    private Integer totalMinutesWorked;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public Integer getTotalMinutesWorked() { return totalMinutesWorked; }
    public void setTotalMinutesWorked(Integer totalMinutesWorked) { this.totalMinutesWorked = totalMinutesWorked; }
}