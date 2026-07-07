package ui;

import java.awt.*;
import javax.swing.*;

public class UITheme {
    // ===== COLOR PALETTE =====
    public static final Color BG_DARK       = new Color(10, 14, 26);
    public static final Color BG_CARD       = new Color(18, 24, 42);
    public static final Color BG_SIDEBAR    = new Color(14, 19, 35);
    public static final Color ACCENT_BLUE   = new Color(59, 130, 246);
    public static final Color ACCENT_PURPLE = new Color(139, 92, 246);
    public static final Color ACCENT_CYAN   = new Color(6, 182, 212);
    public static final Color ACCENT_GREEN  = new Color(16, 185, 129);
    public static final Color ACCENT_ORANGE = new Color(245, 158, 11);
    public static final Color ACCENT_RED    = new Color(239, 68, 68);
    public static final Color ACCENT_PINK   = new Color(236, 72, 153);
    public static final Color TEXT_PRIMARY  = new Color(241, 245, 249);
    public static final Color TEXT_SECOND   = new Color(148, 163, 184);
    public static final Color TEXT_MUTED    = new Color(71, 85, 105);
    public static final Color BORDER_COLOR  = new Color(30, 41, 59);
    public static final Color HOVER_BG      = new Color(30, 41, 59);

    // Status colors
    public static final Color STATUS_APPLIED     = new Color(59, 130, 246);
    public static final Color STATUS_SHORTLISTED = new Color(245, 158, 11);
    public static final Color STATUS_SELECTED    = new Color(16, 185, 129);
    public static final Color STATUS_REJECTED    = new Color(239, 68, 68);

    // ===== FONTS =====
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEAD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BOLD    = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO    = new Font("Consolas", Font.PLAIN, 12);

    // ===== COMPONENT FACTORIES =====

    public static JPanel createCard() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        return p;
    }

    public static JLabel createLabel(String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        return l;
    }

    public static JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_MUTED);
                    g2.setFont(FONT_BODY);
                    g2.drawString(placeholder, 10, getHeight() / 2 + 5);
                }
            }
        };
        styleTextField(tf);
        return tf;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        styleTextField(pf);
        return pf;
    }

    private static void styleTextField(JTextField tf) {
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(new Color(22, 30, 50));
        tf.setCaretColor(ACCENT_BLUE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        tf.setPreferredSize(new Dimension(200, 40));
    }

    public static JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            { addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(java.awt.event.MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = hovered ? bg.brighter() : bg;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(bg);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
        return btn;
    }

    public static JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            { addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(java.awt.event.MouseEvent e) { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) { g2.setColor(HOVER_BG); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); }
                g2.setColor(BORDER_COLOR);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                g2.setColor(TEXT_PRIMARY);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(FONT_BODY);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JTable createStyledTable(String[] columns, Object[][] data) {
        JTable table = new JTable(data, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table.setBackground(BG_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(59, 130, 246, 60));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setBackground(BG_SIDEBAR);
        table.getTableHeader().setForeground(TEXT_SECOND);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        table.setFillsViewportHeight(true);
        return table;
    }

    public static JScrollPane createScrollPane(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_CARD);
        sp.setBackground(BG_CARD);
        sp.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(59, 130, 246, 120);
                trackColor = BG_DARK;
            }
        });
        return sp;
    }

    public static JComboBox<String> createCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(new Color(22, 30, 50));
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return cb;
    }

    public static Color getStatusColor(String status) {
        if (status == null) return TEXT_MUTED;
        return switch (status.toUpperCase()) {
            case "APPLIED"     -> STATUS_APPLIED;
            case "SHORTLISTED" -> STATUS_SHORTLISTED;
            case "SELECTED"    -> STATUS_SELECTED;
            case "REJECTED"    -> STATUS_REJECTED;
            default -> TEXT_MUTED;
        };
    }

    public static void applyGlobalTheme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("Panel.background", BG_DARK);
        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Button.background", ACCENT_BLUE);
        UIManager.put("Button.foreground", TEXT_PRIMARY);
        UIManager.put("TextField.background", new Color(22, 30, 50));
        UIManager.put("TextField.foreground", TEXT_PRIMARY);
        UIManager.put("TextArea.background", new Color(22, 30, 50));
        UIManager.put("TextArea.foreground", TEXT_PRIMARY);
        UIManager.put("ComboBox.background", new Color(22, 30, 50));
        UIManager.put("ComboBox.foreground", TEXT_PRIMARY);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("ScrollPane.background", BG_CARD);
    }
}