package com.miproyecto.apirest.dto;

public record CheckAnswerRequest(
        Integer userId,
        Integer questionId,
        String answer
) {}