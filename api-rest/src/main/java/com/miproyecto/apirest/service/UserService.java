package com.miproyecto.apirest.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miproyecto.apirest.dto.UserCreateDTO;
import com.miproyecto.apirest.model.Roles;
import com.miproyecto.apirest.model.Tarea;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.RolesRepository;
import com.miproyecto.apirest.repository.TareaRepository;
import com.miproyecto.apirest.repository.TimeEntryRepository;
import com.miproyecto.apirest.repository.UserQuestionRepository;
import com.miproyecto.apirest.repository.UsersRepository;




@Service
public class UserService {

    private final UsersRepository userRepo;
    private final RolesRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final TareaRepository tareaRepo;
    private final TimeEntryRepository timeRepo;
    private final UserQuestionRepository UQRepo;
    public UserService(UsersRepository userRepository, RolesRepository roleRepository,
            PasswordEncoder passwordEncoder, UserQuestionRepository UQRepo, 
            TareaRepository tareaRepo, TimeEntryRepository timeRepo) 
    {
    	this.userRepo = userRepository;
    	this.roleRepo = roleRepository;
    	this.passwordEncoder = passwordEncoder;
    	this.tareaRepo = tareaRepo;
    	this.UQRepo = UQRepo;
    	this.timeRepo = timeRepo;
    }
    //Create
    public Users create(UserCreateDTO userDTO) {
    try {

        Users user = new Users();

        user.setUsername(userDTO.username());
        user.setPass(passwordEncoder.encode(userDTO.pass()));
        user.setEmail(userDTO.email());

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

    public void persist(Users user) {
        if (user != null && user.getId() != null) {
            userRepo.save(user);
        }
    }
    public Boolean isBlocked(Users user)
    {
    	Optional<Users> temp = userRepo.findById(user.getId());
    	if(temp.isEmpty())
    		return null;

    	return false;
    }
    public Boolean isBanned(Users user)
    {
    	Optional<Users> temp = userRepo.findById(user.getId());
    	if(temp.isEmpty())
    		return null;
    	

    	return false;
    }
    //Update
    public Users update(Integer id, Users user) {

        Optional<Users> existing = userRepo.findById(id);

        if (existing.isEmpty()) {
            return null;
        }

        Users current = existing.get();

        if (user.getUsername() != null) {
            current.setUsername(user.getUsername());
        }

        if (user.getPass() != null) {
            current.setPass(passwordEncoder.encode(user.getPass()));
        }

        if (user.getSalt() != null) {
            current.setSalt(user.getSalt());
        }

        if (user.getEmail() != null) {
            current.setEmail(user.getEmail());
        }

        if (user.getCode() != null) {
            current.setCode(user.getCode());
        }

        if (user.getRole() != null) {
            // Validar el rol solicitado: solo se permiten roles conocidos (1..4).
            Roles requestedRole = roleRepo.findById(user.getRole().getId()).orElse(null);
            if (requestedRole == null) {
                throw new IllegalArgumentException("Rol invalido: " + user.getRole().getId());
            }

            // Anti-auto-elevacion: un usuario no puede elevarse a ADMIN (id 2)
            // a traves de este endpoint de actualizacion generico.
            Users operator = com.miproyecto.apirest.security.SecurityUtils.currentUser();
            boolean targetingSelf = operator != null
                    && operator.getId() != null
                    && operator.getId().equals(current.getId());
            if (targetingSelf && requestedRole.getId() == 2) {
                throw new SecurityException("No puedes elevarte a rol ADMIN");
            }

            current.setRole(requestedRole);
        }

        return userRepo.save(current);
    }
    //Delete
    
    @Transactional
    public Boolean delete(Integer id)
    {
    	Optional<Users> temp = userRepo.findById(id);
    	
    	if (id == null || id < 1) return null;
    	if (temp.isEmpty()) return false; 

    	
    	List<Tarea> listaTareasUnidasAEseEmpleado = tareaRepo.findByResponsableId(id);
    	
    	for (Tarea t : listaTareasUnidasAEseEmpleado) {
    		t.setResponsable(null);
    		tareaRepo.save(t);
    	}
    	
    	timeRepo.deleteByUserId(id);
    	UQRepo.deleteByUserId(id);
    	userRepo.delete(temp.get());//hacemos esto para pasar de Optional<Users> a Users
    	return true;
    }
    
}