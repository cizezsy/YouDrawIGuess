package me.cizezsy.yourdrawiguess.net;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class MyWebScoketCilent extends WebSocketClient {


    public MyWebScoketCilent(URI serverURI) {
        super(serverURI);
    }

    public MyWebScoketCilent(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public MyWebScoketCilent(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout) {
        super(serverUri, draft, headers, connecttimeout);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
