package com.miproyecto.apirest.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "time_entries")
public class TimeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime startTime;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime endTime;

    @Column
    private Integer totalMinutesWorked;

    public TimeEntry() {
    }

    public TimeEntry(Integer id, Users user, LocalDate date, LocalDateTime startTime,
                      LocalDateTime endTime, Integer totalMinutesWorked) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalMinutesWorked = totalMinutesWorked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalMinutesWorked() {
        return totalMinutesWorked;
    }

    public void setTotalMinutesWorked(Integer totalMinutesWorked) {
        this.totalMinutesWorked = totalMinutesWorked;
    }

    @Override
    public String toString() {
        return "TimeEntry{" +
                "id=" + id +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}