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

    ZonedDateTime timeStamp = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("UTC"));
    boolean running = false;

    JLabel label;

    public Clock(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;

        label = new JLabel("00:00:00", SwingConstants.CENTER);
        label.setBounds((int) (xPos * xScale), (int) (yPos * yScale), (int) (128 * xScale), (int) (64 * yScale));
        setVisible(false);
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
        long hours = Math.abs(ChronoUnit.HOURS.between(timeStamp, ZonedDateTime.now(ZoneId.of("UTC"))));
        long minutes = Math.abs(ChronoUnit.MINUTES.between(timeStamp, ZonedDateTime.now(ZoneId.of("UTC"))) % 60);
        long seconds = Math.abs(ChronoUnit.SECONDS.between(timeStamp, ZonedDateTime.now(ZoneId.of("UTC"))) % 60);
        String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        if (hours == 0 && minutes == 0 && seconds < 10)
            label.setForeground(java.awt.Color.RED);
        else
            label.setForeground(java.awt.Color.BLACK);

        label.setText(time);
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
