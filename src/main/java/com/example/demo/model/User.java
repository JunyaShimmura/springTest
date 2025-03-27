package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false)
    private String workPlace;
    @Column(nullable = false)
    private double companyLat;
    @Column(nullable = false)
    private double companyLon;


    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public double getCompanyLat() {
        return companyLat;
    }

    public void setCompanyLat(double companyLat) {
        this.companyLat = companyLat;
    }

    public double getCompanyLon() {
        return companyLon;
    }

    public void setCompanyLon(double companyLon) {
        this.companyLon = companyLon;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
