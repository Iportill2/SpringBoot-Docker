package com.miproyecto.apirest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.miproyecto.apirest.model.Cliente;
import com.miproyecto.apirest.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepo;

    public ClienteService(ClienteRepository clienteRepo) {
        this.clienteRepo = clienteRepo;
    }

    public List<Cliente> findAll() {
        return clienteRepo.findAll();
    }

    public Cliente findById(Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        Optional<Cliente> temp = clienteRepo.findById(id);
        return temp.orElse(null);
    }

    public Cliente findByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        Optional<Cliente> temp = clienteRepo.findByNombre(nombre);
        return temp.orElse(null);
    }

    public Cliente create(Cliente cliente) {
        if (cliente == null || cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            return null;
        }
        if (clienteRepo.findByNombre(cliente.getNombre()).isPresent()) {
            return null;
        }
        return clienteRepo.save(cliente);
    }

    public Cliente update(Integer id, Cliente cliente) {
        if (id == null || id < 1 || cliente == null) {
            return null;
        }
        Cliente existing = clienteRepo.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            return null;
        }
        Optional<Cliente> duplicated = clienteRepo.findByNombre(cliente.getNombre());
        if (duplicated.isPresent() && !duplicated.get().getId().equals(id)) {
            return null;
        }
        existing.setNombre(cliente.getNombre());
        existing.setPersonaContacto(cliente.getPersonaContacto());
        existing.setTelefono(cliente.getTelefono());
        existing.setDireccion(cliente.getDireccion());
        existing.setFechaAlta(cliente.getFechaAlta());
        return clienteRepo.save(existing);
    }

    public Boolean delete(Integer id) {
        if (id == null || id < 1) {
            return null;
        }
        Optional<Cliente> temp = clienteRepo.findById(id);
        if (temp.isEmpty()) {
            return false;
        }
        clienteRepo.delete(temp.get());
        return true;
    }
}
