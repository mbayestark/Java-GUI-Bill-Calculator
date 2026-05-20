package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionListener;

public class Style {

    public static final Color BG = new Color(255, 255, 255);
    public static final Color SIDEBAR = new Color(248, 249, 250);
    public static final Color ACCENT = new Color(67, 97, 238);
    public static final Color TEXT = new Color(26, 26, 46);
    public static final Color TEXT_LIGHT = new Color(107, 114, 128);
    public static final Color BORDER = new Color(229, 231, 235);
    public static final Color DANGER = new Color(239, 68, 68);
    public static final Color SUCCESS = new Color(16, 185, 129);

    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font HEADING = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font SMALL = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font MONO = new Font("Monospaced", Font.PLAIN, 13);

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BODY);
        button.setBackground(ACCENT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    public static JButton createOutlineButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BODY);
        button.setBackground(BG);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 15, 7, 15)));
        return button;
    }

    public static JButton createDangerButton(String text) {
        JButton button = createButton(text);
        button.setBackground(DANGER);
        return button;
    }

    public static JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFont(BODY);
        button.setBackground(SIDEBAR);
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return button;
    }

    public static JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return field;
    }

    public static JComboBox<String> createComboBox(String[] options) {
        JComboBox<String> combo = new JComboBox<>(options);
        combo.setFont(BODY);
        combo.setBackground(BG);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        return combo;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createHeading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADING);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(SMALL);
        label.setForeground(DANGER);
        return label;
    }

    public static void showInlineError(JLabel label, String message) {
        label.setText(message);
    }

    public static void clearError(JLabel label) {
        label.setText(" ");
    }

    public static DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static JScrollPane createScrollTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        styleTable(table);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        JViewport viewport = scroll.getViewport();
        viewport.setBackground(BG);
        return scroll;
    }

    private static void styleTable(JTable table) {
        table.setFont(BODY);
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.setIntercellSpacing(new Dimension(0, 1));
        JTableHeader header = table.getTableHeader();
        header.setFont(SMALL);
        header.setBackground(SIDEBAR);
        header.setForeground(TEXT_LIGHT);
    }

    public static JTable getTableFromScroll(JScrollPane scroll) {
        JViewport viewport = scroll.getViewport();
        return (JTable) viewport.getView();
    }

    public static JPanel createViewPanel(String title) {
        JPanel view = new JPanel(new BorderLayout(0, 15));
        view.setBackground(BG);
        view.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        JLabel heading = createHeading(title);
        view.add(heading, BorderLayout.NORTH);
        return view;
    }

    public static JPanel createButtonRow(String[] labels, String[] commands,
                                         ActionListener listener) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row.setBackground(BG);
        row.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        for (int i = 0; i < labels.length; i++) {
            JButton button;
            if ("Delete".equals(labels[i])) {
                button = createDangerButton(labels[i]);
            } else {
                button = createButton(labels[i]);
            }
            button.setActionCommand(commands[i]);
            button.addActionListener(listener);
            row.add(button);
        }
        return row;
    }

    public static JTextField[] showFormDialog(Component parent,
                                              String title, String[] labels) {
        return showFormDialog(parent, title, labels, null);
    }

    public static JTextField[] showFormDialog(Component parent, String title,
                                              String[] labels, String[] defaults) {
        JTextField[] fields = new JTextField[labels.length];
        JPanel form = new JPanel(new BorderLayout(0, 8));

        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 8));
        grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        for (int i = 0; i < labels.length; i++) {
            fields[i] = createTextField();
            if (defaults != null && i < defaults.length) {
                fields[i].setText(defaults[i]);
            }
            grid.add(createLabel(labels[i]));
            grid.add(fields[i]);
        }

        JLabel errorLabel = createErrorLabel();
        errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

        form.add(grid, BorderLayout.CENTER);
        form.add(errorLabel, BorderLayout.SOUTH);

        while (true) {
            int result = JOptionPane.showConfirmDialog(parent, form, title,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return null;
            }
            String emptyField = findEmptyField(fields, labels);
            if (emptyField != null) {
                errorLabel.setText(emptyField + " cannot be empty.");
                continue;
            }
            return fields;
        }
    }

    private static String findEmptyField(JTextField[] fields, String[] labels) {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i].getText().trim().isEmpty()) {
                String label = labels[i];
                if (label.endsWith(":")) {
                    label = label.substring(0, label.length() - 1);
                }
                return label;
            }
        }
        return null;
    }
}
