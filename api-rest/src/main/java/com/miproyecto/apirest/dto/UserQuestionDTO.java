package com.miproyecto.apirest.dto;

public record UserQuestionDTO(
                Integer id,
                Integer userId,
                Integer questionId,
                String questionText,
                String answer) {
}
