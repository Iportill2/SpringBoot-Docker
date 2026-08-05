package com.miproyecto.clienterest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@Valid
public class QuestionsDTO {


    @NotNull(message = "El usuario es obligatorio")
    private Integer userId;


    @NotNull(message = "La pregunta es obligatoria")
    private Integer questionId;


    @NotBlank(message = "La respuesta es obligatoria")
    private String answer;



    public QuestionsDTO() {
    }


    public QuestionsDTO(Integer userId, Integer questionId, String answer) {

        this.userId = userId;
        this.questionId = questionId;
        this.answer = answer;
    }



    public Integer getUserId() {
        return userId;
    }


    public void setUserId(Integer userId) {
        this.userId = userId;
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
