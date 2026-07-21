package com.miproyecto.apirest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miproyecto.apirest.model.Users;





@Repository
public interface UsersRepository extends JpaRepository<Users, Integer>{
//	findAll()
//	findById()
//	save()
//	delete()
//	count()
//	existsById()
	Optional<Users> findByUser(String username);
}
