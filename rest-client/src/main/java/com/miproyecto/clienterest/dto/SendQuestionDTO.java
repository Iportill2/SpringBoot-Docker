package com.miproyecto.clienterest.dto;

public record SendQuestionDTO(
        Integer userId,
        Integer questionId,
        String answer
) {}
