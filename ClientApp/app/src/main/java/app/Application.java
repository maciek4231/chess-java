package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Application {

    Game game;
    ChessWebSocketClient client;
    JFrame mainFrame;
    JPanel mainPanel;
    CardLayout mainPanelLayout;
    JPanel menu;
    JPanel loginPanel;

    public Application(ChessWebSocketClient client)
    {
        this.client = client;

        createComps();

        setUpConnection();
    }

    private void setUpConnection() {
        client.connect();
        while (!client.isOpen()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createComps() {
        game = new Game(client);

        mainFrame = new JFrame("illChess");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1536, 1024);

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

        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(menu, BorderLayout.WEST);
        mainFrame.add(mainPanel, BorderLayout.CENTER);

        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                double xScale = e.getComponent().getWidth() / 1152.0;
                double yScale = e.getComponent().getHeight() / 1024.0;
                game.resize(xScale, yScale);
            }
        });

        mainFrame.setVisible(true);
        mainPanelLayout.next(mainPanel);
    }
}
