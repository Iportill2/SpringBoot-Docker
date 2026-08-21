package com.miproyecto.apirest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "user_questions")
public class UserQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(
        foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    private Users user;


    @ManyToOne
    @JoinColumn(name = "question_id")
    private Questions question;


    @Column(nullable = false)
    private String answer;


    public UserQuestion() {
    }


    public UserQuestion(Integer id, Users user, Questions question, String answer) {
        this.id = id;
        this.user = user;
        this.question = question;
        this.answer = answer;
    }


    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }


    public Users getUser() {
        return user;
    }


    public void setUser(Users user) {
        this.user = user;
    }


    public Questions getQuestion() {
        return question;
    }


    public void setQuestion(Questions question) {
        this.question = question;
    }


    public String getAnswer() {
        return answer;
    }


    public void setAnswer(String answer) {
        this.answer = answer;
    }
}