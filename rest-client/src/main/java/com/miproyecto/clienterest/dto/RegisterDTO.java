package com.miproyecto.clienterest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterDTO {
	
	@NotBlank(message = "El nombre es obligatorio")
	private String name;
	
	@NotBlank(message = "El email es obligatorio")
	@Email(message = "Email no válido")
	private String email;
	
	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
	private String password;
	
	@NotBlank(message = "Confirma tu contraseña")
	private String confirmPassword;
//	
//	@NotBlank(message = "La respuesa es obligatoria")
//	private String answerOne;
//	
//	@NotBlank(message = "La respuesa es obligatoria")
//	private String answerTwo;
//	
//	@NotBlank(message = "La respuesa es obligatoria")
//	private String answerThree;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
//	public String getAnswerOne() {
//		return answerOne;
//	}
//	public void setAnswerOne(String answerOne) {
//		this.answerOne = answerOne;
//	}
//	public String getAnswerTwo() {
//		return answerTwo;
//	}
//	public void setAnswerTwo(String answerTwo) {
//		this.answerTwo = answerTwo;
//	}
//	public String getAnswerThree() {
//		return answerThree;
//	}
//	public void setAnswerThree(String answerThree) {
//		this.answerThree = answerThree;
//	}
	
	public RegisterDTO() {
		
	}
	
	public RegisterDTO(String name, String email, 
			String password, String confirmPassword) {
		super();
		this.name = name;
		this.email = email;
		this.password = password;
		this.confirmPassword = confirmPassword;
//		this.answerOne = answerOne;
//		this.answerTwo = answerTwo;
//		this.answerThree = answerThree;
	}
}
