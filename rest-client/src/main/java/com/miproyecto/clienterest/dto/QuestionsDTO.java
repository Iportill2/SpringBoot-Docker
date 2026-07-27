package com.miproyecto.clienterest.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Valid
public class QuestionsDTO {

	
	@NotBlank(message = "La respuesa es obligatoria")
	private String answerOne;
	
	@NotBlank(message = "La respuesa es obligatoria")
	private String answerTwo;
	
	@NotBlank(message = "La respuesa es obligatoria")
	private String answerThree;


	public String getAnswerOne() {
		return answerOne;
	}
	public void setAnswerOne(String answerOne) {
		this.answerOne = answerOne;
	}
	public String getAnswerTwo() {
		return answerTwo;
	}
	public void setAnswerTwo(String answerTwo) {
		this.answerTwo = answerTwo;
	}
	public String getAnswerThree() {
		return answerThree;
	}
	public void setAnswerThree(String answerThree) {
		this.answerThree = answerThree;
	}
	
	public QuestionsDTO() {
		
	}
	
	public QuestionsDTO(String answerOne,String answerTwo,String answerThree) {
		super();

		this.answerOne = answerOne;
		this.answerTwo = answerTwo;
		this.answerThree = answerThree;
	}
}
