package com.miproyecto.clienterest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuestionAnswerDTO {

    @NotNull(message = "La pregunta es obligatoria")
    private Integer questionId;

    @NotBlank(message = "La respuesta es obligatoria")
    private String answer;

    public QuestionAnswerDTO() {
    }

    public QuestionAnswerDTO(Integer questionId, String answer) {
        this.questionId = questionId;
        this.answer = answer;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
