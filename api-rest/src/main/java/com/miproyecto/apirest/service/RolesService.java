package com.miproyecto.apirest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.UsersRepository;

@Service
public class RolesService {
	private final RolesRepository roleRepo;
	private final UsersRepository userRepo;
	public RolesService(RolesRepository rolesRepository, UsersRepository userRepo) {
		this.roleRepo = rolesRepository;
		this.userRepo = userRepo;}

	public List<Roles> findAll(){return roleRepo.findAll();}
	public Optional<Roles> findById(Integer id)
	{
	    if(id == null || id < 1)
	        return Optional.empty();

	    Optional<Roles> temp = roleRepo.findById(id);

	    if(temp.isEmpty())
	        return Optional.empty();

	    return temp;
	}
	public Roles create(Roles role)
	{
		if(role == null)
			return null;
		return roleRepo.save(role);
		 
	}
	
	@Transactional
	public Boolean delete(Integer id)
	{
		if (id == null || id < 1)
			return null;
		Optional<Roles> temp =  roleRepo.findById(id);
		if(temp.isEmpty())
			return false;
		
		List<Users> usersConEseRol = userRepo.findByRoleId(id);
		
		for (Users u : usersConEseRol) {
			u.setRole(null);
			userRepo.save(u);
		}
		
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
