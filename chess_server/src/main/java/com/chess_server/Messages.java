// reduntant
package com.chess_server;

public class Messages {

    // public static class Move {
    // int x1;
    // int y1;
    // int x2;
    // int y2;
    // }

    public static class Message {
        String type;
        Integer gameID;
    }

    public static class PickMoveMessage extends Message {
        Integer moveNo;
    }

    public static class AbortMessage extends Message {
        // Add fields specific to abort message
    }

    public static class TakebackMessage extends Message {
        // Add fields specific to takeback message
    }

    public static class AvailabilityMessage extends Message {

    }
}