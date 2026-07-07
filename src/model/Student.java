package model;

import java.sql.Timestamp;

public class Student {
    private int id;
    private String name;
    private String email;
    private String password;
    private String branch;
    private int year;
    private String phone;
    private String resumeLink;
    private String role;
    private String profilePic;
    private Timestamp createdAt;

    public Student() {}

    public Student(int id, String name, String email, String branch, int year, String phone, String resumeLink, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.branch = branch;
        this.year = year;
        this.phone = phone;
        this.resumeLink = resumeLink;
        this.role = role;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getResumeLink() { return resumeLink; }
    public void setResumeLink(String resumeLink) { this.resumeLink = resumeLink; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() { return name + " (" + email + ")"; }
}