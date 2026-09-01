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
import com.miproyecto.clienterest.service.ClienteService;
import com.miproyecto.clienterest.service.CrmService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/menu")
public class CrmController {

    private final CrmService crmService;
    private final ClienteService clienteServ;

    public CrmController(CrmService crmService, ClienteService clienteServ) {
        this.crmService = crmService;
        this.clienteServ = clienteServ;
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
        model.addAttribute("usuarios", isAdmin ? crmService.findAllUsuarios() : new ArrayList<AdminUserDTO>());
        model.addAttribute("clientes", clienteServ.findAllClientes());
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
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) Integer responsableId,
            @RequestParam(required = false) String fechaLimite,
            @RequestParam(required = false) Double horasEmpleadas,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String role = (String) session.getAttribute("role");
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);
        Integer userId = (Integer) session.getAttribute("userId");

        try {
            // 1. Traer la tarea actual
            TareaDTO tarea = crmService.findTareaById(id);
            if (tarea == null) {
                redirectAttributes.addFlashAttribute("error", "Tarea no encontrada");
                return "redirect:/menu/crm";
            }

            boolean esResponsable = tarea.getResponsable() != null
                    && tarea.getResponsable().getId().equals(userId);

            if (!isAdmin && !esResponsable) {
                redirectAttributes.addFlashAttribute("error", "No tienes permisos");
                return "redirect:/menu/crm";
            }

            // 2. Si NO es admin, solo puede tocar el estado (y horas, que también editan los empleados)
            if (!isAdmin) {
                if (estado != null)
                    tarea.setEstado(estado);
                if (horasEmpleadas != null)
                    tarea.setHorasEmpleadas(horasEmpleadas);

                crmService.actualizarTarea(id, tarea);
                redirectAttributes.addFlashAttribute("message", "Tarea actualizada");
                return "redirect:/menu/crm";
            }

            // 3. Admin: puede tocar todos los campos (lógica original)
            if (titulo != null)
                tarea.setTitulo(titulo);
            if (estado != null)
                tarea.setEstado(estado);
            if (prioridad != null)
                tarea.setPrioridad(prioridad);
            if (fechaLimite != null && !fechaLimite.isBlank())
                tarea.setFechaLimite(java.time.LocalDate.parse(fechaLimite));
            else if (fechaLimite != null && fechaLimite.isBlank())
                tarea.setFechaLimite(null);
            if (horasEmpleadas != null)
                tarea.setHorasEmpleadas(horasEmpleadas);

            if (clienteId != null) {
                ClienteDTO c = new ClienteDTO();
                c.setId(clienteId == 0 ? null : clienteId);
                tarea.setCliente(clienteId == 0 ? null : c);
            }
            if (responsableId != null) {
                AdminUserDTO u = new AdminUserDTO();
                u.setId(responsableId == 0 ? null : responsableId);
                tarea.setResponsable(responsableId == 0 ? null : u);
            }

            crmService.actualizarTarea(id, tarea);
            redirectAttributes.addFlashAttribute("message", "Tarea actualizada");

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
