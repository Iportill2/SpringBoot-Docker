package com.miproyecto.apirest.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.miproyecto.apirest.model.Break;
import com.miproyecto.apirest.model.TimeEntry;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.BreakRepository;
import com.miproyecto.apirest.repository.TimeEntryRepository;
import com.miproyecto.apirest.repository.UsersRepository;

@Service
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final BreakRepository breakRepository;
    private final UsersRepository userRepository;

    public TimeEntryService(TimeEntryRepository timeEntryRepository,
                            BreakRepository breakRepository,
                            UsersRepository userRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.breakRepository = breakRepository;
        this.userRepository = userRepository;
    }

    public TimeEntry startEntry(Integer userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        TimeEntry entry = new TimeEntry();
        entry.setUser(user);
        entry.setDate(LocalDate.now());
        entry.setStartTime(LocalDateTime.now());

        return timeEntryRepository.save(entry);
    }

    public Break addBreak(Integer timeEntryId) {
        TimeEntry entry = timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new RuntimeException("Fichaje no encontrado"));

        Break newBreak = new Break();
        newBreak.setTimeEntry(entry);
        newBreak.setStartTime(LocalDateTime.now());

        return breakRepository.save(newBreak);
    }

    public Break endBreak(Integer breakId) {
        Break existingBreak = breakRepository.findById(breakId)
                .orElseThrow(() -> new RuntimeException("Pausa no encontrada"));

        existingBreak.setEndTime(LocalDateTime.now());

        return breakRepository.save(existingBreak);
    }

    public TimeEntry stopEntry(Integer timeEntryId, List<Break> breaks) {
        TimeEntry entry = timeEntryRepository.findById(timeEntryId)
                .orElseThrow(() -> new RuntimeException("Fichaje no encontrado"));

        entry.setEndTime(LocalDateTime.now());

        long totalBreakMinutes = 0;
        for (Break b : breaks) {
            if (b.getStartTime() != null && b.getEndTime() != null) {
                totalBreakMinutes += Duration.between(b.getStartTime(), b.getEndTime()).toMinutes();
            }
        }

        long totalMinutes = Duration.between(entry.getStartTime(), entry.getEndTime()).toMinutes();
        entry.setTotalMinutesWorked((int) (totalMinutes - totalBreakMinutes));

        return timeEntryRepository.save(entry);
    }
}