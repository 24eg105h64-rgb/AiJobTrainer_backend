package com.aijobtrainer.dto;

import com.aijobtrainer.model.User;

import java.time.Instant;
import java.util.List;

public class UserResponse {
    private String id;
    private String _id; // for convenience with Mongo frontend expectations
    private String name;
    private String email;
    private String college;
    private String degree;
    private String branch;
    private Integer graduationYear;
    private String role;
    private List<String> skills;
    private String profileImage;
    private Instant createdAt;

    public UserResponse() {
    }

    public static UserResponse fromUser(User user) {
        if (user == null) return null;
        UserResponse res = new UserResponse();
        res.id = user.getId();
        res._id = user.getId();
        res.name = user.getName();
        res.email = user.getEmail();
        res.college = user.getCollege();
        res.degree = user.getDegree();
        res.branch = user.getBranch();
        res.graduationYear = user.getGraduationYear();
        res.role = user.getRole();
        res.skills = user.getSkills();
        res.profileImage = user.getProfileImage();
        res.createdAt = user.getCreatedAt();
        return res;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
