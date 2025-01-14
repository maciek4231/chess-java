package app;

import java.awt.CardLayout;

import javax.swing.JPanel;

public class LoginCard {
    JPanel mainPanel;
    CardLayout layout;
    LoginPanel loginPanel;
    RegisterPanel registerPanel;
    ProfilePanel profilePanel;

    MessageHandler messageHandler;

    public LoginCard(Application application, MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        mainPanel = new JPanel();
        layout = new CardLayout();
        mainPanel.setLayout(layout);

        loginPanel = new LoginPanel(this, messageHandler);
        mainPanel.add(loginPanel.getPanel(), "login");
        registerPanel = new RegisterPanel(this, messageHandler);
        mainPanel.add(registerPanel.getPanel(), "register");
        profilePanel = new ProfilePanel(this, messageHandler);
        mainPanel.add(profilePanel.getPanel(), "profile");

    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void changeCard(String cardName) {
        layout.show(mainPanel, cardName);
        if (cardName.equals("profile")) {
            messageHandler.askForMyStats();
        }
    }

    public void logIn(String username) {
        profilePanel.setUsername(username);
        changeCard("profile");
        mainPanel.remove(loginPanel.getPanel());
        loginPanel = new LoginPanel(this, messageHandler);
        mainPanel.add(loginPanel.getPanel(), "login");
        mainPanel.remove(registerPanel.getPanel());
        registerPanel = new RegisterPanel(this, messageHandler);
        mainPanel.add(registerPanel.getPanel(), "register");
    }

    public void logOut() {
        changeCard("login");
    }

    public void showLoginError(String message) {
        loginPanel.setResponseError(message);
    }

    public void showSignupError(String message) {
        registerPanel.setResponseError(message);
    }
}
