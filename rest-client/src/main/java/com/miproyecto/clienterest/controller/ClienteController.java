package com.miproyecto.clienterest.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.miproyecto.clienterest.dto.ClienteDTO;
import com.miproyecto.clienterest.service.ClienteService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/menu")
public class ClienteController {

    private final ClienteService clienteServ;

    public ClienteController(ClienteService clienteServ) {
        this.clienteServ = clienteServ;
    }

    @GetMapping("/clientes")
    public String getClients(Model model, HttpSession session) {

        boolean isAdmin = "ADMIN".equalsIgnoreCase((String) session.getAttribute("role"));

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("clientes", clienteServ.findAllClientes());
        model.addAttribute("clienteForm", new ClienteDTO());

        return "app/clientes";
    }

    @PostMapping("/clientes")
    public String createClient(@ModelAttribute("clienteForm") ClienteDTO clienteDTO) {

        clienteServ.create(clienteDTO);

        return "redirect:/menu/clientes";
    }

    @PostMapping("/clientes/editar/{id}")
    public String editClient(@PathVariable Integer id,
            @RequestParam String nombre,
            @RequestParam(required = false) String personaContacto,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String fechaAlta,
            RedirectAttributes redirectAttributes) {

        try {
            ClienteDTO dto = clienteServ.findById(id);
            if (dto == null) {
                redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
                return "redirect:/menu/clientes";
            }

            dto.setNombre(nombre);
            dto.setPersonaContacto(personaContacto != null && !personaContacto.isBlank() ? personaContacto : null);
            dto.setTelefono(telefono != null && !telefono.isBlank() ? telefono : null);
            dto.setDireccion(direccion != null && !direccion.isBlank() ? direccion : null);
            dto.setFechaAlta(fechaAlta != null && !fechaAlta.isBlank() ? LocalDate.parse(fechaAlta) : null);

            clienteServ.update(id, dto);
            redirectAttributes.addFlashAttribute("message", "Cliente actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar el cliente: " + e.getMessage());
        }

        return "redirect:/menu/clientes";
    }

}