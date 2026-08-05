package com.miproyecto.apirest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miproyecto.apirest.model.Questions;
import com.miproyecto.apirest.model.Users;
import java.util.List;



@Repository
public interface QuestionsRepository extends JpaRepository<Questions, Integer>{
//	findAll()
//	findById()
//	save()
//	delete()
//	count()
//	existsById()
	Optional<Questions> findByText(String text);
	Optional<Questions> findById(Integer id);
}
