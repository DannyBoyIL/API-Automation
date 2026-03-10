package com.apiAutomation.model;

public class User {

    public int id;
    public String name;
    public String username;
    public String email;
    public String phone;
    public String website;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}
