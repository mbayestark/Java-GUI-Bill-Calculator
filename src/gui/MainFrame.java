package gui;

import service.AuthService;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private AdminPanel adminPanel;
    private ClientPanel clientPanel;

    public MainFrame() {
        setTitle("Electricity Bill Estimator");
        setSize(960, 640);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        AuthService authService = new AuthService();
        authService.seedAdminIfNeeded();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        adminPanel = new AdminPanel(this);
        clientPanel = new ClientPanel(this);

        mainPanel.add(loginPanel, "login");
        mainPanel.add(adminPanel, "admin");
        mainPanel.add(clientPanel, "client");

        add(mainPanel);
        showLogin();
        setVisible(true);
    }

    public void showLogin() {
        loginPanel.reset();
        cardLayout.show(mainPanel, "login");
    }

    public void showAdmin() {
        adminPanel.refresh();
        cardLayout.show(mainPanel, "admin");
    }

    public void showClient() {
        clientPanel.refresh();
        cardLayout.show(mainPanel, "client");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });
    }
}
