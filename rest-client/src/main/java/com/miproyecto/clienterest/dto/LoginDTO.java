package com.miproyecto.clienterest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginDTO {

	@NotBlank(message = "El usuario es obligatorio")
	private String username;

	@NotBlank(message = "La contraseña es obligatoria")
	@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
	private String pass;

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}

	public LoginDTO() {
	}

	public LoginDTO(String username, String pass) {
		this.username = username;
		this.pass = pass;
	}
}