package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Application {

    Game game;
    ChessWebSocketClient client;
    JFrame mainFrame;
    JPanel awaitingConnectionPanel;
    JPanel mainPanel;
    CardLayout mainPanelLayout;
    JPanel menu;
    JPanel loginPanel;

    public Application(ChessWebSocketClient client)
    {
        this.client = client;

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
        game = new Game(client);

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(1152, 1024));
        mainPanelLayout = new CardLayout();
        mainPanel.setLayout(mainPanelLayout);
        mainPanel.add(game.getWindow(), "game");

        menu = new JPanel();
        menu.setPreferredSize(new Dimension(384, 1024));

        loginPanel = new JPanel();
        loginPanel.setPreferredSize(new Dimension(1152, 1024));
        loginPanel.setBackground(Color.RED);
        mainPanel.add(loginPanel, "login");

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                double xScale = e.getComponent().getWidth() / 1152.0;
                double yScale = e.getComponent().getHeight() / 1024.0;
                game.resize(xScale, yScale);
            }
        });

        mainFrame.setVisible(true);
        mainPanelLayout.show(mainPanel, "login");
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
        mainFrame.add(menu, BorderLayout.WEST);
        mainFrame.add(mainPanel, BorderLayout.CENTER);
        mainFrame.remove(awaitingConnectionPanel);
    }

    private void loadImages() {
        IconLoader.getMoveIcon();
    }
}
