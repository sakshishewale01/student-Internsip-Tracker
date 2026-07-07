package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Internship;

public class InternshipDAO {

    public List<Internship> getAll() {
        List<Internship> list = new ArrayList<>();
        String sql = "SELECT * FROM internships WHERE status='ACTIVE' ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapInternship(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Internship> search(String keyword, String skill, String location) {
        List<Internship> list = new ArrayList<>();
        String sql = "SELECT * FROM internships WHERE status='ACTIVE' AND " +
                "(company_name LIKE ? OR role LIKE ? OR description LIKE ?) " +
                "AND required_skills LIKE ? AND location LIKE ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + (keyword == null ? "" : keyword) + "%";
            String sk = "%" + (skill == null ? "" : skill) + "%";
            String loc = "%" + (location == null ? "" : location) + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            ps.setString(4, sk); ps.setString(5, loc);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapInternship(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean add(Internship i) {
        String sql = "INSERT INTO internships (company_name, role, location, description, required_skills, stipend, duration, deadline, apply_link, posted_by) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, i.getCompanyName());
            ps.setString(2, i.getRole());
            ps.setString(3, i.getLocation());
            ps.setString(4, i.getDescription());
            ps.setString(5, i.getRequiredSkills());
            ps.setString(6, i.getStipend());
            ps.setString(7, i.getDuration());
            ps.setDate(8, i.getDeadline());
            ps.setString(9, i.getApplyLink());
            ps.setInt(10, i.getPostedBy());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(Internship i) {
        String sql = "UPDATE internships SET company_name=?, role=?, location=?, description=?, required_skills=?, stipend=?, duration=?, deadline=?, apply_link=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, i.getCompanyName()); ps.setString(2, i.getRole());
            ps.setString(3, i.getLocation()); ps.setString(4, i.getDescription());
            ps.setString(5, i.getRequiredSkills()); ps.setString(6, i.getStipend());
            ps.setString(7, i.getDuration()); ps.setDate(8, i.getDeadline());
            ps.setString(9, i.getApplyLink()); ps.setInt(10, i.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "UPDATE internships SET status='CLOSED' WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public Internship getById(int id) {
        String sql = "SELECT * FROM internships WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapInternship(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Internship mapInternship(ResultSet rs) throws SQLException {
        Internship i = new Internship();
        i.setId(rs.getInt("id"));
        i.setCompanyName(rs.getString("company_name"));
        i.setRole(rs.getString("role"));
        i.setLocation(rs.getString("location"));
        i.setDescription(rs.getString("description"));
        i.setRequiredSkills(rs.getString("required_skills"));
        i.setStipend(rs.getString("stipend"));
        i.setDuration(rs.getString("duration"));
        i.setDeadline(rs.getDate("deadline"));
        i.setApplyLink(rs.getString("apply_link"));
        i.setPostedBy(rs.getInt("posted_by"));
        i.setStatus(rs.getString("status"));
        return i;
    }
}