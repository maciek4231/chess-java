package app;

import java.awt.CardLayout;

import javax.swing.JPanel;

public class LoginCard {
    JPanel mainPanel;
    CardLayout layout;
    LoginPanel loginPanel;

    public LoginCard() {
        mainPanel = new JPanel();
        layout = new CardLayout();
        mainPanel.setLayout(layout);

        loginPanel = new LoginPanel();
        mainPanel.add(loginPanel.getPanel(), "login");
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void changeCard(String cardName) {
        layout.show(mainPanel, cardName);
    }
}
