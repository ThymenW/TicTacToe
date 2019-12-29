package com.example.tictactoe.services.net;

import com.example.tictactoe.models.Move;

public interface EventCallback {
    void onMove(Move move);
}
