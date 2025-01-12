package app;

import java.awt.Font;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Clock {
    static double xScale = 1;
    static double yScale = 1;
    int xPos;
    int yPos;

    ZonedDateTime timeStamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
    boolean running = false;

    JLabel label;

    public Clock(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;

        label = new JLabel("00:00:00", SwingConstants.CENTER);
        label.setBounds((int) (xPos * xScale), (int) (yPos * yScale), (int) (128 * xScale), (int) (64 * yScale));
        setVisible(true); // TODO: turn this to false later
        correctFontSize();
    }

    public static void staticResize(double xScale, double yScale) {
        Clock.xScale = xScale;
        Clock.yScale = yScale;
    }

    public void resize(double xScale, double yScale) {
        label.setBounds((int) (xPos * xScale), (int) (yPos * yScale), (int) (128 * xScale), (int) (64 * yScale));
        correctFontSize();
    }

    public JLabel getLabel() {
        return label;
    }

    public void move(int x, int y) {
        xPos = x;
        yPos = y;
        label.setBounds((int) (xPos * xScale), (int) (yPos * yScale), (int) (128 * xScale), (int) (64 * yScale));
    }

    void correctFontSize()
    {
        label.setFont(new Font("Dialog.bold", Font.PLAIN, getCorrectFontSize()));
    }

    int getCorrectFontSize()
    {
        if (xScale > 0.953)
            return 23;
        else if (xScale > 0.781)
            return 19;
        else if (xScale > 0.609)
            return 15;
        else if (xScale > 0.452)
            return 11;
        else
            return 7;
    }

    void textUpdate()
    {
        ZonedDateTime period = timeStamp.minus(ZonedDateTime.now().toEpochSecond(), ChronoUnit.SECONDS);
        label.setText(period.toString().substring(11, 19));
    }

    public void update()
    {
        if (running)
            textUpdate();
    }

    public void updateTimeStamp(ZonedDateTime newTime)
    {
        timeStamp = newTime;
        textUpdate();
    }

    public void setRunning(boolean value)
    {
        running = value;
    }

    public void setVisible(boolean value)
    {
        label.setVisible(value);
    }
}
