package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Application {

    Game game;
    ChessWebSocketClient client;
    JFrame mainFrame;
    JPanel awaitingConnectionPanel;
    JPanel mainPanel;
    JPanel gamePanel;
    CardLayout mainPanelLayout;
    JPanel menu;
    JPanel loginPanel;
    MessageHandler messageHandler;
    LoginCard loginCard;
    StatsCard statsCard;
    LeaderboardCard leaderboardCard;

    boolean loggedIn = false;
    String username = "";

    public Application(ChessWebSocketClient client)
    {
        this.client = client;
        messageHandler = new MessageHandler(this, client);
        client.setHandler(messageHandler);

        mainFrame = new JFrame("illChess");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1536, 1024);
        mainFrame.setLayout(new BorderLayout());

        setUpConnection();
        showAwaitingConnection();
        loadImages();

        createComps();
        waitForConnection();

        showGame();
    }

    private void setUpConnection() {
        client.connect();
    }

    private void waitForConnection() {
        while (!client.isOpen()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createComps() {
        game = new Game(this, messageHandler);
        messageHandler.setGame(game);

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(1152, 1024));
        mainPanelLayout = new CardLayout();
        mainPanel.setLayout(mainPanelLayout);

        gamePanel = new JPanel();
        gamePanel.setPreferredSize(new Dimension(1152, 1024));
        gamePanel.setLayout(new BorderLayout());
        gamePanel.add(game.getWindow(), BorderLayout.CENTER);
        mainPanel.add(gamePanel, "game");

        menu = new JPanel();
        menu.setPreferredSize(new Dimension(384, 1024));
        menu.setBackground(Color.LIGHT_GRAY);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        menu.add(new MenuButton(this, "Play", "game"));
        menu.add(new MenuButton(this, "Leaderboard", "leaderboard"));
        menu.add(new MenuButton(this, "Statistics", "statistics"));
        menu.add(Box.createVerticalGlue());
        menu.add(new MenuButton(this, "Account", "login"));

        loginPanel = new JPanel();
        loginPanel.setPreferredSize(new Dimension(1152, 1024));
        loginPanel.setLayout(new BorderLayout());
        loginCard = new LoginCard(this, messageHandler);
        loginPanel.add(loginCard.getMainPanel(), BorderLayout.CENTER);
        mainPanel.add(loginPanel, "login");

        statsCard = new StatsCard(this, messageHandler);
        mainPanel.add(statsCard.getPanel(), "statistics");

        leaderboardCard = new LeaderboardCard(this, messageHandler);
        mainPanel.add(leaderboardCard.getPanel(), "leaderboard");

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                double xScale = e.getComponent().getWidth() / 1152.0;
                double yScale = e.getComponent().getHeight() / 1024.0;
                game.resize(xScale, yScale);
            }
        });
        mainPanelLayout.show(mainPanel, "login");

        menu.setVisible(false);
        mainPanel.setVisible(false);

        mainFrame.add(menu, BorderLayout.WEST);
        mainFrame.add(mainPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    private void showAwaitingConnection()
    {
        awaitingConnectionPanel = new JPanel();
        awaitingConnectionPanel.setPreferredSize(new Dimension(1536, 1024));
        awaitingConnectionPanel.setLayout(new BorderLayout());
        JLabel label = new JLabel("Awaiting connection...");
        label.setFont(label.getFont().deriveFont(64.0f));
        awaitingConnectionPanel.add(label, BorderLayout.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        mainFrame.add(awaitingConnectionPanel, BorderLayout.CENTER);

        mainFrame.setVisible(true);
    }

    private void showGame() {
        menu.setVisible(true);
        mainPanel.setVisible(true);
        awaitingConnectionPanel.setVisible(false);
        mainFrame.remove(awaitingConnectionPanel);
        mainFrame.repaint();
    }

    private void loadImages() {
        IconLoader.getMoveIcon();
        ImageLoader.getMenuButtonIcon("game");
    }

    public void changeCard(String cardName) {
        mainPanelLayout.show(mainPanel, cardName);
    }

    public void resetGame() { // TODO: also reset game when the opponent leaves. (MAYBE NOT?!)
        messageHandler.abandonGame();
        game = new Game(this, messageHandler);
        messageHandler.setGame(game);
        gamePanel.removeAll();
        gamePanel.add(game.getWindow(), BorderLayout.CENTER);
        game.resize(gamePanel.getSize().getWidth() / 1152.0, gamePanel.getSize().getHeight() / 1024.0);
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public void logIn(String username) {
        loggedIn = true;
        this.username = username;
        loginCard.logIn(username);
        game.logIn();
    }

    public void logOut() {
        loggedIn = false;
        username = "";
        loginCard.logOut();
        resetGame();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void showLoginError(String message) {
        loginCard.showLoginError(message);
    }

    public void showSignupError(String message) {
        loginCard.showSignupError(message);
    }
}
