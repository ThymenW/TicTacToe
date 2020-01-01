package com.example.tictactoe;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tictactoe.models.Move;

import java.util.ArrayList;
import java.util.Random;

public class AI {
    private final String TAG = "AI_TAG";
    private AI_Algorithm algorithm;

    public AI(AI_Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public Move predict(ArrayList<Move> moves) {
        if (this.algorithm == AI_Algorithm.RANDOM)
            return this.predictRandom(moves);
        else
            return this.predictMiniMax(moves);
    }

    @Nullable
    private Move predictRandom(ArrayList<Move> moves) {
        Log.d(TAG, "predictRandom: " + moves.size());
        if (moves.size() == 0)
            return null;
        Random random = new Random();
        int index = random.nextInt(moves.size() - 1);
        return moves.get(index);
    }

    private Move predictMiniMax(ArrayList<Move> moves) {
        return new Move(0, 0, 1);
    }

    public enum AI_Algorithm {
        RANDOM,
        MINIMAX
    }
}

