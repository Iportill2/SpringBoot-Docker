package com.miproyecto.apirest.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.miproyecto.apirest.model.TimeEntry;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.TimeEntryRepository;
import com.miproyecto.apirest.repository.UsersRepository;

@Service
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final UsersRepository userRepository;

    public TimeEntryService(TimeEntryRepository timeEntryRepository,
            UsersRepository userRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.userRepository = userRepository;
    }

    public Optional<TimeEntry> findOpenByUser(Integer userId) {
        return timeEntryRepository.findByUserIdAndEndTimeIsNull(userId);
    }

    public TimeEntry startEntry(Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Optional<TimeEntry> jornadaAbierta = timeEntryRepository.findByUserIdAndEndTimeIsNull(userId);
        if (jornadaAbierta.isPresent()) {
            throw new RuntimeException("Ya tienes una jornada abierta sin cerrar");
        }

        TimeEntry entry = new TimeEntry();
        entry.setUser(user);
        entry.setDate(LocalDate.now());
        entry.setStartTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        return timeEntryRepository.save(entry);
    }

    public Optional<TimeEntry> findTodayByUser(Integer userId) {
        return timeEntryRepository.findByUserIdAndDate(userId, LocalDate.now());
    }

    public TimeEntry stopEntry(Integer timeEntryId, int pauseMinutes) {
        TimeEntry entry = timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new RuntimeException("Fichaje no encontrado"));

        entry.setEndTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        long totalMinutes = Duration.between(entry.getStartTime(), entry.getEndTime()).toMinutes();
        entry.setTotalMinutesWorked((int) (totalMinutes - pauseMinutes));

        return timeEntryRepository.save(entry);
    }

    public List<TimeEntry> findByUserAndMonth(Integer userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        return timeEntryRepository.findByUserIdAndDateBetween(userId, start, end);
    }
}