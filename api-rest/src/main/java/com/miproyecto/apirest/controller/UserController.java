package com.miproyecto.apirest.controller;


import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miproyecto.apirest.dto.UserCreateDTO;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.service.UserService;





@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }
    //Create
    @PostMapping
    public ResponseEntity<Users> create(@RequestBody UserCreateDTO userDTO) {

        Users createdUser = userService.create(userDTO);

        if (createdUser == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdUser);
    }
    //Read
    @GetMapping
    public ResponseEntity<List<Users>> findAll()
    {
        List<Users> temp = userService.findAll();
        if(temp.isEmpty())
        	return ResponseEntity.noContent().build();

        return ResponseEntity.ok(temp);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Users> findByIdOptional(@PathVariable Integer id)
    {
    	if(id < 1)
    		return ResponseEntity.badRequest().build();
    	Optional<Users> temp = userService.findByIdOptional(id);
    	if(temp.isEmpty())
    		return ResponseEntity.notFound().build();
        return ResponseEntity.ok(temp.get());
    }
    @GetMapping("/email/{email}")
    public ResponseEntity<Users>findByEmail(@PathVariable String email)
    {
    	if(email == null || email.isBlank())
    		return ResponseEntity.badRequest().build();
    	Optional<Users> temp = userService.findByEmail(email);
    	if(temp.isEmpty())
    		return ResponseEntity.notFound().build();
    	return ResponseEntity.ok(temp.get());
    }
    @GetMapping("/name/{username}")
    public ResponseEntity<Users>findByUsername(@PathVariable String username)
    {
    	if(username == null || username.isBlank())
    		return ResponseEntity.badRequest().build();
    	Users temp = userService.findByUsername(username);
    	if(temp == null)
    		return ResponseEntity.notFound().build();
    	return ResponseEntity.ok(temp);
    }
    @GetMapping("/name/exist/{username}")
    public ResponseEntity<Boolean> existsByUsername(@PathVariable String username)
    {
    	if(username == null || username.isBlank())
    		return ResponseEntity.badRequest().build();
    	Boolean temp = userService.existsByUsername(username);
    	
    	return ResponseEntity.ok(temp);
    }
    @GetMapping("/email/exist/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email)
    {
    	if(email == null || email.isBlank())
    		return ResponseEntity.badRequest().build();
    	Boolean temp = userService.existsByEmail(email);
    	
    	return ResponseEntity.ok(temp);
    }

    
    
    @GetMapping("/blocked/{username}")
    public ResponseEntity<Boolean> isBlocked(@PathVariable String username)
    {
    	if(username == null || username.isBlank())
    		return ResponseEntity.ok(false);
    	Users user = userService.findByUsername(username);
    	if (user == null ) 
    		return ResponseEntity.ok(false);
    	
    	Boolean temp = userService.isBlocked(user);
    	if(temp == null)
    		return ResponseEntity.ok(false);

    	return ResponseEntity.ok(temp);
    }
    @GetMapping("/banned/{username}")
    public ResponseEntity<Boolean> isBanned(@PathVariable String username)
    {
    	if(username == null || username.isBlank())
    		return ResponseEntity.ok(false);
    	Users user = userService.findByUsername(username);
    	if (user == null ) 
    		return ResponseEntity.ok(false);
    	
    	Boolean temp = userService.isBanned(user);
    	if(temp == null)
    		return ResponseEntity.ok(false);

    	return ResponseEntity.ok(temp);
    }
    //Update
    @PutMapping("/{id}")
    public ResponseEntity<Users> update(@PathVariable Integer id, @RequestBody Users user)
    {
        Users updatedUser = userService.update(id, user);

        if (updatedUser == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(updatedUser);
    }
    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable Integer id)
    {
        Boolean deleted = userService.delete(id);

        if (deleted == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(deleted);
    }
}
