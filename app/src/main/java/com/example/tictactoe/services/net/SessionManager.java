package com.example.tictactoe.services.net;

import android.util.Log;

import com.example.tictactoe.models.Move;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;

import java.net.URI;

public class SessionManager extends WebSocketClient {
    private static SessionManager instance = null;
    private EventCallback callback;

    private SessionManager(URI serverUri, EventCallback callback) {
        super(serverUri);
    }

    public static SessionManager getInstance(URI serverUri, EventCallback callback) {
        Log.d("EVENT_TAG", "getInstance: " + serverUri);
        if (instance == null)
            instance = new SessionManager(serverUri, callback);
        return instance;
    }

    public void send(Move move) {
        String data = move.toJSONString();
        this.send(data);
    }

    @Override
    public void onOpen(ServerHandshake data) {
        Log.d("EVENT_TAG", "onOpen: " + data.getHttpStatusMessage());
    }

    @Override
    public void onMessage(String message) {
        Log.d("EVENT_TAG", "onMessage: " + message);
        try {
            Move m = Move.fromJSON(message);
            callback.onMove(m);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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