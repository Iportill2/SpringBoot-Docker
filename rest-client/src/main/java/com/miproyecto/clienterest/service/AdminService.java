package com.miproyecto.clienterest.service;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.AdminUserDTO;
import com.miproyecto.clienterest.dto.UserQuestionReadDTO;

@Service
public class AdminService {
	
    private final RestClient restClient;

    public AdminService(RestClient restClient) {
        this.restClient = restClient;
    }

	public List<AdminUserDTO> findPendingUsers() {
	    ResponseEntity<List<AdminUserDTO>> response = restClient.get()
	            .uri("/api/user")
	            .retrieve()
	            .toEntity(new ParameterizedTypeReference<List<AdminUserDTO>>() {});

	    List<AdminUserDTO> allUsers = response.getBody();

	    return allUsers.stream()
	            .filter(u -> u.getRole() == null || u.getRole().getId() == 3)
	            .toList();
	}
	
	public Boolean approve(Integer id) {
	    Boolean result = restClient.patch()
	            .uri("/api/user/{id}", id)
	            .body(Map.of("role", Map.of("id", 2)))
	            .retrieve()
	            .body(Boolean.class);

	    return Boolean.TRUE.equals(result);
	}

	public Boolean block(Integer id) {
	    Boolean result = restClient.patch()
	            .uri("/api/user/{id}", id)
	            .body(Map.of("blocked", true))
	            .retrieve()
	            .body(Boolean.class);

	    return Boolean.TRUE.equals(result);
	}
    
    public Boolean delete(Integer id) {

        List<UserQuestionReadDTO> userQuestions = restClient.get()
                .uri("/api/userquestion/user/{id}", id)
                .retrieve()
                .body(new ParameterizedTypeReference<List<UserQuestionReadDTO>>() {});

        if (userQuestions != null) {
            for (UserQuestionReadDTO uq : userQuestions) {
                Integer questionRecordId = (Integer) uq.getId();
                restClient.delete()
                        .uri("/api/userquestion/{id}", questionRecordId)
                        .retrieve()
                        .body(Boolean.class);
            }
        }

        Boolean deleted = restClient.delete()
                .uri("/api/user/{id}", id)
                .retrieve()
                .body(Boolean.class);

        return Boolean.TRUE.equals(deleted);
    }
}
