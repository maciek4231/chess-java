package app;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

public class ChessWebSocketClient extends WebSocketClient {

	private MessageHandler handler;

	public ChessWebSocketClient(URI serverUri, Draft draft) {
		super(serverUri, draft);
	}

	public ChessWebSocketClient(URI serverURI) {
		super(serverURI);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		System.out.println("new connection opened");
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println("closed with exit code " + code + " additional info: " + reason);
	}

	@Override
	public void onMessage(String message) {
		System.out.println(message);
		handler.handleMessage(message);
	}

	@Override
	public void onMessage(ByteBuffer message) {
		System.out.println("received ByteBuffer");
	}

	@Override
	public void onError(Exception ex) {
		System.err.println("an error occurred:" + ex);
	}

	public void setHandler(MessageHandler handler) {
		this.handler = handler;
	}

	public static void main(String[] args) throws URISyntaxException {
		WebSocketClient client = new ChessWebSocketClient(new URI("ws://localhost:8887"));
		client.connect();
	}
}