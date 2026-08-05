package com.miproyecto.apirest.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.miproyecto.apirest.model.Break;
import com.miproyecto.apirest.model.TimeEntry;
import com.miproyecto.apirest.repository.BreakRepository;
import com.miproyecto.apirest.repository.TimeEntryRepository;

@Service
public class BreakService {

	private final BreakRepository breakRepo;
	private final TimeEntryRepository timeEntryRepo;
	
	public BreakService(BreakRepository breakRepo, TimeEntryRepository timeEntryRepo) {
		this.breakRepo = breakRepo;
		this.timeEntryRepo = timeEntryRepo;
	}
	
	public Break startBreak(Integer timeEntryId) {
		
		TimeEntry timeTemp = timeEntryRepo.findById(timeEntryId)
				.orElseThrow(() -> new RuntimeException("TimeEntry no encontrada"));
		
		Break newBreak = new Break();
		newBreak.setTimeEntry(timeTemp);
		newBreak.setStartTime(LocalDateTime.now());
		
		return breakRepo.save(newBreak);
		
	}
	
	public Break endBreak(Integer breakId) {
		
		Break breakTemp = breakRepo.findById(breakId)
				.orElseThrow(() -> new RuntimeException("pausa no encontrada"));
		
		breakTemp.setEndTime(LocalDateTime.now());
		
		return breakRepo.save(breakTemp);
		
	}
	
	public List<Break> findByTimeEntryId(Integer timeEntryId) {
	    return breakRepo.findByTimeEntryId(timeEntryId);
	}
}
