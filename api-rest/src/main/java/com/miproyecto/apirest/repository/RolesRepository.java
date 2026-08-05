package com.miproyecto.apirest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.miproyecto.apirest.model.Roles;


@Repository
public interface RolesRepository extends JpaRepository<Roles, Integer>{
//	findAll()
//	findById()
//	save()
//	delete()
//	count()
//	existsById()
	Optional<Roles> findByName(String name);
}
