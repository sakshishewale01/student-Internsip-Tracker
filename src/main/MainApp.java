package main;

import java.awt.*;
import javax.swing.*;
import ui.LoginScreen;
import ui.UITheme;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UITheme.applyGlobalTheme();
            showSplashScreen();
        });
    }

    private static void showSplashScreen() {
        JWindow splash = new JWindow();
        JPanel panel = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background gradient
                GradientPaint gp = new GradientPaint(0, 0, new Color(10,14,26), getWidth(), getHeight(), new Color(30,10,60));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative elements
                g2.setColor(new Color(59, 130, 246, 30));
                g2.fillOval(-80, -80, 300, 300);
                g2.setColor(new Color(139, 92, 246, 20));
                g2.fillOval(getWidth()-150, getHeight()-150, 280, 280);
                // Logo
                g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
                g2.setColor(Color.WHITE);
                g2.drawString("🎯", getWidth()/2 - 40, getHeight()/2 - 40);
                // Title
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                GradientPaint textGp = new GradientPaint(0, 0, new Color(59,130,246), getWidth(), 0, new Color(139,92,246));
                g2.setPaint(textGp);
                String title = "InternTrack Pro";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(title, (getWidth()-fm.stringWidth(title))/2, getHeight()/2 + 20);
                // Subtitle
                g2.setColor(new Color(148,163,184));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                String sub = "Student Internship Management System";
                fm = g2.getFontMetrics();
                g2.drawString(sub, (getWidth()-fm.stringWidth(sub))/2, getHeight()/2 + 50);
                // Loading text
                g2.setColor(new Color(100,116,139));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                String loading = "Loading...";
                fm = g2.getFontMetrics();
                g2.drawString(loading, (getWidth()-fm.stringWidth(loading))/2, getHeight() - 30);
            }
        };
        panel.setPreferredSize(new Dimension(480, 320));
        splash.setContentPane(panel);
        splash.pack();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);

        Timer timer = new Timer(2000, e -> {
            splash.dispose();
            new LoginScreen();
        });
        timer.setRepeats(false);
        timer.start();
    }
}