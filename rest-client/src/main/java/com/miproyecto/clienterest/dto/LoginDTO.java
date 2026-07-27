package com.miproyecto.clienterest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginDTO {
	
	@NotBlank(message = "El email es obligatorio")
	@Email(message = "Email no válido")
	private String email;
	
	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
	private String password;
	

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
	
	public LoginDTO() {
		
	}
	
	public LoginDTO(String email, String password) {
		this.email = email;
		this.password = password;
	}
	
}
