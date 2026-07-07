package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Application;

public class ApplicationDAO {

    public boolean apply(int studentId, int internshipId) {
        // Check if already applied
        String check = "SELECT id FROM applications WHERE student_id=? AND internship_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(check)) {
            ps.setInt(1, studentId); ps.setInt(2, internshipId);
            if (ps.executeQuery().next()) return false; // Already applied
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String sql = "INSERT INTO applications (student_id, internship_id, status) VALUES (?,?,'APPLIED')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, internshipId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateStatus(int appId, String status) {
        String sql = "UPDATE applications SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, appId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Application> getByStudent(int studentId) {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, i.company_name, i.role, i.location, i.deadline FROM applications a " +
                "JOIN internships i ON a.internship_id = i.id WHERE a.student_id=? ORDER BY a.applied_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Application app = new Application();
                app.setId(rs.getInt("id"));
                app.setStudentId(rs.getInt("student_id"));
                app.setInternshipId(rs.getInt("internship_id"));
                app.setStatus(rs.getString("status"));
                app.setAppliedDate(rs.getTimestamp("applied_date"));
                app.setNotes(rs.getString("notes"));
                app.setCompanyName(rs.getString("company_name"));
                app.setRole(rs.getString("role"));
                app.setLocation(rs.getString("location"));
                app.setDeadline(rs.getString("deadline"));
                list.add(app);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Application> getAll() {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT a.*, i.company_name, i.role, s.name as student_name FROM applications a " +
                "JOIN internships i ON a.internship_id = i.id " +
                "JOIN students s ON a.student_id = s.id ORDER BY a.applied_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Application app = new Application();
                app.setId(rs.getInt("id"));
                app.setStudentId(rs.getInt("student_id"));
                app.setInternshipId(rs.getInt("internship_id"));
                app.setStatus(rs.getString("status"));
                app.setAppliedDate(rs.getTimestamp("applied_date"));
                app.setCompanyName(rs.getString("company_name"));
                app.setRole(rs.getString("role"));
                app.setNotes(rs.getString("student_name")); // reusing notes field for student name in admin view
                list.add(app);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Map<String, Integer> getStatusCounts(int studentId) {
        Map<String, Integer> map = new HashMap<>();
        map.put("APPLIED", 0); map.put("SHORTLISTED", 0);
        map.put("SELECTED", 0); map.put("REJECTED", 0);
        String sql = "SELECT status, COUNT(*) as cnt FROM applications WHERE student_id=? GROUP BY status";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getString("status"), rs.getInt("cnt"));
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public int getTotalCount(int studentId) {
        String sql = "SELECT COUNT(*) FROM applications WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}