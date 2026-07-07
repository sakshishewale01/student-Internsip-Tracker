package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Certification;
import model.Skill;

public class SkillDAO {

    // ---- SKILLS ----
    public List<Skill> getSkillsByStudent(int studentId) {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT * FROM skills WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Skill sk = new Skill(rs.getInt("id"), rs.getInt("student_id"),
                        rs.getString("skill_name"), rs.getString("proficiency"));
                list.add(sk);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addSkill(Skill s) {
        String sql = "INSERT INTO skills (student_id, skill_name, proficiency) VALUES (?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getStudentId());
            ps.setString(2, s.getSkillName());
            ps.setString(3, s.getProficiency());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteSkill(int skillId) {
        String sql = "DELETE FROM skills WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, skillId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateSkill(Skill s) {
        String sql = "UPDATE skills SET skill_name=?, proficiency=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSkillName());
            ps.setString(2, s.getProficiency());
            ps.setInt(3, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ---- CERTIFICATIONS ----
    public List<Certification> getCertsByStudent(int studentId) {
        List<Certification> list = new ArrayList<>();
        String sql = "SELECT * FROM certifications WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Certification c = new Certification();
                c.setId(rs.getInt("id"));
                c.setStudentId(rs.getInt("student_id"));
                c.setCourseName(rs.getString("course_name"));
                c.setPlatform(rs.getString("platform"));
                c.setCompletionDate(rs.getDate("completion_date"));
                c.setCertificateLink(rs.getString("certificate_link"));
                c.setRelatedSkill(rs.getString("related_skill"));
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addCert(Certification c) {
        String sql = "INSERT INTO certifications (student_id, course_name, platform, completion_date, certificate_link, related_skill) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, c.getStudentId());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getPlatform());
            ps.setDate(4, c.getCompletionDate());
            ps.setString(5, c.getCertificateLink());
            ps.setString(6, c.getRelatedSkill());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean deleteCert(int certId) {
        String sql = "DELETE FROM certifications WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, certId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}