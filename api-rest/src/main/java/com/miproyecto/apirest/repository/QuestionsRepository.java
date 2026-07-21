package com.miproyecto.apirest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miproyecto.apirest.model.Questions;


@Repository
public interface QuestionsRepository extends JpaRepository<Questions, Integer>{
//	findAll()
//	findById()
//	save()
//	delete()
//	count()
//	existsById()
}
