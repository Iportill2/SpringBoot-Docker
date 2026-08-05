package com.miproyecto.apirest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.dto.CheckAnswerRequest;
import com.miproyecto.apirest.dto.UserQuestionDTO;
import com.miproyecto.apirest.model.UserQuestion;

import com.miproyecto.apirest.service.UserQuestionService;


@RestController
@RequestMapping("/api/userquestion")
public class UserQuestionController {
	private final UserQuestionService userQuestionServ;
	public UserQuestionController(UserQuestionService userQuestionService) {this.userQuestionServ = userQuestionService ;}
	//Create
	@PostMapping
	public ResponseEntity<UserQuestion> create (@RequestBody UserQuestion userQuestion)
	{
		UserQuestion temp = userQuestionServ.create(userQuestion);
		if(temp == null)
			return ResponseEntity.badRequest().build();
		return ResponseEntity.ok(temp);
	}
	//Create from DTO
	@PostMapping("/from-dto")
	public ResponseEntity<UserQuestion> createFromDTO(@RequestBody UserQuestionDTO dto)
	{
		UserQuestion temp = userQuestionServ.createFromDTO(dto);
		if(temp == null)
			return ResponseEntity.badRequest().build();
		return ResponseEntity.status(HttpStatus.CREATED).body(temp);
	}
	//Read
	@GetMapping
	public ResponseEntity<List<UserQuestion>> findAll()
	{
		List<UserQuestion> temp = userQuestionServ.findAll();
		if(temp == null || temp.isEmpty())
			return ResponseEntity.noContent().build();
		 return ResponseEntity.ok(temp);
		 

	}
	
	@GetMapping("/user/{id}")
	public ResponseEntity<List<UserQuestion>> findByUser(@PathVariable Integer id)
	{
	    if(id == null || id < 1)
	        return ResponseEntity.badRequest().build();

	    List<UserQuestion> temp = userQuestionServ.findByUser(id);

	    if(temp.isEmpty())
	        return ResponseEntity.notFound().build();

	    return ResponseEntity.ok(temp);
	}
	@PostMapping("/check")
	public ResponseEntity<Boolean> checkAnswer(@RequestBody CheckAnswerRequest request)
	{
	    Boolean result = userQuestionServ.checkAnswer(
	            request.userId(),
	            request.questionId(),
	            request.answer()
	    );

	    if (result == null)
	        return ResponseEntity.notFound().build();

	    return ResponseEntity.ok(result);
	}
	@GetMapping("/{id}")
	public ResponseEntity<UserQuestion> findById(@PathVariable Integer id)
	{
	    if(id == null || id < 1)
	        return ResponseEntity.badRequest().build();

	    Optional<UserQuestion> temp = userQuestionServ.findById(id);

	    if(temp.isEmpty())
	        return ResponseEntity.notFound().build();

	    return ResponseEntity.ok(temp.get());
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> delete(@PathVariable Integer id)
	{
	    Boolean result = userQuestionServ.delete(id);

	    if(result == null)
	        return ResponseEntity.notFound().build();

	    return ResponseEntity.ok(result);
	}

}
