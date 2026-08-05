package com.miproyecto.clienterest.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class UserQuestionsDTO {

    @NotNull(message = "El usuario es obligatorio")
    private Integer userId;

    @Valid
    private List<QuestionAnswerDTO> answers = new ArrayList<>();

    public UserQuestionsDTO() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<QuestionAnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswerDTO> answers) {
        this.answers = answers;
    }
}
