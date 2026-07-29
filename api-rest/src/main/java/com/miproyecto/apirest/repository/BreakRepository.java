package com.miproyecto.apirest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miproyecto.apirest.model.Break;

@Repository
public interface BreakRepository extends JpaRepository<Break, Integer> {
	
	public List<Break> findByTimeEntryId(Integer timeEntryId);
}