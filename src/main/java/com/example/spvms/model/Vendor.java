package com.example.spvms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;


@JsonPropertyOrder({
    "id",
    "name",
    "userName",
    "email",
    "phone",
    "address",
    "gstNumber",
    "rating",
    "location",
    "category",
    "compliance",
    "createdAt",
    "updatedAt"
})
@Entity
@Schema(description = "Vendor details")
@Table(name = "vendors")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "userName", length = 150)
    private String userName;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "phone", length = 10)
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "gst_number", length = 50)
    private String gstNumber;

    @Column(name = "rating")
    private Double rating;     

    @Column(name = "location", length = 100)
    private String location;   

    @Column(name = "category", length = 100)
    private String category;   

    @Column(name = "compliance")
    private Boolean compliance;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ------ Getters & Setters -------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Boolean getCompliance() { return compliance; }
    public void setCompliance(Boolean compliance) { this.compliance = compliance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}