package com.example.tictactoe.ai;

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

    public Move predict(String[][] board) {
        if (this.algorithm == AI_Algorithm.RANDOM)
            return this.predictRandom(board);
        else if (this.algorithm == AI_Algorithm.MINIMAX)
            return this.predictMiniMax(board);
        return null;
    }

    @Nullable
    private Move predictRandom(String[][] board) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].equals(""))
                    possibleMoves.add(new Move(i, j, 1));
            }
        }
        if (possibleMoves.size() == 0)
            return null;
        Random random = new Random();
        int index = random.nextInt(possibleMoves.size() - 1);
        return possibleMoves.get(index);
    }


    private Move predictMiniMax(String[][] board) {
        int bestScore = -Integer.MAX_VALUE;
        Move move = null;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals("")) {
                    board[i][j] = "X";
                    int score = minimax(board, 0, false);
                    board[i][j] = "";
                    if (score > bestScore) {
                        bestScore = score;
                        move = new Move(i, j, 1);
                    }
                }
            }
        }
        return move;
    }

    private int minimax(String[][] board, int depth, boolean isMaximizing) {
        if (isMaximizing) {
            int bestScore = -Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].equals("")) {
                        board[i][j] = "O";
                        int score = minimax(board, depth + 1, false);
                        board[i][j] = "";
                        if (score > bestScore)
                            bestScore = score;
                    }
                }
            }
            return bestScore;
        }
        int bestScore = Integer.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals("")) {
                    board[i][j] = "X";
                    int score = minimax(board, depth + 1, true);
                    board[i][j] = "";
                    if (score < bestScore) {

                    }
                }
            }
        }
        return bestScore;
    }

    public enum AI_Algorithm {
        RANDOM,
        MINIMAX
    }
}

