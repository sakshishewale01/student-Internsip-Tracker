package ui;

import java.awt.*;
import javax.swing.*;
import model.Student;

public class StudentPanel extends JPanel {
    private final Student student;

    public StudentPanel(Student student) {
        this.student = student;
        setBackground(UITheme.BG_CARD);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        buildUI();
    }

    private void buildUI() {
        setLayout(new GridLayout(0, 2, 12, 8));

        addField("Name", student.getName());
        addField("Email", student.getEmail());
        addField("Branch", student.getBranch() != null ? student.getBranch() : "N/A");
        addField("Year", student.getYear() > 0 ? "Year " + student.getYear() : "N/A");
        addField("Phone", student.getPhone() != null ? student.getPhone() : "N/A");
        addField("Role", student.getRole() != null ? student.getRole() : "STUDENT");
        addField("Resume", student.getResumeLink() != null && !student.getResumeLink().isEmpty() 
            ? "✅ Added" : "❌ Not added");
    }

    private void addField(String label, String value) {
        add(UITheme.createLabel(label + ":", UITheme.FONT_BOLD, UITheme.TEXT_SECOND));
        add(UITheme.createLabel(value, UITheme.FONT_BODY, UITheme.TEXT_PRIMARY));
    }
}