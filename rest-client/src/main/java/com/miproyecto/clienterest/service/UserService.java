package com.miproyecto.clienterest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.miproyecto.clienterest.dto.QuestionDTO;
import com.miproyecto.clienterest.dto.SendQuestionDTO;
import com.miproyecto.clienterest.dto.UserQuestionPrintDTO;
import com.miproyecto.clienterest.dto.UsersDTO;

@Service
public class UserService {

    private final RestClient restClient;

    public UserService(
            @Qualifier("apiRestClient") RestClient restClient) {

        this.restClient = restClient;
    }

    public ResponseEntity<UsersDTO> create(UsersDTO user) {

        return restClient.post()
                .uri("/api/user")
                .body(user)
                .retrieve()
                .toEntity(UsersDTO.class);
    }

    // READ ALL
    public ResponseEntity<List<UsersDTO>> findAll() {

        return restClient.get()
                .uri("/api/user")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UsersDTO>>() {
                });
    }

    // READ BY ID
    public ResponseEntity<UsersDTO> findById(Integer id) {

        return restClient.get()
                .uri("/api/user/{id}", id)
                .retrieve()
                .toEntity(UsersDTO.class);
    }

    // READ BY EMAIL
    public ResponseEntity<UsersDTO> findByEmail(String email) {

        return restClient.get()
                .uri("/api/user/email/{email}", email)
                .retrieve()
                .toEntity(UsersDTO.class);
    }

    // READ BY USERNAME
    public ResponseEntity<UsersDTO> findByUsername(String username) {

        return restClient.get()
                .uri("/api/user/name/{username}", username)
                .retrieve()
                .toEntity(UsersDTO.class);
    }

    // CHECK USERNAME EXISTS
    public ResponseEntity<Boolean> existsByUsername(String username) {

        return restClient.get()
                .uri("/api/user/name/exist/{username}", username)
                .retrieve()
                .toEntity(Boolean.class);
    }

    // CHECK EMAIL EXISTS
    public ResponseEntity<Boolean> existsByEmail(String email) {

        return restClient.get()
                .uri("/api/user/email/exist/{email}", email)
                .retrieve()
                .toEntity(Boolean.class);
    }

    // CHECK BLOCKED
    public ResponseEntity<Boolean> isBlocked(String username) {

        return restClient.get()
                .uri("/api/user/blocked/{username}", username)
                .retrieve()
                .toEntity(Boolean.class);
    }

    // CHECK BANNED
    public ResponseEntity<Boolean> isBanned(String username) {

        return restClient.get()
                .uri("/api/user/banned/{username}", username)
                .retrieve()
                .toEntity(Boolean.class);
    }

    // UPDATE
    public ResponseEntity<UsersDTO> update(Integer id, UsersDTO user) {

        return restClient.put()
                .uri("/api/user/{id}", id)
                .body(user)
                .retrieve()
                .toEntity(UsersDTO.class);
    }

    // DELETE
    public ResponseEntity<Boolean> delete(Integer id) {

        return restClient.delete()
                .uri("/api/user/{id}", id)
                .retrieve()
                .toEntity(Boolean.class);
    }

    // SAVE QUESTION
    public ResponseEntity<?> saveQuestion(Integer userId, Integer questionId, String answer) {

        SendQuestionDTO dto = new SendQuestionDTO(userId, questionId, answer);

        return restClient.post()
                .uri("/api/userquestion/from-dto")
                .body(dto)
                .retrieve()
                .toEntity(Object.class);
    }

    public ResponseEntity<List<QuestionDTO>> findAllQuestions() {
        return restClient.get()
                .uri("/api/questions")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<QuestionDTO>>() {
                });
    }

    // READ QUESTIONS BY USER ID
    public ResponseEntity<List<UserQuestionPrintDTO>> findQuestionsByUserId(Integer userId, String jwt) {
        return restClient.get()
                .uri("/api/userquestion/user/{id}", userId)
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UserQuestionPrintDTO>>() {
                });
    }

}
