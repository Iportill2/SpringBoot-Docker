package com.miproyecto.apirest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    @Query("SELECT u FROM Users u WHERE u.user = :username")
    Optional<Users> findByUser(String username);
}
