package com.miproyecto.apirest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.miproyecto.apirest.model.UserQuestion;
import com.miproyecto.apirest.repository.UserQuestionRepository;

@Service
public class UserQuestionService {
	private final UserQuestionRepository userQRepo;
	public UserQuestionService(UserQuestionRepository userQuestionRepository) {this.userQRepo = userQuestionRepository;}

	public List<UserQuestion> findAll(){return userQRepo.findAll();}
	public List<UserQuestion> findByUser(Integer id)
	{
	    if(   id ==null || id < 1)
	    	return List.of();
	   return  userQRepo.findByUserId(id);
	}
	public UserQuestion create(UserQuestion userQuestion) 
	{
		if(userQuestion == null)
			return null;
		return userQRepo.save(userQuestion);
	}
	public Boolean checkAnswer (Integer userId,Integer questionId,String answer)
	{
	    if(userId == null || questionId == null || answer == null)
	        return null;

		Optional<UserQuestion> temp =userQRepo.findByUserIdAndQuestionId(userId,questionId);
		if(temp.isEmpty())
			return null;
		if(temp.get().getAnswer().equals(answer))
			return true;
		return false;
	}
}
