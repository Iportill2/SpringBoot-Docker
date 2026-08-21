package com.miproyecto.clienterest.controller;

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
import org.springframework.web.bind.annotation.RequestBody;

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

    @PostMapping("/clientes/eliminar/{id}")
    public String deleteClient(@PathVariable Integer id) {

        clienteServ.deleteCliente(id);
        
        return "redirect:/menu/clientes";
    }

}
