package com.example.tictactoe.ai;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.tictactoe.models.Move;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class AI {
    private final String TAG = "AI_TAG";
    private Map<String, Integer> scores = new HashMap<String, Integer>() {
    };
    private AI_Algorithm algorithm;

    public AI(AI_Algorithm algorithm) {
        this.algorithm = algorithm;
        if (algorithm == AI_Algorithm.MINIMAX) {
            scores.put("X", 10);
            scores.put("O", -10);
            scores.put("tie", 0);
        }
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
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
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
        Log.d(TAG, "predictMiniMax: " + bestScore);
        return move;
    }

    private String boardToString(String[][] board) {
        String str = "[";
        for (int i = 0; i < board.length; i++) {
            str += "[";
            for (int j = 0; j < board[i].length; j++) {
                str += "[" + board[i][j] + "]";
            }
            str += "]";
        }
        str += "]";
        return str;
    }

    private int minimax(String[][] board, int depth, boolean isMaximizing) {
        String win = checkWin(board);
        if (!win.equals(""))
            return scores.get(win);

        if (isMaximizing) {
            int bestScore = -Integer.MAX_VALUE;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j].equals("")) {
                        board[i][j] = "X";
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
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].equals("")) {
                    board[i][j] = "O";
                    int score = minimax(board, depth + 1, true);
                    board[i][j] = "";
                    if (score < bestScore) {
                        bestScore = score;
                    }
                }
            }
        }
        return bestScore;
    }

    private String checkWin(String[][] board) {
        String winner = "tie";
        for (int i = 0; i < board.length; i++) {
            //Horizontal
            if (board[i][0].equals(board[i][1])
                    && board[i][1].equals(board[i][2])
                    && !board[i][0].equals("")) {
                winner = board[i][0];
            }

            //Vertical
            if (board[0][i].equals(board[1][i])
                    && board[1][i].equals(board[2][i])
                    && !board[0][i].equals("")) {
                winner = board[0][i];
            }

            //Diagonal
            if (board[0][0].equals(board[1][1])
                    && board[1][1].equals(board[2][2])
                    && !board[0][0].equals("")) {
                winner = board[0][0];
            }

            if (board[2][0].equals(board[1][1])
                    && board[1][1].equals(board[0][2])
                    && !board[2][0].equals("")) {
                winner = board[2][0];
            }
        }
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j].equals(""))
                    return "";
            }
        }
        return winner;
    }
    public enum AI_Algorithm {
        RANDOM,
        MINIMAX
    }
}

