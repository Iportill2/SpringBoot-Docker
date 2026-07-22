package com.miproyecto.apirest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.service.RolesService;

@RestController
public class RolesController {
	private final RolesService roleServ;
	public RolesController(RolesService rolesService) {this.roleServ = rolesService;}
	
	
	public ResponseEntity<List<Roles>> findAll()
	{
		List<Roles> temp = roleServ.findAll();
		if(temp == null || temp.isEmpty())
			return ResponseEntity.noContent().build();
		return ResponseEntity.ok(temp);
	}
	public ResponseEntity<Roles> findById(Integer id)
	{
		if(id == null )
			return ResponseEntity.badRequest().build();
		Optional<Roles> temp = roleServ.findById(id);
		if(temp == null )
			return ResponseEntity.badRequest().build();
		else if(temp.isEmpty())
			return ResponseEntity.noContent().build();
		return ResponseEntity.ok(temp.get());
	}
	public ResponseEntity<Roles> create(String role)
	{
		if(role == null)
			ResponseEntity.badRequest().build();
		
		Roles temp = roleServ.findByName(role);
		
		roleServ.create(temp);
		return ResponseEntity.ok(temp);
	}
	public ResponseEntity<Boolean> delete(Integer id)
	{
		if(id == null )
			ResponseEntity.badRequest().build();
		Boolean temp = roleServ.delete(id);
		
		return ResponseEntity.ok(temp);
	}
	public ResponseEntity<Roles> findByName(String name)
	{
		if(name == null || name.isBlank())
			return ResponseEntity.badRequest().build();
		Roles temp = roleServ.findByName(name);
		if(temp == null)
			return ResponseEntity.notFound().build();
		return ResponseEntity.ok(temp);
		return null;
	}



}
