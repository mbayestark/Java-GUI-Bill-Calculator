package gui;

import model.Session;
import model.User;
import service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel implements ActionListener {

    private MainFrame frame;
    private AuthService authService;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    public LoginPanel(MainFrame frame) {
        this.frame = frame;
        this.authService = new AuthService();
        setLayout(new GridBagLayout());
        setBackground(Style.BG);
        add(createFormCard());
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Style.BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Style.BORDER),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));

        addTitle(card);
        addFields(card);
        addButtons(card);
        addMessage(card);
        return card;
    }

    private void addTitle(JPanel card) {
        JLabel title = new JLabel("Electricity Bill Estimator");
        title.setFont(Style.TITLE);
        title.setForeground(Style.TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(30));
    }

    private void addFields(JPanel card) {
        usernameField = Style.createTextField();
        passwordField = Style.createPasswordField();

        card.add(Style.createLabel("Username"));
        card.add(Box.createVerticalStrut(6));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(Style.createLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(20));
    }

    private void addButtons(JPanel card) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        row.setBackground(Style.BG);

        JButton loginBtn = Style.createButton("Login");
        loginBtn.setActionCommand("login");
        loginBtn.addActionListener(this);

        JButton registerBtn = Style.createOutlineButton("Register");
        registerBtn.setActionCommand("register");
        registerBtn.addActionListener(this);

        row.add(loginBtn);
        row.add(registerBtn);
        card.add(row);
    }

    private void addMessage(JPanel card) {
        card.add(Box.createVerticalStrut(16));
        messageLabel = new JLabel(" ");
        messageLabel.setFont(Style.SMALL);
        messageLabel.setForeground(Style.DANGER);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(messageLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("login".equals(cmd)) {
            handleLogin();
        } else if ("register".equals(cmd)) {
            handleRegister();
        }
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        User user = authService.login(username, password);
        if (user == null) {
            messageLabel.setForeground(Style.DANGER);
            messageLabel.setText("Invalid username or password.");
            return;
        }
        Session.getInstance().setCurrentUser(user);
        messageLabel.setText(" ");

        if (user.isAdmin()) {
            frame.showAdmin();
        } else {
            frame.showClient();
        }
    }

    private void handleRegister() {
        String[] types = {"individual", "school", "hospital"};
        String clientType = (String) JOptionPane.showInputDialog(this,
                "Select client type:", "Client Type",
                JOptionPane.PLAIN_MESSAGE, null, types, types[0]);
        if (clientType == null) {
            return;
        }

        String[] labels = getLabelsForType(clientType);
        JTextField[] fields = Style.showFormDialog(this, "Register", labels);
        if (fields == null) {
            return;
        }

        String password = fields[2].getText().trim();
        if (password.length() < 6) {
            messageLabel.setForeground(Style.DANGER);
            messageLabel.setText("Password must be at least 6 characters.");
            return;
        }

        registerUser(fields, clientType);
    }

    private String[] getLabelsForType(String clientType) {
        if ("school".equals(clientType)) {
            return new String[]{"Establishment Name:", "Username:",
                    "Password (min 6):", "Number of Classrooms:"};
        }
        if ("hospital".equals(clientType)) {
            return new String[]{"Establishment Name:", "Username:",
                    "Password (min 6):", "Number of Beds:"};
        }
        return new String[]{"Full Name:", "Username:", "Password (min 6):"};
    }

    private void registerUser(JTextField[] fields, String clientType) {
        String fullName = fields[0].getText().trim();
        String username = fields[1].getText().trim();
        String password = fields[2].getText().trim();
        int capacity = parseCapacity(fields, clientType);

        User user = authService.register(username, password, fullName,
                "client", clientType, capacity);
        if (user != null) {
            messageLabel.setForeground(Style.SUCCESS);
            messageLabel.setText("Registered! You can now log in.");
        } else {
            messageLabel.setForeground(Style.DANGER);
            messageLabel.setText(authService.getLastError());
        }
    }

    private int parseCapacity(JTextField[] fields, String clientType) {
        if ("individual".equals(clientType)) {
            return 1;
        }
        try {
            return Integer.parseInt(fields[3].getText().trim());
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText(" ");
        messageLabel.setForeground(Style.DANGER);
    }
}
