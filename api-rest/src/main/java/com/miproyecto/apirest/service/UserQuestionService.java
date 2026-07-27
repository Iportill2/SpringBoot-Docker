package com.miproyecto.apirest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.miproyecto.apirest.dto.UserQuestionDTO;
import com.miproyecto.apirest.model.Questions;
import com.miproyecto.apirest.model.UserQuestion;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.QuestionsRepository;
import com.miproyecto.apirest.repository.UserQuestionRepository;
import com.miproyecto.apirest.repository.UsersRepository;

@Service
public class UserQuestionService {
	private final UserQuestionRepository userQRepo;
	private final UsersRepository userRepo;
	private final QuestionsRepository questionRepo;

	public UserQuestionService(UserQuestionRepository userQuestionRepository,
	                           UsersRepository usersRepository,
	                           QuestionsRepository questionsRepository) {
		this.userQRepo = userQuestionRepository;
		this.userRepo = usersRepository;
		this.questionRepo = questionsRepository;
	}

	public List<UserQuestion> findAll(){return userQRepo.findAll();}
	public Optional<UserQuestion> findById(Integer id)
	{
		if(id == null || id < 1)
			return Optional.empty();
		
		return userQRepo.findById(id);
	}
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
	public UserQuestion createFromDTO(UserQuestionDTO dto) {
		if (dto == null || dto.userId() == null || dto.questionId() == null || dto.answer() == null)
			return null;

		Optional<Users> userOpt = userRepo.findById(dto.userId());
		Optional<Questions> questionOpt = questionRepo.findById(dto.questionId());

		if (userOpt.isEmpty() || questionOpt.isEmpty())
			return null;

		UserQuestion uq = new UserQuestion();
		uq.setUser(userOpt.get());
		uq.setQuestion(questionOpt.get());
		uq.setAnswer(dto.answer());

		return userQRepo.save(uq);
	}

	public Boolean delete(Integer id)
	{
	    if(id == null || id < 1)
	        return false;

	    Optional<UserQuestion> temp = userQRepo.findById(id);

	    if(temp.isEmpty())
	        return null;

	    userQRepo.delete(temp.get());
	    return true;
	}
}
