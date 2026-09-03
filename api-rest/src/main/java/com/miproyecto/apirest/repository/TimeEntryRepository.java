package com.miproyecto.apirest.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miproyecto.apirest.model.TimeEntry;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Integer> {

	List<TimeEntry> findByUserIdAndDateBetween(Integer userId, LocalDate start, LocalDate end);
	Optional<TimeEntry> findByUserIdAndEndTimeIsNull(Integer userId);
	Optional<TimeEntry> findByUserIdAndDate(Integer userId, LocalDate date);
	void deleteByUserId(Integer userId);
}
