package com.example.android_assigment;

import java.util.List;

public class User {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String username;
    private List<String> friends;
    public User() {}
    public User(String name, String lastName, String email, String password, String username) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
