package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Student;

public class StudentDAO {

    public Student login(String email, String password) {
        String sql = "SELECT * FROM students WHERE email=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapStudent(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean register(Student s) {
        String sql = "INSERT INTO students (name, email, password, branch, year, phone, resume_link, role) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getPassword());
            ps.setString(4, s.getBranch());
            ps.setInt(5, s.getYear());
            ps.setString(6, s.getPhone());
            ps.setString(7, s.getResumeLink());
            ps.setString(8, "STUDENT");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateProfile(Student s) {
        String sql = "UPDATE students SET name=?, branch=?, year=?, phone=?, resume_link=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getBranch());
            ps.setInt(3, s.getYear());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getResumeLink());
            ps.setInt(6, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE role='STUDENT'";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapStudent(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public Student getById(int id) {
        String sql = "SELECT * FROM students WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapStudent(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setPassword(rs.getString("password"));
        s.setBranch(rs.getString("branch"));
        s.setYear(rs.getInt("year"));
        s.setPhone(rs.getString("phone"));
        s.setResumeLink(rs.getString("resume_link"));
        s.setRole(rs.getString("role"));
        return s;
    }
}