package model;

import java.sql.Date;

public class Certification {
    private int id;
    private int studentId;
    private String courseName;
    private String platform;
    private Date completionDate;
    private String certificateLink;
    private String relatedSkill;

    public Certification() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public Date getCompletionDate() { return completionDate; }
    public void setCompletionDate(Date completionDate) { this.completionDate = completionDate; }
    public String getCertificateLink() { return certificateLink; }
    public void setCertificateLink(String certificateLink) { this.certificateLink = certificateLink; }
    public String getRelatedSkill() { return relatedSkill; }
    public void setRelatedSkill(String relatedSkill) { this.relatedSkill = relatedSkill; }
}