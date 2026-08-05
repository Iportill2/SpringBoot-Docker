package com.miproyecto.apirest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miproyecto.apirest.model.UserQuestion;


@Repository
public interface UserQuestionRepository extends JpaRepository<UserQuestion, Integer>{
//	findAll()
//	findById()
//	save()
//	delete()
//	count()
//	existsById()
	List<UserQuestion> findByUserId(Integer userId);
	Optional<UserQuestion> findByUserIdAndQuestionId(Integer userId, Integer questionId);

}
