package com.miproyecto.apirest.dto;

public record UserQuestionDTO(
        Integer userId,
        Integer questionId,
        String answer
) {}
