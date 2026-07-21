package com.miproyecto.apirest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;


import com.miproyecto.apirest.model.Roles;

import com.miproyecto.apirest.repository.RolesRepository;

@Service
public class RolesService {
	private final RolesRepository roleRepo;
	public RolesService(RolesRepository rolesRepository) {this.roleRepo = rolesRepository;}

	public List<Roles> findAll(){return roleRepo.findAll();}
	public Roles findById(Integer id)
	{
		if(id == null || id < 1)
			return null;
		Optional<Roles> temp = roleRepo.findById(id);
		if(temp.isEmpty())
	        return null;

		return temp.get();
	}
	public Roles create(Roles role)
	{
		if(role == null)
			return null;
		return roleRepo.save(role);
		 
	}
	public Boolean delete(Integer id)
	{
		if (id == null || id < 1)
			return null;
		Optional<Roles> temp =  roleRepo.findById(id);
		if(temp.isEmpty())
			return false;
		roleRepo.delete(temp.get());
		return true;

	}
	public Roles findByName(String name)
	{
		if(name == null)
			return null;
		Optional<Roles> temp = roleRepo.findByName(name);
		if(temp.isEmpty())
			return null;
		
			
		return temp.get();
		
	}
}
