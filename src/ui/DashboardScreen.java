package ui;

import dao.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.*;
import model.*;
import service.RecommendationService;

public class DashboardScreen extends JFrame {
    private Student currentUser;
    private final ApplicationDAO appDAO = new ApplicationDAO();
    private final InternshipDAO internDAO = new InternshipDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final RecommendationService recService = new RecommendationService();
    private JLabel notifBadge;
    private int unreadNotifs = 0;
    private javax.swing.Timer animTimer;

    public DashboardScreen(Student user) {
        this.currentUser = user;
        setTitle("InternTrack Pro — " + user.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        buildUI();
        setVisible(true);
        startDeadlineChecker();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(UITheme.BG_DARK);
        setContentPane(root);

        // Sidebar
        JPanel sidebar = buildSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // Main content with tabs
        JTabbedPane tabs = buildMainTabs();
        root.add(tabs, BorderLayout.CENTER);

        // Header bar
        JPanel header = buildHeader();
        root.add(header, BorderLayout.NORTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, UITheme.BG_SIDEBAR, getWidth(), 0, UITheme.BG_DARK);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
            }
        };
        header.setPreferredSize(new Dimension(0, 58));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel welcome = UITheme.createLabel("Welcome back, " + currentUser.getName() + " 👋",
                new Font("Segoe UI", Font.BOLD, 16), UITheme.TEXT_PRIMARY);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        // Notification bell
        JButton notifBtn = new JButton("🔔") {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (unreadNotifs > 0) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(UITheme.ACCENT_RED);
                    g2.fillOval(getWidth()-14, 2, 12, 12);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    g2.drawString(String.valueOf(Math.min(unreadNotifs,9)), getWidth()-11, 12);
                }
            }
        };
        notifBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        notifBtn.setForeground(UITheme.TEXT_PRIMARY);
        notifBtn.setBackground(UITheme.BG_CARD);
        notifBtn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        notifBtn.setFocusPainted(false);
        notifBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notifBtn.addActionListener(e -> showNotifications());

        // Role badge
        JLabel roleBadge = new JLabel(currentUser.getRole());
        roleBadge.setFont(UITheme.FONT_SMALL);
        roleBadge.setForeground(UITheme.ACCENT_CYAN);
        roleBadge.setOpaque(true);
        roleBadge.setBackground(new Color(6, 182, 212, 30));
        roleBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(6, 182, 212, 80), 1),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));

        JButton logoutBtn = UITheme.createButton("Logout", new Color(55, 65, 81));
        logoutBtn.setPreferredSize(new Dimension(90, 32));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        rightPanel.add(roleBadge);
        rightPanel.add(notifBtn);
        rightPanel.add(logoutBtn);

        header.add(welcome, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(UITheme.BG_SIDEBAR);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(UITheme.BORDER_COLOR);
                g.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        logoPanel.setOpaque(false);
        JLabel logo = UITheme.createLabel("🎯 InternTrack", new Font("Segoe UI", Font.BOLD, 17), UITheme.TEXT_PRIMARY);
        logoPanel.add(logo);

        // Avatar
        JPanel avatarSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        avatarSection.setOpaque(false);
        JLabel avatar = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0,0, UITheme.ACCENT_BLUE, getWidth(), getHeight(), UITheme.ACCENT_PURPLE);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                String initial = currentUser.getName().substring(0,1).toUpperCase();
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initial, (getWidth()-fm.stringWidth(initial))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        avatar.setPreferredSize(new Dimension(42, 42));
        JPanel namePanel = new JPanel(new GridLayout(2,1));
        namePanel.setOpaque(false);
        namePanel.add(UITheme.createLabel(currentUser.getName(), UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY));
        namePanel.add(UITheme.createLabel(currentUser.getBranch() != null ? currentUser.getBranch() : "Student", UITheme.FONT_SMALL, UITheme.TEXT_SECOND));
        avatarSection.add(avatar);
        avatarSection.add(namePanel);

        JPanel navPanel = new JPanel(new GridLayout(0, 1, 0, 2));
        navPanel.setOpaque(false);
        navPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Stats quick view
        Map<String, Integer> stats = appDAO.getStatusCounts(currentUser.getId());
        int total = appDAO.getTotalCount(currentUser.getId());
        int selected = stats.getOrDefault("SELECTED", 0);

        JPanel statsCard = new JPanel(new GridLayout(2, 2, 6, 6));
        statsCard.setBackground(new Color(22, 30, 50));
        statsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        statsCard.add(makeMiniStat("Total", String.valueOf(total), UITheme.ACCENT_BLUE));
        statsCard.add(makeMiniStat("Selected", String.valueOf(selected), UITheme.ACCENT_GREEN));
        statsCard.add(makeMiniStat("Rejected", String.valueOf(stats.getOrDefault("REJECTED",0)), UITheme.ACCENT_RED));
        statsCard.add(makeMiniStat("Pending", String.valueOf(stats.getOrDefault("APPLIED",0)+stats.getOrDefault("SHORTLISTED",0)), UITheme.ACCENT_ORANGE));

        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setOpaque(false);
        statsWrapper.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        statsWrapper.add(statsCard);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(logoPanel, BorderLayout.NORTH);
        topSection.add(avatarSection, BorderLayout.CENTER);
        topSection.add(statsWrapper, BorderLayout.SOUTH);

        sidebar.add(topSection, BorderLayout.NORTH);
        sidebar.add(navPanel, BorderLayout.CENTER);

        JLabel version = UITheme.createLabel("v2.0 — InternTrack Pro", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        version.setBorder(BorderFactory.createEmptyBorder(0,14,0,0));
        sidebar.add(version, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel makeMiniStat(String label, String value, Color color) {
        JPanel p = new JPanel(new GridLayout(2,1));
        p.setOpaque(false);
        JLabel vl = UITheme.createLabel(value, new Font("Segoe UI", Font.BOLD, 18), color);
        vl.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel ll = UITheme.createLabel(label, UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        ll.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(vl); p.add(ll);
        return p;
    }

    private JTabbedPane buildMainTabs() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setBackground(UITheme.BG_DARK);
        tabs.setForeground(UITheme.TEXT_PRIMARY);
        tabs.setFont(UITheme.FONT_BOLD);
        tabs.setBorder(BorderFactory.createEmptyBorder());
        // Custom tab UI
        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override protected void paintTabBackground(Graphics g, int tp, int ti, int x, int y, int w, int h, boolean sel) {
                Graphics2D g2 = (Graphics2D) g;
                if (sel) {
                    g2.setColor(UITheme.BG_CARD);
                    g2.fillRect(x, y, w, h);
                    g2.setColor(UITheme.ACCENT_BLUE);
                    g2.fillRect(x, y+h-3, w, 3);
                } else {
                    g2.setColor(UITheme.BG_DARK);
                    g2.fillRect(x, y, w, h);
                }
            }
            @Override protected void paintTabBorder(Graphics g, int tp, int ti, int x, int y, int w, int h, boolean sel) {}
            @Override protected void paintFocusIndicator(Graphics g, int tp, Rectangle[] r, int ti, Rectangle iconR, Rectangle textR, boolean sel) {}
            @Override protected int calculateTabHeight(int tp, int ti, int fh) { return 44; }
        });

        tabs.addTab("📊  Dashboard", buildDashboardTab());
        tabs.addTab("🔍  Internships", buildInternshipsTab());
        tabs.addTab("📋  My Applications", buildApplicationsTab());
        tabs.addTab("🧠  Skills", buildSkillsTab());
        tabs.addTab("🎓  Certifications", buildCertificationsTab());
        tabs.addTab("🤖  AI Recommendations", buildRecommendationsTab());
        tabs.addTab("📈  Analytics", buildAnalyticsTab());
        if ("ADMIN".equals(currentUser.getRole())) {
            tabs.addTab("⚙️  Admin Panel", buildAdminTab());
        }
        tabs.addTab("👤  My Profile", buildProfileTab());

        return tabs;
    }

    // ===================== DASHBOARD TAB =====================
    private JPanel buildDashboardTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = UITheme.createLabel("Overview Dashboard", new Font("Segoe UI", Font.BOLD, 22), UITheme.TEXT_PRIMARY);
        JLabel subtitle = UITheme.createLabel("Track your internship journey at a glance", UITheme.FONT_BODY, UITheme.TEXT_SECOND);
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(title); titlePanel.add(subtitle);

        // Stats cards row
        Map<String, Integer> stats = appDAO.getStatusCounts(currentUser.getId());
        int total = appDAO.getTotalCount(currentUser.getId());
        int skillCount = skillDAO.getSkillsByStudent(currentUser.getId()).size();
        int certCount = skillDAO.getCertsByStudent(currentUser.getId()).size();

        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        cardsRow.setOpaque(false);
        cardsRow.add(makeStatCard("📨", "Total Applied", String.valueOf(total), UITheme.ACCENT_BLUE, "Applications submitted"));
        cardsRow.add(makeStatCard("✅", "Selected", String.valueOf(stats.getOrDefault("SELECTED",0)), UITheme.ACCENT_GREEN, "Congratulations!"));
        cardsRow.add(makeStatCard("⭐", "Shortlisted", String.valueOf(stats.getOrDefault("SHORTLISTED",0)), UITheme.ACCENT_ORANGE, "Keep going!"));
        cardsRow.add(makeStatCard("🧠", "Skills", String.valueOf(skillCount), UITheme.ACCENT_PURPLE, certCount + " certifications"));

        // Recent applications
        JPanel recentPanel = buildRecentApplications();
        // Upcoming deadlines
        JPanel deadlinePanel = buildUpcomingDeadlines();

        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 16, 0));
        bottomRow.setOpaque(false);
        bottomRow.add(recentPanel);
        bottomRow.add(deadlinePanel);

        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);
        content.add(titlePanel, BorderLayout.NORTH);
        content.add(cardsRow, BorderLayout.CENTER);
        content.add(bottomRow, BorderLayout.SOUTH);

        panel.add(content, BorderLayout.NORTH);

        // Progress section
        JPanel progressSection = buildProgressSection(stats, total);
        panel.add(progressSection, BorderLayout.CENTER);

        return panel;
    }

    private JPanel makeStatCard(String icon, String label, String value, Color accent, String sub) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                // Left accent bar
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 16));

        JLabel iconLabel = UITheme.createLabel(icon, new Font("Segoe UI Emoji", Font.PLAIN, 28), accent);
        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 2));
        textPanel.setOpaque(false);
        textPanel.add(UITheme.createLabel(value, new Font("Segoe UI", Font.BOLD, 32), UITheme.TEXT_PRIMARY));
        textPanel.add(UITheme.createLabel(label, UITheme.FONT_BOLD, UITheme.TEXT_SECOND));
        textPanel.add(UITheme.createLabel(sub, UITheme.FONT_SMALL, UITheme.TEXT_MUTED));

        card.add(iconLabel, BorderLayout.WEST);
        JPanel spacer = new JPanel(); spacer.setOpaque(false); spacer.setPreferredSize(new Dimension(12,0));
        card.add(spacer, BorderLayout.CENTER);
        card.add(textPanel, BorderLayout.EAST);
        card.setPreferredSize(new Dimension(0, 110));
        return card;
    }

    private JPanel buildRecentApplications() {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 10));
        card.add(UITheme.createLabel("📋 Recent Applications", UITheme.FONT_SUBHEAD, UITheme.TEXT_PRIMARY), BorderLayout.NORTH);

        List<Application> apps = appDAO.getByStudent(currentUser.getId());
        JPanel listPanel = new JPanel(new GridLayout(Math.min(apps.size(), 4), 1, 0, 6));
        listPanel.setOpaque(false);

        for (int i = 0; i < Math.min(apps.size(), 4); i++) {
            Application a = apps.get(i);
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR));
            row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            JLabel nameLabel = UITheme.createLabel(a.getCompanyName() + " — " + a.getRole(), UITheme.FONT_BODY, UITheme.TEXT_PRIMARY);
            JLabel statusLabel = makeStatusBadge(a.getStatus());
            row.add(nameLabel, BorderLayout.WEST);
            row.add(statusLabel, BorderLayout.EAST);
            listPanel.add(row);
        }
        if (apps.isEmpty()) listPanel.add(UITheme.createLabel("No applications yet. Start applying!", UITheme.FONT_BODY, UITheme.TEXT_MUTED));

        card.add(listPanel, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(0, 220));
        return card;
    }

    private JPanel buildUpcomingDeadlines() {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 10));
        card.add(UITheme.createLabel("⏰ Upcoming Deadlines", UITheme.FONT_SUBHEAD, UITheme.TEXT_PRIMARY), BorderLayout.NORTH);

        List<Internship> internships = internDAO.getAll();
        JPanel listPanel = new JPanel(new GridLayout(Math.min(internships.size(), 4), 1, 0, 6));
        listPanel.setOpaque(false);

        int count = 0;
        for (Internship i : internships) {
            if (count >= 4) break;
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            JLabel nameLabel = UITheme.createLabel(i.getCompanyName() + " – " + i.getRole(), UITheme.FONT_BODY, UITheme.TEXT_PRIMARY);
            JLabel dateLabel = UITheme.createLabel(i.getDeadline() != null ? i.getDeadline().toString() : "N/A",
                    UITheme.FONT_SMALL, UITheme.ACCENT_ORANGE);
            row.add(nameLabel, BorderLayout.WEST);
            row.add(dateLabel, BorderLayout.EAST);
            listPanel.add(row);
            count++;
        }
        if (internships.isEmpty()) listPanel.add(UITheme.createLabel("No active internships.", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        card.add(listPanel, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(0, 220));
        return card;
    }

    private JPanel buildProgressSection(Map<String, Integer> stats, int total) {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        card.add(UITheme.createLabel("📊 Application Status Breakdown", UITheme.FONT_SUBHEAD, UITheme.TEXT_PRIMARY), BorderLayout.NORTH);

        JPanel barsPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        barsPanel.setOpaque(false);

        String[][] statData = {
            {"Applied",     String.valueOf(stats.getOrDefault("APPLIED", 0)),     "#3B82F6"},
            {"Shortlisted", String.valueOf(stats.getOrDefault("SHORTLISTED", 0)), "#F59E0B"},
            {"Selected",    String.valueOf(stats.getOrDefault("SELECTED", 0)),    "#10B981"},
            {"Rejected",    String.valueOf(stats.getOrDefault("REJECTED", 0)),    "#EF4444"}
        };

        for (String[] sd : statData) {
            int val = Integer.parseInt(sd[1]);
            int pct = total > 0 ? (val * 100) / total : 0;
            Color c = Color.decode(sd[2]);
            barsPanel.add(makeProgressBar(sd[0], val, pct, c));
        }

        card.add(barsPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel makeProgressBar(String label, int value, int pct, Color color) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);

        JLabel labelL = UITheme.createLabel(label, UITheme.FONT_BODY, UITheme.TEXT_SECOND);
        labelL.setPreferredSize(new Dimension(100, 20));
        JLabel valueL = UITheme.createLabel(value + " (" + pct + "%)", UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        valueL.setPreferredSize(new Dimension(80, 20));

        JPanel barBg = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BORDER_COLOR);
                g2.fillRoundRect(0, 4, getWidth(), 8, 8, 8);
                if (pct > 0) {
                    g2.setColor(color);
                    g2.fillRoundRect(0, 4, (int)(getWidth() * pct / 100.0), 8, 8, 8);
                }
            }
        };
        barBg.setOpaque(false);
        barBg.setPreferredSize(new Dimension(0, 16));

        row.add(labelL, BorderLayout.WEST);
        row.add(barBg, BorderLayout.CENTER);
        row.add(valueL, BorderLayout.EAST);
        return row;
    }

    // ===================== INTERNSHIPS TAB =====================
    private JPanel buildInternshipsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        JTextField searchField = UITheme.createTextField("🔍  Search company, role, skill...");
        searchField.setPreferredSize(new Dimension(280, 40));
        JTextField locationField = UITheme.createTextField("📍  Location");
        locationField.setPreferredSize(new Dimension(150, 40));
        JTextField skillField = UITheme.createTextField("🧠  Skill filter");
        skillField.setPreferredSize(new Dimension(150, 40));
        JButton searchBtn = UITheme.createButton("Search", UITheme.ACCENT_BLUE);
        JButton clearBtn = UITheme.createOutlineButton("Clear");
        clearBtn.setPreferredSize(new Dimension(70, 40));
        JLabel titleLbl = UITheme.createLabel("🔍  Browse Internships", new Font("Segoe UI", Font.BOLD, 20), UITheme.TEXT_PRIMARY);

        searchPanel.add(searchField); searchPanel.add(locationField);
        searchPanel.add(skillField); searchPanel.add(searchBtn); searchPanel.add(clearBtn);

        JPanel topBar = new JPanel(new BorderLayout(0, 8));
        topBar.setOpaque(false);
        topBar.add(titleLbl, BorderLayout.NORTH);
        topBar.add(searchPanel, BorderLayout.SOUTH);

        // Cards grid for internships
        JPanel cardsGrid = new JPanel();
        cardsGrid.setBackground(UITheme.BG_DARK);
        cardsGrid.setLayout(new WrapLayout(FlowLayout.LEFT, 16, 16));
        JScrollPane scrollPane = UITheme.createScrollPane(cardsGrid);
        scrollPane.getViewport().setBackground(UITheme.BG_DARK);

        loadInternshipCards(cardsGrid, null, null, null);

        searchBtn.addActionListener(e -> {
            cardsGrid.removeAll();
            loadInternshipCards(cardsGrid,
                searchField.getText().trim(),
                skillField.getText().trim(),
                locationField.getText().trim());
            cardsGrid.revalidate();
            cardsGrid.repaint();
        });
        clearBtn.addActionListener(e -> {
            searchField.setText(""); locationField.setText(""); skillField.setText("");
            cardsGrid.removeAll();
            loadInternshipCards(cardsGrid, null, null, null);
            cardsGrid.revalidate(); cardsGrid.repaint();
        });

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadInternshipCards(JPanel container, String kw, String skill, String loc) {
        List<Internship> list;
        if ((kw != null && !kw.isEmpty()) || (skill != null && !skill.isEmpty()) || (loc != null && !loc.isEmpty())) {
            list = internDAO.search(kw, skill, loc);
        } else {
            list = internDAO.getAll();
        }
        container.removeAll();
        if (list.isEmpty()) {
            container.add(UITheme.createLabel("No internships found matching your criteria.", UITheme.FONT_BODY, UITheme.TEXT_SECOND));
            return;
        }
        for (Internship i : list) container.add(makeInternshipCard(i));
        container.revalidate(); container.repaint();
    }

    private JPanel makeInternshipCard(Internship i) {
        JPanel card = new JPanel(new BorderLayout(0, 8)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(UITheme.BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(340, 220));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Match percent
        int matchPct = recService.calculateMatchPercent(currentUser.getId(), i);
        Color matchColor = matchPct >= 70 ? UITheme.ACCENT_GREEN : matchPct >= 40 ? UITheme.ACCENT_ORANGE : UITheme.ACCENT_RED;

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        JLabel company = UITheme.createLabel(i.getCompanyName(), new Font("Segoe UI", Font.BOLD, 16), UITheme.TEXT_PRIMARY);
        JLabel matchBadge = makeStatusBadge(matchPct + "% match");
        matchBadge.setForeground(matchColor);
        topRow.add(company, BorderLayout.WEST);
        topRow.add(matchBadge, BorderLayout.EAST);

        JLabel roleLabel = UITheme.createLabel("💼 " + i.getRole(), UITheme.FONT_BODY, UITheme.TEXT_SECOND);
        JLabel locLabel = UITheme.createLabel("📍 " + (i.getLocation() != null ? i.getLocation() : "Remote"), UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        JLabel stipLabel = UITheme.createLabel("💰 " + (i.getStipend() != null ? i.getStipend() : "Not specified"), UITheme.FONT_SMALL, UITheme.ACCENT_GREEN);
        JLabel deadLabel = UITheme.createLabel("⏰ Deadline: " + (i.getDeadline() != null ? i.getDeadline() : "Open"), UITheme.FONT_SMALL, UITheme.ACCENT_ORANGE);

        // Skills chips
        JPanel skillsRow = new JPanel(new WrapLayout(FlowLayout.LEFT, 4, 2));
        skillsRow.setOpaque(false);
        if (i.getRequiredSkills() != null) {
            for (String sk : i.getRequiredSkills().split(",")) {
                JLabel chip = makeSkillChip(sk.trim());
                skillsRow.add(chip);
            }
        }

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        btnRow.setOpaque(false);
        JButton viewBtn = UITheme.createOutlineButton("Details");
        viewBtn.setPreferredSize(new Dimension(75, 32));
        JButton applyBtn = UITheme.createButton("Apply Now", UITheme.ACCENT_BLUE);
        applyBtn.setPreferredSize(new Dimension(100, 32));
        applyBtn.setFont(UITheme.FONT_SMALL);
        btnRow.add(viewBtn); btnRow.add(applyBtn);

        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 0, 2));
        infoPanel.setOpaque(false);
        infoPanel.add(roleLabel); infoPanel.add(locLabel); infoPanel.add(stipLabel); infoPanel.add(deadLabel);

        card.add(topRow, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(skillsRow, BorderLayout.NORTH);
        bottom.add(btnRow, BorderLayout.SOUTH);
        card.add(bottom, BorderLayout.SOUTH);

        applyBtn.addActionListener(e -> handleApply(i));
        viewBtn.addActionListener(e -> showInternshipDetails(i));
        return card;
    }

    private void handleApply(Internship i) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "<html><b>Apply to: " + i.getCompanyName() + " — " + i.getRole() + "</b><br>Are you sure?</html>",
            "Confirm Application", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = appDAO.apply(currentUser.getId(), i.getId());
            if (success) {
                JOptionPane.showMessageDialog(this, "✅ Application submitted successfully!", "Applied", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ You have already applied to this internship!", "Already Applied", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void showInternshipDetails(Internship i) {
        JDialog dialog = new JDialog(this, i.getCompanyName() + " — " + i.getRole(), true);
        dialog.setSize(520, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JTextArea desc = new JTextArea();
        desc.setEditable(false);
        desc.setBackground(UITheme.BG_CARD);
        desc.setForeground(UITheme.TEXT_PRIMARY);
        desc.setFont(UITheme.FONT_BODY);
        desc.setWrapStyleWord(true);
        desc.setLineWrap(true);

        int matchPct = recService.calculateMatchPercent(currentUser.getId(), i);
        List<String> gaps = recService.getSkillGaps(currentUser.getId(), i);

        StringBuilder sb = new StringBuilder();
        sb.append("Company: ").append(i.getCompanyName()).append("\n");
        sb.append("Role: ").append(i.getRole()).append("\n");
        sb.append("Location: ").append(i.getLocation()).append("\n");
        sb.append("Stipend: ").append(i.getStipend()).append("\n");
        sb.append("Duration: ").append(i.getDuration()).append("\n");
        sb.append("Deadline: ").append(i.getDeadline()).append("\n\n");
        sb.append("Description:\n").append(i.getDescription()).append("\n\n");
        sb.append("Required Skills: ").append(i.getRequiredSkills()).append("\n\n");
        sb.append("Your Match: ").append(matchPct).append("%\n");
        if (!gaps.isEmpty()) sb.append("Skill Gaps: ").append(String.join(", ", gaps));
        desc.setText(sb.toString());

        JButton applyBtn = UITheme.createButton("Apply Now", UITheme.ACCENT_BLUE);
        applyBtn.addActionListener(e -> { dialog.dispose(); handleApply(i); });

        panel.add(UITheme.createLabel(i.getCompanyName() + " — " + i.getRole(), UITheme.FONT_HEADING, UITheme.TEXT_PRIMARY), BorderLayout.NORTH);
        panel.add(UITheme.createScrollPane(desc), BorderLayout.CENTER);
        panel.add(applyBtn, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    // ===================== APPLICATIONS TAB =====================
    private JPanel buildApplicationsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = UITheme.createLabel("📋 My Applications", new Font("Segoe UI", Font.BOLD, 20), UITheme.TEXT_PRIMARY);

        // Filter buttons
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterRow.setOpaque(false);
        filterRow.add(UITheme.createLabel("Filter: ", UITheme.FONT_BOLD, UITheme.TEXT_SECOND));
        String[] filters = {"All", "Applied", "Shortlisted", "Selected", "Rejected"};
        Color[] fColors = {UITheme.TEXT_MUTED, UITheme.STATUS_APPLIED, UITheme.STATUS_SHORTLISTED, UITheme.STATUS_SELECTED, UITheme.STATUS_REJECTED};

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(UITheme.BG_CARD);
        tableContainer.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));

        Runnable[] loadTable = {null};
        String[] activeFilter = {"All"};

        for (int fi = 0; fi < filters.length; fi++) {
            final String f = filters[fi];
            final Color fc = fColors[fi];
            JButton btn = UITheme.createButton(f, UITheme.BG_CARD);
            btn.setPreferredSize(new Dimension(100, 32));
            btn.setFont(UITheme.FONT_SMALL);
            filterRow.add(btn);
            btn.addActionListener(e -> {
                activeFilter[0] = f;
                if (loadTable[0] != null) loadTable[0].run();
            });
        }

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setOpaque(false);
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(filterRow, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);

        loadTable[0] = () -> {
            tableContainer.removeAll();
            List<Application> apps = appDAO.getByStudent(currentUser.getId());
            String[] cols = {"Company", "Role", "Location", "Applied Date", "Status", "Action"};
            List<Object[]> rows = new ArrayList<>();
            for (Application a : apps) {
                if (!"All".equals(activeFilter[0]) && !activeFilter[0].equalsIgnoreCase(a.getStatus())) continue;
                rows.add(new Object[]{a.getCompanyName(), a.getRole(), a.getLocation(),
                    a.getAppliedDate() != null ? a.getAppliedDate().toString().substring(0, 10) : "", a.getStatus(), "Update"});
            }
            Object[][] data = rows.toArray(new Object[0][]);
            JTable table = UITheme.createStyledTable(cols, data);

            // Color status column
            table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                    super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                    setBackground(sel ? new Color(59,130,246,60) : UITheme.BG_CARD);
                    setForeground(UITheme.getStatusColor(v != null ? v.toString() : ""));
                    setFont(UITheme.FONT_BOLD);
                    return this;
                }
            });

            // Update button column
            table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                    JButton b = UITheme.createButton("Update", UITheme.ACCENT_BLUE);
                    b.setFont(UITheme.FONT_SMALL);
                    return b;
                }
            });

            table.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if (col == 5 && row >= 0) {
                        Application a = apps.stream().filter(ap ->
                            "All".equals(activeFilter[0]) || activeFilter[0].equalsIgnoreCase(ap.getStatus()))
                            .collect(java.util.stream.Collectors.toList()).get(row);
                        showUpdateStatusDialog(a, loadTable[0]);
                    }
                }
            });

            tableContainer.add(UITheme.createScrollPane(table));
            tableContainer.revalidate(); tableContainer.repaint();
        };

        loadTable[0].run();
        panel.add(tableContainer, BorderLayout.CENTER);
        return panel;
    }

    private void showUpdateStatusDialog(Application app, Runnable onUpdate) {
        String[] statuses = {"APPLIED", "SHORTLISTED", "SELECTED", "REJECTED"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Update status for: " + app.getCompanyName() + " — " + app.getRole(),
            "Update Application Status",
            JOptionPane.QUESTION_MESSAGE, null, statuses, app.getStatus());
        if (newStatus != null && appDAO.updateStatus(app.getId(), newStatus)) {
            JOptionPane.showMessageDialog(this, "✅ Status updated to: " + newStatus);
            onUpdate.run();
        }
    }

    // ===================== SKILLS TAB =====================
    private JPanel buildSkillsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = UITheme.createLabel("🧠 Skills Management", new Font("Segoe UI", Font.BOLD, 20), UITheme.TEXT_PRIMARY);

        JPanel addPanel = UITheme.createCard();
        addPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JTextField skillNameField = UITheme.createTextField("Skill name (e.g., Java)");
        skillNameField.setPreferredSize(new Dimension(200, 38));
        JComboBox<String> profCombo = UITheme.createCombo(new String[]{"Beginner", "Intermediate", "Advanced", "Expert"});
        profCombo.setPreferredSize(new Dimension(140, 38));
        JButton addSkillBtn = UITheme.createButton("+ Add Skill", UITheme.ACCENT_BLUE);
        addPanel.add(UITheme.createLabel("Add New Skill:", UITheme.FONT_BOLD, UITheme.TEXT_SECOND));
        addPanel.add(skillNameField);
        addPanel.add(UITheme.createLabel("Level:", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        addPanel.add(profCombo);
        addPanel.add(addSkillBtn);

        // Skills cards grid
        JPanel skillsGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        skillsGrid.setBackground(UITheme.BG_DARK);
        JScrollPane scrollPane = UITheme.createScrollPane(skillsGrid);
        scrollPane.getViewport().setBackground(UITheme.BG_DARK);

        Runnable loadSkills = () -> {
            skillsGrid.removeAll();
            List<model.Skill> skills = skillDAO.getSkillsByStudent(currentUser.getId());
            if (skills.isEmpty()) {
                skillsGrid.add(UITheme.createLabel("No skills added yet. Add your first skill above!", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
            }
            for (model.Skill sk : skills) {
                skillsGrid.add(makeSkillCard(sk, skillsGrid));
            }
            skillsGrid.revalidate(); skillsGrid.repaint();
        };

        addSkillBtn.addActionListener(e -> {
            String skillName = skillNameField.getText().trim();
            if (skillName.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter skill name!"); return; }
            model.Skill sk = new model.Skill(0, currentUser.getId(), skillName, (String)profCombo.getSelectedItem());
            if (skillDAO.addSkill(sk)) {
                skillNameField.setText("");
                loadSkills.run();
            }
        });

        loadSkills.run();

        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setOpaque(false);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(addPanel, BorderLayout.SOUTH);

        panel.add(topSection, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel makeSkillCard(model.Skill sk, JPanel parent) {
        JPanel card = new JPanel(new BorderLayout(8, 4)) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(UITheme.ACCENT_PURPLE);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(200, 80));
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 12));

        String prof = sk.getProficiency() != null ? sk.getProficiency() : "Intermediate";
        int profPct = switch(prof.toLowerCase()) {
            case "expert" -> 100; case "advanced" -> 75; case "intermediate" -> 50; default -> 25;
        };
        Color profColor = profPct >= 75 ? UITheme.ACCENT_GREEN : profPct >= 50 ? UITheme.ACCENT_BLUE : UITheme.ACCENT_ORANGE;

        JLabel nameLabel = UITheme.createLabel(sk.getSkillName(), UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY);
        JLabel profLabel = UITheme.createLabel(prof, UITheme.FONT_SMALL, profColor);

        JButton deleteBtn = new JButton("×");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        deleteBtn.setForeground(UITheme.TEXT_MUTED);
        deleteBtn.setBorder(BorderFactory.createEmptyBorder());
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.addActionListener(e -> {
            if (skillDAO.deleteSkill(sk.getId())) {
                parent.remove(card);
                parent.revalidate(); parent.repaint();
            }
        });

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(nameLabel, BorderLayout.WEST);
        topRow.add(deleteBtn, BorderLayout.EAST);

        card.add(topRow, BorderLayout.NORTH);
        card.add(profLabel, BorderLayout.CENTER);
        return card;
    }

    // ===================== CERTIFICATIONS TAB =====================
    private JPanel buildCertificationsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = UITheme.createLabel("🎓 Certifications", new Font("Segoe UI", Font.BOLD, 20), UITheme.TEXT_PRIMARY);

        JPanel addPanel = UITheme.createCard();
        addPanel.setLayout(new GridLayout(2, 4, 10, 10));
        JTextField courseField = UITheme.createTextField("Course Name");
        JTextField platformField = UITheme.createTextField("Platform (Coursera, etc.)");
        JTextField dateField = UITheme.createTextField("Date (YYYY-MM-DD)");
        JTextField skillField = UITheme.createTextField("Related Skill");
        JTextField linkField = UITheme.createTextField("Certificate Link");
        JButton addBtn = UITheme.createButton("+ Add", UITheme.ACCENT_GREEN);

        addPanel.add(UITheme.createLabel("Course:", UITheme.FONT_SMALL, UITheme.TEXT_SECOND));
        addPanel.add(courseField);
        addPanel.add(UITheme.createLabel("Platform:", UITheme.FONT_SMALL, UITheme.TEXT_SECOND));
        addPanel.add(platformField);
        addPanel.add(UITheme.createLabel("Date:", UITheme.FONT_SMALL, UITheme.TEXT_SECOND));
        addPanel.add(dateField);
        addPanel.add(UITheme.createLabel("Skill:", UITheme.FONT_SMALL, UITheme.TEXT_SECOND));
        addPanel.add(skillField);

        JPanel certList = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        certList.setBackground(UITheme.BG_DARK);

        Runnable loadCerts = () -> {
            certList.removeAll();
            List<model.Certification> certs = skillDAO.getCertsByStudent(currentUser.getId());
            if (certs.isEmpty()) certList.add(UITheme.createLabel("No certifications added yet.", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
            for (model.Certification c : certs) {
                JPanel card = UITheme.createCard();
                card.setLayout(new GridLayout(4, 1, 0, 4));
                card.setPreferredSize(new Dimension(260, 110));
                card.add(UITheme.createLabel("🎓 " + c.getCourseName(), UITheme.FONT_BOLD, UITheme.TEXT_PRIMARY));
                card.add(UITheme.createLabel("📚 " + c.getPlatform(), UITheme.FONT_BODY, UITheme.TEXT_SECOND));
                card.add(UITheme.createLabel("📅 " + (c.getCompletionDate() != null ? c.getCompletionDate().toString() : "N/A"), UITheme.FONT_SMALL, UITheme.ACCENT_CYAN));
                card.add(UITheme.createLabel("🔗 " + (c.getRelatedSkill() != null ? c.getRelatedSkill() : ""), UITheme.FONT_SMALL, UITheme.ACCENT_GREEN));
                certList.add(card);
            }
            certList.revalidate(); certList.repaint();
        };

        addBtn.addActionListener(e -> {
            String course = courseField.getText().trim();
            if (course.isEmpty()) return;
            model.Certification c = new model.Certification();
            c.setStudentId(currentUser.getId());
            c.setCourseName(course);
            c.setPlatform(platformField.getText().trim());
            c.setRelatedSkill(skillField.getText().trim());
            c.setCertificateLink(linkField.getText().trim());
            try {
                if (!dateField.getText().trim().isEmpty())
                    c.setCompletionDate(java.sql.Date.valueOf(dateField.getText().trim()));
            } catch (Exception ex) {}
            if (skillDAO.addCert(c)) {
                courseField.setText(""); platformField.setText(""); dateField.setText(""); skillField.setText("");
                loadCerts.run();
            }
        });

        loadCerts.run();

        JPanel topSection = new JPanel(new BorderLayout(0, 12));
        topSection.setOpaque(false);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(addPanel, BorderLayout.CENTER);

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addRow.setOpaque(false);
        addRow.add(linkField); addRow.add(addBtn);
        topSection.add(addRow, BorderLayout.SOUTH);

        panel.add(topSection, BorderLayout.NORTH);
        panel.add(UITheme.createScrollPane(certList), BorderLayout.CENTER);
        return panel;
    }

    // ===================== RECOMMENDATIONS TAB =====================
    private JPanel buildRecommendationsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = UITheme.createLabel("🤖 AI-Powered Recommendations", new Font("Segoe UI", Font.BOLD, 20), UITheme.TEXT_PRIMARY);
        JLabel sub = UITheme.createLabel("Personalized internships matched to your skills", UITheme.FONT_BODY, UITheme.TEXT_SECOND);

        JPanel topInfo = new JPanel(new GridLayout(2, 1));
        topInfo.setOpaque(false);
        topInfo.add(title); topInfo.add(sub);

        JPanel cardsGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 16, 16));
        cardsGrid.setBackground(UITheme.BG_DARK);

        List<Internship> recs = recService.getRecommendations(currentUser.getId());
        if (recs.isEmpty()) {
            cardsGrid.add(UITheme.createLabel("Add skills to get personalized recommendations!", UITheme.FONT_BODY, UITheme.TEXT_MUTED));
        }
        for (int idx = 0; idx < Math.min(recs.size(), 12); idx++) {
            Internship i = recs.get(idx);
            int match = recService.calculateMatchPercent(currentUser.getId(), i);
            JPanel card = makeInternshipCard(i);
            // Add recommendation rank badge
            cardsGrid.add(card);
        }

        panel.add(topInfo, BorderLayout.NORTH);
        panel.add(UITheme.createScrollPane(cardsGrid), BorderLayout.CENTER);
        return panel;
    }

    // ===================== ANALYTICS TAB =====================
    private JPanel buildAnalyticsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = UITheme.createLabel("📈 Analytics & Insights", new Font("Segoe UI", Font.BOLD, 20), UITheme.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setOpaque(false);

        Map<String, Integer> stats = appDAO.getStatusCounts(currentUser.getId());
        int total = appDAO.getTotalCount(currentUser.getId());

        // Donut chart
        chartsPanel.add(makeDonutChart(stats, total));
        // Bar chart (skill breakdown)
        chartsPanel.add(makeSkillBarChart());

        panel.add(chartsPanel, BorderLayout.CENTER);

        // Bottom stats
        JPanel bottomStats = new JPanel(new GridLayout(1, 3, 16, 0));
        bottomStats.setOpaque(false);
        bottomStats.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        int selected = stats.getOrDefault("SELECTED", 0);
        double successRate = total > 0 ? (selected * 100.0 / total) : 0;

        bottomStats.add(makeAnalyticCard("Success Rate", String.format("%.1f%%", successRate), UITheme.ACCENT_GREEN,
            "Selected / Total Applied"));
        bottomStats.add(makeAnalyticCard("Most Active", "Applications", UITheme.ACCENT_BLUE,
            "Keep applying to improve odds"));
        bottomStats.add(makeAnalyticCard("Skill Level", skillDAO.getSkillsByStudent(currentUser.getId()).size() + " skills",
            UITheme.ACCENT_PURPLE, "Add more to increase matches"));

        panel.add(bottomStats, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel makeDonutChart(Map<String, Integer> stats, int total) {
        return new JPanel() {
            { setBackground(UITheme.BG_CARD); setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR), BorderFactory.createEmptyBorder(20,20,20,20)));
            }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.setFont(UITheme.FONT_SUBHEAD);
                g2.drawString("Application Status", 20, 30);

                int cx = getWidth()/2, cy = getHeight()/2 + 10, r = Math.min(getWidth(), getHeight())/3;
                String[] labels = {"APPLIED","SHORTLISTED","SELECTED","REJECTED"};
                Color[] colors = {UITheme.STATUS_APPLIED, UITheme.STATUS_SHORTLISTED, UITheme.STATUS_SELECTED, UITheme.STATUS_REJECTED};
                int startAngle = 90;
                for (int i=0; i<labels.length; i++) {
                    int val = stats.getOrDefault(labels[i], 0);
                    int arc = total > 0 ? (int)(val * 360.0 / total) : (i==0?360:0);
                    g2.setColor(colors[i]);
                    g2.fillArc(cx-r, cy-r, 2*r, 2*r, startAngle, arc);
                    startAngle += arc;
                }
                // Center hole
                g2.setColor(UITheme.BG_CARD);
                int hr = r/2;
                g2.fillOval(cx-hr, cy-hr, 2*hr, 2*hr);
                // Center text
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                String totalText = String.valueOf(total);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(totalText, cx - fm.stringWidth(totalText)/2, cy + fm.getAscent()/2 - 2);
                g2.setFont(UITheme.FONT_SMALL);
                g2.setColor(UITheme.TEXT_MUTED);
                g2.drawString("Total", cx - g2.getFontMetrics().stringWidth("Total")/2, cy + fm.getAscent()/2 + 14);

                // Legend
                int ly = getHeight() - 60;
                for (int i=0; i<labels.length; i++) {
                    g2.setColor(colors[i]);
                    g2.fillRoundRect(20 + i*110, ly, 14, 14, 4, 4);
                    g2.setColor(UITheme.TEXT_SECOND);
                    g2.setFont(UITheme.FONT_SMALL);
                    g2.drawString(labels[i], 38 + i*110, ly+12);
                }
            }
        };
    }

    private JPanel makeSkillBarChart() {
        return new JPanel() {
            { setBackground(UITheme.BG_CARD); setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER_COLOR), BorderFactory.createEmptyBorder(20,20,20,20)));
            }
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.setFont(UITheme.FONT_SUBHEAD);
                g2.drawString("Skill Proficiency", 20, 30);

                List<model.Skill> skills = skillDAO.getSkillsByStudent(currentUser.getId());
                if (skills.isEmpty()) {
                    g2.setColor(UITheme.TEXT_MUTED);
                    g2.setFont(UITheme.FONT_BODY);
                    g2.drawString("Add skills to see chart", getWidth()/2 - 70, getHeight()/2);
                    return;
                }

                int barH = 28, gap = 12, startY = 55;
                Color[] barColors = {UITheme.ACCENT_BLUE, UITheme.ACCENT_PURPLE, UITheme.ACCENT_CYAN, UITheme.ACCENT_GREEN, UITheme.ACCENT_ORANGE, UITheme.ACCENT_PINK};

                for (int i = 0; i < Math.min(skills.size(), 8); i++) {
                    model.Skill sk = skills.get(i);
                    String prof = sk.getProficiency() != null ? sk.getProficiency() : "Beginner";
                    int pct = switch(prof.toLowerCase()) { case "expert"->100; case "advanced"->75; case "intermediate"->50; default->25; };
                    int maxW = getWidth() - 140;
                    int barW = (int)(maxW * pct / 100.0);
                    int y = startY + i * (barH + gap);
                    Color c = barColors[i % barColors.length];

                    // Label
                    g2.setColor(UITheme.TEXT_SECOND);
                    g2.setFont(UITheme.FONT_BODY);
                    g2.drawString(sk.getSkillName(), 10, y + barH - 8);

                    // Background bar
                    g2.setColor(UITheme.BORDER_COLOR);
                    g2.fillRoundRect(110, y, maxW, barH, 6, 6);

                    // Filled bar
                    g2.setColor(c);
                    if (barW > 0) g2.fillRoundRect(110, y, barW, barH, 6, 6);

                    // Percent label
                    g2.setColor(UITheme.TEXT_SECOND);
                    g2.setFont(UITheme.FONT_SMALL);
                    g2.drawString(pct + "%", 110 + maxW + 6, y + barH - 8);
                }
            }
        };
    }

    private JPanel makeAnalyticCard(String title, String value, Color color, String desc) {
        JPanel card = UITheme.createCard();
        card.setLayout(new GridLayout(3, 1, 0, 4));
        card.add(UITheme.createLabel(title, UITheme.FONT_BOLD, UITheme.TEXT_SECOND));
        card.add(UITheme.createLabel(value, new Font("Segoe UI", Font.BOLD, 22), color));
        card.add(UITheme.createLabel(desc, UITheme.FONT_SMALL, UITheme.TEXT_MUTED));
        return card;
    }

    // ===================== ADMIN TAB =====================
    private JPanel buildAdminTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = UITheme.createLabel("⚙️ Admin Panel", new Font("Segoe UI", Font.BOLD, 20), UITheme.TEXT_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        JTabbedPane adminTabs = new JTabbedPane();
        adminTabs.setBackground(UITheme.BG_CARD);
        adminTabs.setForeground(UITheme.TEXT_PRIMARY);
        adminTabs.setFont(UITheme.FONT_BODY);

        adminTabs.addTab("Manage Internships", buildManageInternshipsPanel());
        adminTabs.addTab("All Applications", buildAllApplicationsPanel());
        adminTabs.addTab("All Students", buildAllStudentsPanel());

        panel.add(adminTabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildManageInternshipsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JButton addBtn = UITheme.createButton("+ Add Internship", UITheme.ACCENT_GREEN);
        JButton refreshBtn = UITheme.createButton("Refresh", UITheme.ACCENT_BLUE);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setOpaque(false);
        btnRow.add(addBtn); btnRow.add(refreshBtn);

        String[] cols = {"ID","Company","Role","Location","Deadline","Stipend","Action"};
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(UITheme.BG_CARD);

        Runnable loadTable = () -> {
            List<Internship> list = internDAO.getAll();
            Object[][] data = new Object[list.size()][7];
            for (int i = 0; i < list.size(); i++) {
                Internship in = list.get(i);
                data[i] = new Object[]{in.getId(), in.getCompanyName(), in.getRole(),
                    in.getLocation(), in.getDeadline(), in.getStipend(), "Delete"};
            }
            JTable table = UITheme.createStyledTable(cols, data);
            table.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (table.columnAtPoint(e.getPoint()) == 6) {
                        int row = table.rowAtPoint(e.getPoint());
                        int id = (int) table.getValueAt(row, 0);
                        if (JOptionPane.showConfirmDialog(null, "Delete this internship?", "Confirm", JOptionPane.YES_NO_OPTION) == 0) {
                            internDAO.delete(id);
                        }
                    }
                }
            });
            tableWrapper.removeAll();
            tableWrapper.add(UITheme.createScrollPane(table));
            tableWrapper.revalidate(); tableWrapper.repaint();
        };

        addBtn.addActionListener(e -> showAddInternshipDialog(loadTable));
        refreshBtn.addActionListener(e -> loadTable.run());
        loadTable.run();

        panel.add(btnRow, BorderLayout.NORTH);
        panel.add(tableWrapper, BorderLayout.CENTER);
        return panel;
    }

    private void showAddInternshipDialog(Runnable onAdd) {
        JDialog d = new JDialog(this, "Add New Internship", true);
        d.setSize(480, 500);
        d.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(UITheme.BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField company = UITheme.createTextField("Company Name");
        JTextField role = UITheme.createTextField("Role");
        JTextField location = UITheme.createTextField("Location");
        JTextField stipend = UITheme.createTextField("Stipend");
        JTextField duration = UITheme.createTextField("Duration");
        JTextField deadline = UITheme.createTextField("Deadline (YYYY-MM-DD)");
        JTextField skills = UITheme.createTextField("Required Skills (comma-separated)");
        JTextField link = UITheme.createTextField("Apply Link (optional)");
        JButton saveBtn = UITheme.createButton("Save", UITheme.ACCENT_GREEN);
        JButton cancelBtn = UITheme.createOutlineButton("Cancel");

        panel.add(UITheme.createLabel("Company:", UITheme.FONT_BODY, UITheme.TEXT_SECOND)); panel.add(company);
        panel.add(UITheme.createLabel("Role:", UITheme.FONT_BODY, UITheme.TEXT_SECOND)); panel.add(role);
        panel.add(UITheme.createLabel("Location:", UITheme.FONT_BODY, UITheme.TEXT_SECOND)); panel.add(location);
        panel.add(UITheme.createLabel("Stipend:", UITheme.FONT_BODY, UITheme.TEXT_SECOND)); panel.add(stipend);
        panel.add(UITheme.createLabel("Duration:", UITheme.FONT_BODY, UITheme.TEXT_SECOND)); panel.add(duration);
        panel.add(UITheme.createLabel("Deadline:", UITheme.FONT_BODY, UITheme.TEXT_SECOND)); panel.add(deadline);
        panel.add(UITheme.createLabel("Skills:", UITheme.FONT_BODY, UITheme.TEXT_SECOND)); panel.add(skills);
        panel.add(UITheme.createLabel("Apply Link:", UITheme.FONT_BODY, UITheme.TEXT_SECOND)); panel.add(link);
        panel.add(cancelBtn); panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            Internship i = new Internship();
            i.setCompanyName(company.getText().trim()); i.setRole(role.getText().trim());
            i.setLocation(location.getText().trim()); i.setStipend(stipend.getText().trim());
            i.setDuration(duration.getText().trim()); i.setRequiredSkills(skills.getText().trim());
            i.setApplyLink(link.getText().trim()); i.setPostedBy(currentUser.getId());
            try { if (!deadline.getText().trim().isEmpty()) i.setDeadline(java.sql.Date.valueOf(deadline.getText().trim())); }
            catch (Exception ex) { JOptionPane.showMessageDialog(d, "Invalid date format!"); return; }
            if (internDAO.add(i)) { d.dispose(); onAdd.run(); }
            else JOptionPane.showMessageDialog(d, "Failed to add internship!");
        });
        cancelBtn.addActionListener(e -> d.dispose());
        d.setContentPane(panel);
        d.setVisible(true);
    }

    private JPanel buildAllApplicationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        String[] cols = {"Student","Company","Role","Status","Applied Date"};
        List<Application> apps = appDAO.getAll();
        Object[][] data = new Object[apps.size()][5];
        for (int i = 0; i < apps.size(); i++) {
            Application a = apps.get(i);
            data[i] = new Object[]{a.getNotes(), a.getCompanyName(), a.getRole(), a.getStatus(),
                a.getAppliedDate() != null ? a.getAppliedDate().toString().substring(0,10) : ""};
        }
        JTable table = UITheme.createStyledTable(cols, data);
        panel.add(UITheme.createScrollPane(table));
        return panel;
    }

    private JPanel buildAllStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BG_DARK);
        String[] cols = {"ID","Name","Email","Branch","Year","Role"};
        List<Student> students = studentDAO.getAllStudents();
        Object[][] data = new Object[students.size()][6];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i] = new Object[]{s.getId(), s.getName(), s.getEmail(), s.getBranch(), s.getYear(), s.getRole()};
        }
        JTable table = UITheme.createStyledTable(cols, data);
        panel.add(UITheme.createScrollPane(table));
        return panel;
    }

    // ===================== PROFILE TAB =====================
    private JPanel buildProfileTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UITheme.BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel title = UITheme.createLabel("👤 My Profile", new Font("Segoe UI", Font.BOLD, 20), UITheme.TEXT_PRIMARY);

        JPanel formCard = UITheme.createCard();
        formCard.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = UITheme.createTextField("Full Name");
        nameField.setText(currentUser.getName() != null ? currentUser.getName() : "");
        JTextField emailField = UITheme.createTextField("Email");
        emailField.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        emailField.setEditable(false);
        emailField.setForeground(UITheme.TEXT_MUTED);
        JTextField branchField = UITheme.createTextField("Branch");
        branchField.setText(currentUser.getBranch() != null ? currentUser.getBranch() : "");
        JTextField phoneField = UITheme.createTextField("Phone");
        phoneField.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");
        JTextField resumeField = UITheme.createTextField("Resume link / Google Drive URL");
        resumeField.setText(currentUser.getResumeLink() != null ? currentUser.getResumeLink() : "");
        JComboBox<String> yearCombo = UITheme.createCombo(new String[]{"Year 1", "Year 2", "Year 3", "Year 4"});
        if (currentUser.getYear() > 0) yearCombo.setSelectedIndex(currentUser.getYear() - 1);

        String[][] fields = {
            {"Full Name","Email (read-only)","Branch","Phone","Year","Resume Link"}
        };
        JComponent[] comps = {nameField, emailField, branchField, phoneField, yearCombo, resumeField};
        String[] labels = {"Full Name", "Email (read-only)", "Branch", "Phone", "Year", "Resume Link"};

        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.3;
            formCard.add(UITheme.createLabel(labels[i], UITheme.FONT_BODY, UITheme.TEXT_SECOND), g);
            g.gridx = 1; g.weightx = 0.7;
            comps[i].setPreferredSize(new Dimension(320, 40));
            formCard.add(comps[i], g);
        }

        JButton saveBtn = UITheme.createButton("Save Changes", UITheme.ACCENT_BLUE);
        saveBtn.setPreferredSize(new Dimension(200, 42));
        g.gridx = 1; g.gridy = labels.length; g.insets = new Insets(20, 8, 8, 8);
        formCard.add(saveBtn, g);

        saveBtn.addActionListener(e -> {
            currentUser.setName(nameField.getText().trim());
            currentUser.setBranch(branchField.getText().trim());
            currentUser.setPhone(phoneField.getText().trim());
            currentUser.setResumeLink(resumeField.getText().trim());
            currentUser.setYear(yearCombo.getSelectedIndex() + 1);
            if (studentDAO.updateProfile(currentUser)) {
                JOptionPane.showMessageDialog(this, "✅ Profile updated successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
                setTitle("InternTrack Pro — " + currentUser.getName());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JScrollPane scroll = UITheme.createScrollPane(formCard);
        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ===================== HELPERS =====================
    private JLabel makeStatusBadge(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_SMALL);
        Color c = UITheme.getStatusColor(text);
        l.setForeground(c);
        l.setOpaque(true);
        l.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue(), 25));
        l.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(c.getRed(), c.getGreen(), c.getBlue(), 80)),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        return l;
    }

    private JLabel makeSkillChip(String skill) {
        JLabel chip = new JLabel(skill);
        chip.setFont(UITheme.FONT_SMALL);
        chip.setForeground(UITheme.ACCENT_CYAN);
        chip.setOpaque(true);
        chip.setBackground(new Color(6, 182, 212, 20));
        chip.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(6, 182, 212, 60)),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        return chip;
    }

    private void showNotifications() {
        JOptionPane.showMessageDialog(this,
            "🔔 Notifications\n\n" +
            "• Check deadlines for active internships!\n" +
            "• New internships matching your skills are available.\n" +
            "• Update your profile for better recommendations.",
            "Notifications", JOptionPane.INFORMATION_MESSAGE);
        unreadNotifs = 0;
        repaint();
    }

    private void startDeadlineChecker() {
        // Check for upcoming deadlines once
        SwingUtilities.invokeLater(() -> {
            List<Internship> list = internDAO.getAll();
            long now = System.currentTimeMillis();
            for (Internship i : list) {
                if (i.getDeadline() != null) {
                    long diff = i.getDeadline().getTime() - now;
                    long days = diff / (1000 * 60 * 60 * 24);
                    if (days >= 0 && days <= 7) unreadNotifs++;
                }
            }
            repaint();
        });
    }

    // Wrap layout for dynamic card grids
    static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }
        @Override public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }
        @Override public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }
        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                if (targetWidth == 0) targetWidth = Integer.MAX_VALUE;
                int hgap = getHgap(), vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);
                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0, rowHeight = 0;
                int nmembers = target.getComponentCount();
                for (int i=0; i<nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0; rowHeight = 0;
                        }
                        if (rowWidth != 0) rowWidth += hgap;
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                addRow(dim, rowWidth, rowHeight);
                dim.width += insets.left + insets.right + hgap * 2;
                dim.height += insets.top + insets.bottom + vgap * 2;
                return dim;
            }
        }
        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);
            if (dim.height > 0) dim.height += getVgap();
            dim.height += rowHeight;
        }
    }
}