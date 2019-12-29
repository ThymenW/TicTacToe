package com.example.tictactoe.services.net;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class SessionManager extends WebSocketClient {
    private static SessionManager instance = null;

    private SessionManager(URI serverUri) {
        super(serverUri);
    }

    public static SessionManager getInstance(URI serverUri) {
        Log.d("EVENT_TAG", "getInstance: " + serverUri);
        if (instance == null)
            instance = new SessionManager(serverUri);
        return instance;
    }

    @Override
    public void onOpen(ServerHandshake data) {
        Log.d("EVENT_TAG", "onOpen: " + data.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        Log.d("EVENT_TAG", "onMessage: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d("EVENT_TAG", "onClose: " + reason);

    }

    @Override
    public void onError(Exception ex) {
        Log.d("EVENT_TAG", "onError: " + ex.getMessage());

    }
}