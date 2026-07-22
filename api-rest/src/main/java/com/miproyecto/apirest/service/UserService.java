package com.miproyecto.apirest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.UsersRepository;




@Service
public class UserService {

    private final UsersRepository userRepo;
    public UserService(UsersRepository userRepository) {this.userRepo = userRepository;}
    //Create
    public Users create(Users user)
    {
    	if(user == null)
    		return null;
    	user.setId(null);
    	return userRepo.save(user);
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



    public Users findByUsername(String username)
    {
    	if(username == null)
    		return null;
    	Optional<Users>temp = userRepo.findByUser(username);
    	if(temp.isEmpty())
    		return null;
    	return temp.get();
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