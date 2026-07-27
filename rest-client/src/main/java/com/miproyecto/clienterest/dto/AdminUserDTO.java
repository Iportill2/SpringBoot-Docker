package com.miproyecto.clienterest.dto;

public class AdminUserDTO {

    private Integer id;
    private String username;
    private String pass;
    private String salt;
    private String email;
    private String code;
    private Integer fails;
    private Boolean blocked;
    private Boolean banned;
    private RoleDTO role;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return this.pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getFails() {
        return this.fails;
    }

    public void setFails(Integer fails) {
        this.fails = fails;
    }

    public Boolean isBlocked() {
        return this.blocked;
    }

    public Boolean getBlocked() {
        return this.blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Boolean isBanned() {
        return this.banned;
    }

    public Boolean getBanned() {
        return this.banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public RoleDTO getRole() {
        return this.role;
    }

    public void setRole(RoleDTO role) {
        this.role = role;
    }
}
