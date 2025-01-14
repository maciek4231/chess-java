package app;

import java.awt.CardLayout;

import javax.swing.JPanel;

public class LoginCard {
    JPanel mainPanel;
    CardLayout layout;
    LoginPanel loginPanel;
    RegisterPanel registerPanel;

    public LoginCard(Application application, MessageHandler messageHandler) {
        mainPanel = new JPanel();
        layout = new CardLayout();
        mainPanel.setLayout(layout);

        loginPanel = new LoginPanel(this, messageHandler);
        mainPanel.add(loginPanel.getPanel(), "login");
        registerPanel = new RegisterPanel(this, messageHandler);
        mainPanel.add(registerPanel.getPanel(), "register");
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void changeCard(String cardName) {
        layout.show(mainPanel, cardName);
    }
}
