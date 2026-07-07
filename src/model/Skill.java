package model;

public class Skill {
    private int id;
    private int studentId;
    private String skillName;
    private String proficiency;

    public Skill() {}

    public Skill(int id, int studentId, String skillName, String proficiency) {
        this.id = id;
        this.studentId = studentId;
        this.skillName = skillName;
        this.proficiency = proficiency;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getProficiency() { return proficiency; }
    public void setProficiency(String proficiency) { this.proficiency = proficiency; }

    @Override
    public String toString() { return skillName + " (" + proficiency + ")"; }
}