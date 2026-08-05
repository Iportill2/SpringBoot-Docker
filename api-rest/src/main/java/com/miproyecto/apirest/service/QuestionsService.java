package com.miproyecto.apirest.service;

import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;

import com.miproyecto.apirest.model.Questions;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.QuestionsRepository;

@Service
public class QuestionsService  {
	private final QuestionsRepository QRepo;
	public QuestionsService(QuestionsRepository questionsRepository) {this.QRepo = questionsRepository;}

	public Questions create(Questions question) {

	    if (question == null) {
	        return null;
	    }

	    return QRepo.save(question);
	}
	public Questions update(Integer id, Questions question) {

	    if (id == null || id < 1 || question == null) {
	        return null;
	    }

	    Questions existing = QRepo.findById(id).orElse(null);

	    if (existing == null) {
	        return null;
	    }

	    // Actualizar los campos
	    existing.setId(id);
	    existing.setText(question.getText());


	    return QRepo.save(existing);
	}
	public List<Questions> findAll(){return QRepo.findAll();}
	public Questions findById(Integer id)
	{
		if(id < 1)
			return null;
		Optional<Questions> temp = QRepo.findById(id);
		return temp.get();
	}
	public Questions findByText(String text)
	{
		if(text == null || text.isBlank())
			return null;
		Optional<Questions> temp = QRepo.findByText(text);
		return temp.get();
	}
	public Boolean delete(Integer Id)
	{
		if(Id <1)
			return null;
		Optional<Questions> temp = QRepo.findById(Id);
		if(temp.isEmpty())
			return false;
		QRepo.delete(temp.get());
		return true;
	}

}
