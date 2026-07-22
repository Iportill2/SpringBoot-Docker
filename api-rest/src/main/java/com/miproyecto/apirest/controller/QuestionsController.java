package com.miproyecto.apirest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.model.Questions;
import com.miproyecto.apirest.service.QuestionsService;


@RestController
@RequestMapping("/api/questions")
public class QuestionsController {
	private final QuestionsService questionsServ;
	public QuestionsController(QuestionsService questionsService) {this.questionsServ = questionsService;}
	
	@GetMapping
	public ResponseEntity<List<Questions>> findAll ()
	{
		List<Questions> temp = questionsServ.findAll();
		if(temp == null || temp.isEmpty())
			return ResponseEntity.noContent().build();
		return ResponseEntity.ok(temp);
	}
	@GetMapping("/{id}")
	public ResponseEntity<Questions> findById(@PathVariable Integer id)
	{
	    if(id == null || id < 1)
	        return ResponseEntity.badRequest().build();

	    Questions temp = questionsServ.findById(id);

	    if(temp == null)
	        return ResponseEntity.notFound().build();

	    return ResponseEntity.ok(temp);
	}
	@PostMapping
	public ResponseEntity<Questions>create(@PathVariable String question)
	{
		
		Questions temp = questionsServ.findByText(question);
		if(temp == null )
			return ResponseEntity.badRequest().build();
		return ResponseEntity.ok(temp);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> delete(@PathVariable Integer id)
	{
		if(id == null || id < 1)
			return ResponseEntity.badRequest().build();
	
			
		Boolean deleted = questionsServ.delete(id);
		return ResponseEntity.ok(deleted);
		

	}



}
