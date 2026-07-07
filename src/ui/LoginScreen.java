package ui;

import dao.StudentDAO;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import model.Student;

public class LoginScreen extends JFrame {
    private final StudentDAO studentDAO = new StudentDAO();
    private JPanel mainPanel, loginPanel, registerPanel;
    private JTextField loginEmail, regName, regEmail, regBranch, regPhone;
    private JPasswordField loginPass, regPass;
    private JComboBox<String> regYear;
    private boolean showingLogin = true;

    public LoginScreen() {
        setTitle("InternTrack Pro — Student Internship Management");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setUndecorated(false);
        UITheme.applyGlobalTheme();
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        mainPanel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, UITheme.BG_DARK, getWidth(), getHeight(), new Color(20, 10, 40));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative circles
                g2.setColor(new Color(59, 130, 246, 20));
                g2.fillOval(-100, -100, 400, 400);
                g2.setColor(new Color(139, 92, 246, 15));
                g2.fillOval(getWidth() - 200, getHeight() - 200, 400, 400);
                g2.setColor(new Color(6, 182, 212, 10));
                g2.fillOval(getWidth()/2 - 150, getHeight()/2 - 150, 300, 300);
            }
        };
        mainPanel.setOpaque(false);
        setContentPane(mainPanel);

        // Left branding panel
        JPanel brandPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(37, 99, 235), getWidth(), getHeight(), new Color(109, 40, 217));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Pattern dots
                g2.setColor(new Color(255,255,255,20));
                for (int i=0; i<getWidth(); i+=30) for (int j=0; j<getHeight(); j+=30)
                    g2.fillOval(i, j, 3, 3);
            }
        };
        brandPanel.setPreferredSize(new Dimension(400, 0));
        brandPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx=0; gbc.gridy=GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(8,20,8,20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel logo = new JLabel("🎯");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        JLabel title = UITheme.createLabel("InternTrack Pro", new Font("Segoe UI", Font.BOLD, 28), Color.WHITE);
        JLabel subtitle = UITheme.createLabel("Your Career Launch Platform", new Font("Segoe UI", Font.PLAIN, 15), new Color(196, 181, 253));

        JPanel featuresPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        featuresPanel.setOpaque(false);
        String[] features = {"📋  Track All Applications", "🧠  Smart Skill Matching", "🎓  Certification Manager",
                "📊  Analytics Dashboard", "🤖  AI Recommendations"};
        for (String f : features) {
            JLabel fl = UITheme.createLabel(f, new Font("Segoe UI", Font.PLAIN, 14), new Color(224, 231, 255));
            featuresPanel.add(fl);
        }

        brandPanel.add(logo, gbc);
        brandPanel.add(title, gbc);
        brandPanel.add(subtitle, gbc);
        gbc.insets = new Insets(25, 20, 0, 20);
        brandPanel.add(featuresPanel, gbc);

        // Right form panel
        JPanel formContainer = new JPanel(new CardLayout());
        formContainer.setOpaque(false);
        loginPanel = buildLoginPanel();
        registerPanel = buildRegisterPanel();
        formContainer.add(loginPanel, "LOGIN");
        formContainer.add(registerPanel, "REGISTER");

        mainPanel.add(brandPanel, BorderLayout.WEST);
        mainPanel.add(formContainer, BorderLayout.CENTER);
    }

    private JPanel buildLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0; g.gridy=GridBagConstraints.RELATIVE;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(8, 60, 8, 60);
        g.weightx = 1;

        JLabel heading = UITheme.createLabel("Welcome Back! 👋", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel sub = UITheme.createLabel("Sign in to manage your internships", UITheme.FONT_BODY, UITheme.TEXT_SECOND);
        loginEmail = UITheme.createTextField("Email address");
        loginPass = UITheme.createPasswordField();
        loginPass.setPreferredSize(new Dimension(200, 40));

        JButton loginBtn = UITheme.createButton("Sign In →", UITheme.ACCENT_BLUE);
        loginBtn.setPreferredSize(new Dimension(300, 46));
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton adminBtn = UITheme.createButton("Admin Login", new Color(55, 65, 81));
        adminBtn.setPreferredSize(new Dimension(300, 40));

        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setOpaque(false);
        JLabel swLabel = UITheme.createLabel("Don't have an account? ", UITheme.FONT_BODY, UITheme.TEXT_SECOND);
        JButton swBtn = UITheme.createOutlineButton("Register here");
        swBtn.setForeground(UITheme.ACCENT_BLUE);
        swBtn.setFont(UITheme.FONT_BOLD);
        switchPanel.add(swLabel); switchPanel.add(swBtn);

        // Placeholder labels
        JLabel emailLbl = UITheme.createLabel("Email", UITheme.FONT_SMALL, UITheme.TEXT_SECOND);
        JLabel passLbl = UITheme.createLabel("Password", UITheme.FONT_SMALL, UITheme.TEXT_SECOND);

        panel.add(heading, g);
        panel.add(sub, g);
        g.insets = new Insets(16, 60, 2, 60);
        panel.add(emailLbl, g);
        g.insets = new Insets(0, 60, 8, 60);
        panel.add(loginEmail, g);
        g.insets = new Insets(8, 60, 2, 60);
        panel.add(passLbl, g);
        g.insets = new Insets(0, 60, 20, 60);
        panel.add(loginPass, g);
        g.insets = new Insets(4, 60, 4, 60);
        panel.add(loginBtn, g);
        panel.add(adminBtn, g);
        g.insets = new Insets(16, 60, 8, 60);
        panel.add(switchPanel, g);

        loginBtn.addActionListener(e -> doLogin());
        adminBtn.addActionListener(e -> doAdminLogin());
        swBtn.addActionListener(e -> switchToRegister());
        loginPass.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin(); }
        });

        return panel;
    }

    private JPanel buildRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0; g.gridy=GridBagConstraints.RELATIVE;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 50, 4, 50);
        g.weightx = 1;

        JLabel heading = UITheme.createLabel("Create Account ✨", UITheme.FONT_TITLE, UITheme.TEXT_PRIMARY);
        JLabel sub = UITheme.createLabel("Join thousands of students tracking their careers", UITheme.FONT_BODY, UITheme.TEXT_SECOND);

        regName  = UITheme.createTextField("Full Name");
        regEmail = UITheme.createTextField("Email address");
        regPass  = UITheme.createPasswordField();
        regPass.setPreferredSize(new Dimension(200, 40));
        regBranch = UITheme.createTextField("Branch (e.g. CSE, IT)");
        regPhone  = UITheme.createTextField("Phone number");
        regYear   = UITheme.createCombo(new String[]{"Year 1", "Year 2", "Year 3", "Year 4"});
        regYear.setPreferredSize(new Dimension(200, 38));

        JButton regBtn = UITheme.createButton("Create Account →", UITheme.ACCENT_PURPLE);
        regBtn.setPreferredSize(new Dimension(300, 46));
        regBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        switchPanel.setOpaque(false);
        JLabel swLabel = UITheme.createLabel("Already have an account? ", UITheme.FONT_BODY, UITheme.TEXT_SECOND);
        JButton swBtn = UITheme.createOutlineButton("Sign in");
        swBtn.setForeground(UITheme.ACCENT_BLUE);
        swBtn.setFont(UITheme.FONT_BOLD);
        switchPanel.add(swLabel); switchPanel.add(swBtn);

        panel.add(heading, g);
        panel.add(sub, g);
        for (JComponent comp : new JComponent[]{regName, regEmail, regPass, regBranch, regPhone, regYear}) {
            g.insets = new Insets(4, 50, 4, 50);
            panel.add(comp, g);
        }
        g.insets = new Insets(12, 50, 4, 50);
        panel.add(regBtn, g);
        g.insets = new Insets(8, 50, 4, 50);
        panel.add(switchPanel, g);

        regBtn.addActionListener(e -> doRegister());
        swBtn.addActionListener(e -> switchToLogin());
        return panel;
    }

    private void doLogin() {
        String email = loginEmail.getText().trim();
        String pass = new String(loginPass.getPassword());
        if (email.isEmpty() || pass.isEmpty()) {
            showError("Please enter email and password!"); return;
        }
        Student s = studentDAO.login(email, pass);
        if (s != null) {
            dispose();
            new DashboardScreen(s);
        } else {
            showError("Invalid email or password!\n\nDefault admin: admin@tracker.com / admin123");
        }
    }

    private void doAdminLogin() {
        loginEmail.setText("admin@tracker.com");
        loginPass.setText("admin123");
        doLogin();
    }

    private void doRegister() {
        String name = regName.getText().trim();
        String email = regEmail.getText().trim();
        String pass = new String(regPass.getPassword());
        String branch = regBranch.getText().trim();
        String phone = regPhone.getText().trim();
        int year = regYear.getSelectedIndex() + 1;

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || branch.isEmpty()) {
            showError("Please fill all required fields!"); return;
        }
        if (pass.length() < 6) { showError("Password must be at least 6 characters!"); return; }

        Student s = new Student();
        s.setName(name); s.setEmail(email); s.setPassword(pass);
        s.setBranch(branch); s.setYear(year); s.setPhone(phone);

        if (studentDAO.register(s)) {
            JOptionPane.showMessageDialog(this, "✅ Account created successfully!\nYou can now sign in.",
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);
            switchToLogin();
        } else {
            showError("Registration failed!\nEmail may already be in use.");
        }
    }

    private void switchToRegister() {
        CardLayout cl = (CardLayout) ((JPanel) loginPanel.getParent()).getLayout();
        cl.show(loginPanel.getParent(), "REGISTER");
    }

    private void switchToLogin() {
        CardLayout cl = (CardLayout) ((JPanel) loginPanel.getParent()).getLayout();
        cl.show(loginPanel.getParent(), "LOGIN");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}