package com.miproyecto.apirest.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.miproyecto.apirest.dto.UserCreateDTO;
import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.UsersRepository;




@Service
public class UserService {

    private final UsersRepository userRepo;
    private final RolesRepository roleRepo;
    public UserService(UsersRepository userRepository, RolesRepository roleRepository) 
    {
    	this.userRepo = userRepository;
    	this.roleRepo = roleRepository;
    }
    //Create
    public Users create(UserCreateDTO userDTO) {
    try {

        Users user = new Users();

        user.setUsername(userDTO.username());
        user.setPass(userDTO.password());
        user.setEmail(userDTO.email());

        user.setFails(0);
        user.setBlocked(false);
        user.setBanned(false);
        user.setSalt("salt_generada");
        user.setCode(UUID.randomUUID().toString());

        Roles role = roleRepo.findById(3).orElse(null);

        System.out.println("ROL = " + role);

        user.setRole(role);

        System.out.println("ANTES DEL SAVE");

        Users saved = userRepo.save(user);

        System.out.println("DESPUES DEL SAVE");

        return saved;

    } catch (Exception e) {
        e.printStackTrace();
        throw e;
    }
    }
    //Read
    public List<Users> findAll() {return userRepo.findAll();}
    
    public Optional<Users>findByIdOptional(Integer id)
    {
    	if(id == null || id < 1)
            return Optional.empty();
    	// Si el usuario no existe, devuelve Optional.empty() no un NULL como en c++
    	// Sino tendras el user
    	return  userRepo.findById(id);
    }
    public Optional<Users> findByEmail(String email)
    {
    	if(email == null)
    		return Optional.empty();
    	return  userRepo.findByEmail(email);
    }


    public Users findByUsername(String username)
    {
    	if(username == null)
    		return null;
    	Optional<Users>temp = userRepo.findByUser(username);
    	if(temp.isEmpty())
    		return null;
    	return temp.get();
    }

    public Boolean existsByUsername(String username) 
    {
    	if(username == null)
    		return null;
        return userRepo.existsByUsername(username);
    }
    public Boolean existsByEmail(String email) 
    {
    	if(email == null)
    		return null;
        return userRepo.existsByEmail(email);
    }
    public Boolean isBlocked(Users user)
    {
    	Optional<Users> temp = userRepo.findById(user.getId());
    	if(temp.isEmpty())
    		return null;
    	
    	if ( temp.get().isBlocked() == true)
    		return true;

    	return false;
    }
    public Boolean isBanned(Users user)
    {
    	Optional<Users> temp = userRepo.findById(user.getId());
    	if(temp.isEmpty())
    		return null;
    	
    	if ( temp.get().isBanned() == true)
    		return true;

    	return false;
    }
    //Update
    public Users update(Integer id, Users user)
    {
    	if(user == null || id < 1)
    		return null;
        if (!userRepo.existsById(id))//verificamos si el id existe en la BD
            return null;
        user.setId(id);
    	return userRepo.save(user);
    }
    //Delete
    public Boolean delete(Integer id)
    {
    	 
    	if(id < 1 )
    		return false;
    	Optional<Users> temp = userRepo.findById(id);
    	if(temp.isEmpty())
    		return null;
    	userRepo.delete(temp.get());//hacemos esto para pasar de Optional<Users> a Users
    	return true;
    }
    
}