package com.miproyecto.apirest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.miproyecto.apirest.model.Cliente;
import com.miproyecto.apirest.model.Tarea;
import com.miproyecto.apirest.model.Users;
import com.miproyecto.apirest.repository.ClienteRepository;
import com.miproyecto.apirest.repository.TareaRepository;
import com.miproyecto.apirest.repository.UsersRepository;

@Service
public class TareaService {

    private final TareaRepository tareaRepo;
    private final ClienteRepository clienteRepo;
    private final UsersRepository usersRepo;

    public TareaService(TareaRepository tareaRepo, ClienteRepository clienteRepo, UsersRepository usersRepo) {
        this.tareaRepo = tareaRepo;
        this.clienteRepo = clienteRepo;
        this.usersRepo = usersRepo;
    }

    public List<Tarea> findAll() {
        return tareaRepo.findAll();
    }

    public List<Tarea> findByResponsableId(Integer responsableId) {
        if (responsableId == null || responsableId < 1) {
            return List.of();
        }
        return tareaRepo.findByResponsableId(responsableId);
    }

    public List<Tarea> findByResponsableIsNull() {
        return tareaRepo.findByResponsableIsNull();
    }

    public List<Tarea> findByClienteId(Integer clienteId) {
        if (clienteId == null || clienteId < 1) {
            return List.of();
        }
        return tareaRepo.findByClienteId(clienteId);
    }

    public List<Tarea> findByEstado(Tarea.Estado estado) {
        if (estado == null) {
            return List.of();
        }
        return tareaRepo.findByEstado(estado);
    }

    public Tarea findById(Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        Optional<Tarea> temp = tareaRepo.findById(id);
        return temp.orElse(null);
    }

    public Tarea create(Tarea tarea) {
        if (tarea == null || tarea.getTitulo() == null || tarea.getTitulo().isBlank()) {
            return null;
        }
        if (!validateReferences(tarea)) {
            return null;
        }
        if (tarea.getEstado() == null) {
            tarea.setEstado(Tarea.Estado.PENDIENTE);
        }
        if (tarea.getPrioridad() == null) {
            tarea.setPrioridad(Tarea.Prioridad.MEDIA);
        }
        return tareaRepo.save(tarea);
    }

    public Tarea asignarResponsable(Integer id, Integer userId) {
        if (id == null || id < 1 || userId == null || userId < 1) {
            return null;
        }
        Tarea existing = tareaRepo.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (existing.getResponsable() != null) {
            return null;
        }
        Users user = usersRepo.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        existing.setResponsable(user);
        return tareaRepo.save(existing);
    }

    public Tarea actualizarHoras(Integer id, Double horas) {
        if (id == null || id < 1) {
            return null;
        }
        Tarea existing = tareaRepo.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        existing.setHorasEmpleadas(horas);
        return tareaRepo.save(existing);
    }

    public Tarea update(Integer id, Tarea tarea) {
        if (id == null || id < 1 || tarea == null) {
            return null;
        }
        Tarea existing = tareaRepo.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (tarea.getTitulo() == null || tarea.getTitulo().isBlank()) {
            return null;
        }
        if (!validateReferences(tarea)) {
            return null;
        }
        existing.setTitulo(tarea.getTitulo());
        existing.setDescripcion(tarea.getDescripcion());
        existing.setCliente(tarea.getCliente());
        existing.setResponsable(tarea.getResponsable());
        existing.setEstado(tarea.getEstado());
        existing.setPrioridad(tarea.getPrioridad());
        existing.setFechaLimite(tarea.getFechaLimite());
        existing.setHorasEmpleadas(tarea.getHorasEmpleadas());
        return tareaRepo.save(existing);
    }

    public Boolean delete(Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        Optional<Tarea> temp = tareaRepo.findById(id);
        if (temp.isEmpty()) {
            return false;
        }
        tareaRepo.delete(temp.get());
        return true;
    }

    private boolean validateReferences(Tarea tarea) {
        if (tarea.getCliente() != null && tarea.getCliente().getId() != null) {
            Optional<Cliente> cliente = clienteRepo.findById(tarea.getCliente().getId());
            if (cliente.isEmpty()) {
                return false;
            }
            tarea.setCliente(cliente.get());
        } else {
            tarea.setCliente(null);
        }
        if (tarea.getResponsable() != null && tarea.getResponsable().getId() != null) {
            Optional<Users> responsable = usersRepo.findById(tarea.getResponsable().getId());
            if (responsable.isEmpty()) {
                return false;
            }
            tarea.setResponsable(responsable.get());
        } else {
            tarea.setResponsable(null);
        }
        return true;
    }
}
