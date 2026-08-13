package com.miproyecto.clienterest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.miproyecto.clienterest.dto.AdminUserDTO;
import com.miproyecto.clienterest.dto.ClienteDTO;
import com.miproyecto.clienterest.dto.TareaDTO;
import com.miproyecto.clienterest.service.CrmService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/menu")
public class CrmController {

    private final CrmService crmService;

    public CrmController(CrmService crmService) {
        this.crmService = crmService;
    }

    @GetMapping("/crm")
    public String crmGet(Model model, HttpSession session) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase((String) session.getAttribute("role"));

        List<TareaDTO> tareas;
        if (isAdmin) {
            List<TareaDTO> todas = crmService.findAllTareas();
            tareas = todas != null ? new ArrayList<>(todas) : new ArrayList<>();
        } else {
            Integer userId = (Integer) session.getAttribute("userId");
            tareas = new ArrayList<>();
            List<TareaDTO> propias = crmService.findTareasByResponsable(userId);
            List<TareaDTO> pool = crmService.findTareasSinAsignar();
            if (propias != null) {
                tareas.addAll(propias);
            }
            if (pool != null) {
                tareas.addAll(pool);
            }
        }

        model.addAttribute("tareas", tareas);
        model.addAttribute("clientes", crmService.findAllClientes());
        model.addAttribute("usuarios", crmService.findAllUsuarios());
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("tareaForm", emptyForm());

        return "app/crm";
    }

    @PostMapping("/crm/crear")
    public String crear(@ModelAttribute("tareaForm") TareaDTO tarea,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {

        String role = (String) session.getAttribute("role");
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        if (!isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Solo los administradores pueden crear tareas");
            return "redirect:/menu/crm";
        }

        try {
            crmService.crearTarea(tarea);
            redirectAttributes.addFlashAttribute("message", "Tarea creada correctamente");
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo crear la tarea");
        }
        return "redirect:/menu/crm";
    }

    @PostMapping("/crm/editar/{id}")
    public String editar(@PathVariable Integer id,
                         @ModelAttribute("tareaForm") TareaDTO tarea,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {

        String role = (String) session.getAttribute("role");
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        if (!isAdmin) {
            Integer userId = (Integer) session.getAttribute("userId");
            if (tarea.getResponsable() == null) {
                tarea.setResponsable(new AdminUserDTO());
            }
            if (tarea.getResponsable().getId() == null
                    || !tarea.getResponsable().getId().equals(userId)) {
                tarea.getResponsable().setId(userId);
            }
        }

        try {
            crmService.actualizarTarea(id, tarea);
            redirectAttributes.addFlashAttribute("message", "Tarea actualizada correctamente");
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar la tarea");
        }
        return "redirect:/menu/crm";
    }

    @PostMapping("/crm/eliminar/{id}")
    public String eliminar(@PathVariable Integer id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {

        boolean isAdmin = "ADMIN".equalsIgnoreCase((String) session.getAttribute("role"));
        if (!isAdmin) {
            redirectAttributes.addFlashAttribute("error", "Solo los administradores pueden eliminar tareas");
            return "redirect:/menu/crm";
        }

        try {
            crmService.eliminarTarea(id);
            redirectAttributes.addFlashAttribute("message", "Tarea eliminada correctamente");
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo eliminar la tarea");
        }
        return "redirect:/menu/crm";
    }

    @PostMapping("/crm/asignar/{id}")
    public String asignar(@PathVariable Integer id,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        try {
            crmService.asignarTarea(id, userId);
            redirectAttributes.addFlashAttribute("message", "Tarea asignada correctamente");
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo asignar la tarea");
        }
        return "redirect:/menu/crm";
    }

    @PostMapping("/crm/horas/{id}")
    public String horas(@PathVariable Integer id,
                        @RequestParam(required = false) Double horasEmpleadas,
                        RedirectAttributes redirectAttributes) {

        try {
            crmService.actualizarHoras(id, horasEmpleadas);
            redirectAttributes.addFlashAttribute("message", "Horas actualizadas correctamente");
        } catch (RestClientException e) {
            redirectAttributes.addFlashAttribute("error", "No se pudieron actualizar las horas");
        }
        return "redirect:/menu/crm";
    }

    private TareaDTO emptyForm() {
        TareaDTO tarea = new TareaDTO();
        tarea.setCliente(new ClienteDTO());
        tarea.setResponsable(new AdminUserDTO());
        tarea.setEstado("PENDIENTE");
        tarea.setPrioridad("MEDIA");
        return tarea;
    }
}
