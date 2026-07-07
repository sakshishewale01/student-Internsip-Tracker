package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Internship {
    private int id;
    private String companyName;
    private String role;
    private String location;
    private String description;
    private String requiredSkills;
    private String stipend;
    private String duration;
    private Date deadline;
    private String applyLink;
    private int postedBy;
    private String status;
    private Timestamp createdAt;

    public Internship() {}

    public Internship(int id, String companyName, String role, String location,
                      String requiredSkills, String stipend, String duration, Date deadline, String status) {
        this.id = id;
        this.companyName = companyName;
        this.role = role;
        this.location = location;
        this.requiredSkills = requiredSkills;
        this.stipend = stipend;
        this.duration = duration;
        this.deadline = deadline;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }
    public String getStipend() { return stipend; }
    public void setStipend(String stipend) { this.stipend = stipend; }
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }
    public String getApplyLink() { return applyLink; }
    public void setApplyLink(String applyLink) { this.applyLink = applyLink; }
    public int getPostedBy() { return postedBy; }
    public void setPostedBy(int postedBy) { this.postedBy = postedBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() { return companyName + " - " + role; }
}