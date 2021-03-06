package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.ai.AI;
import com.example.tictactoe.models.Mode;
import com.example.tictactoe.models.Move;
import com.example.tictactoe.services.net.SessionManager;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SessionManager sessionManager;

    private Mode gameMode;
    private boolean isPlayer1 = false;

    private Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true;
    private int roundCount;
    private int player1Points;
    private int player2Points;
    private TextView textViewPlayer1;
    private TextView textViewPlayer2;
    private TextView player1Dot;
    private TextView player2Dot;
    private ImageView win_line_h_2;
    private ImageView win_line_h_1;
    private ImageView win_line_h_3;
    private ImageView win_line_v_1;
    private ImageView win_line_v_2;
    private ImageView win_line_v_3;
    private ImageView win_line_d_1;
    private ImageView win_line_d_2;
    private long waitForWin = 1000;

    private AI ai;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        gameMode = (Mode) intent.getSerializableExtra("GAME_MODE");
        if (gameMode == Mode.ONLINE) {
            sessionManager = SessionManager.getInstance(URI.create("ws://" + Constants.SERVER_URL + "/session"), move -> {
                if (move.getPlayer() == -1) {
                    isPlayer1 = true;
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "You are player 1", Toast.LENGTH_LONG).show());
                    return;
                } else if (move.getPlayer() == -2) {
                    isPlayer1 = false;
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "You are player 2", Toast.LENGTH_LONG).show());
                    return;
                }
                if (!buttons[move.getX()][move.getY()].getText().equals("")) {
                    sessionManager.send(new Move(-1, -1, -3));
                    return;
                }
                if (move.getPlayer() == 0)
                    buttons[move.getX()][move.getY()].setText("X");
                else
                    buttons[move.getX()][move.getY()].setText("O");
                if (checkForWin())
                    if (player1Turn)
                        player1Wins();
                    else player2Wins();
                player1Turn = !player1Turn;
            });
            if (sessionManager != null) {
                if (sessionManager.isClosed()) {
                    sessionManager.reconnect();
                } else
                    sessionManager.connect();
                sessionManager.setConnectionLostTimeout(0);

            } else {
                Log.d("MAINACTIVITY_TAG", "onCreate: can't connect to server service");
            }
        }
        if (gameMode == Mode.AI)
            ai = new AI(AI.AI_Algorithm.RANDOM);

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);

        win_line_h_2 = findViewById(R.id.win_line_h_2);
        win_line_h_1 = findViewById(R.id.win_line_h_1);
        win_line_h_3 = findViewById(R.id.win_line_h_3);
        win_line_v_1 = findViewById(R.id.win_line_v_1);
        win_line_v_2 = findViewById(R.id.win_line_v_2);
        win_line_v_3 = findViewById(R.id.win_line_v_3);
        win_line_d_1 = findViewById(R.id.win_line_d_1);
        win_line_d_2 = findViewById(R.id.win_line_d_2);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                final int x = i;
                final int y = j;
                switch (gameMode) {
                    case AI:
                        buttons[i][j].setOnClickListener(v -> {
                            aiGameLogic(v, x, y);
                        });
                        break;
                    case ONLINE:
                        buttons[i][j].setOnClickListener(v -> {
                            onlineGameLogic(x, y);
                        });
                        break;
                    case OFFLINE:
                        buttons[i][j].setOnClickListener(this::offlineGameLogic);
                        break;
                }
            }
        }

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(v -> resetGame());

        player1Dot = findViewById(R.id.player1Dot);
        player2Dot = findViewById(R.id.player2Dot);

        player1Dot.setText("------");
    }

    private void offlineGameLogic(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        if (player1Turn) {
            ((Button) v).setText("X");
            player2Dot.setText("-----");
            player1Dot.setText("");
        } else {
            ((Button) v).setText("O");
            player1Dot.setText("-----");
            player2Dot.setText("");

        }

        roundCount++;


        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            player1Turn = !player1Turn;
        }
    }

    private void onlineGameLogic(int x, int y) {
        if (player1Turn && isPlayer1) {
            player2Dot.setText("-----");
            player1Dot.setText("");
            sessionManager.send(new Move(x, y, 0));
        } else if (!isPlayer1 && !player1Turn) {
            sessionManager.send(new Move(x, y, 1));
            player1Dot.setText("-----");
            player2Dot.setText("");
        }

    }

    private void aiGameLogic(View v, int x, int y) {

        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        if (player1Turn) {
            ((Button) v).setText("X");
            roundCount++;
            if (checkForWin()) {
                if (player1Turn) {
                    player1Wins();
                } else {
                    player2Wins();
                }
                return;
            } else if (roundCount == 9) {
                draw();
                return;
            }
            player1Turn = !player1Turn;
            String[][] board = new String[3][3];
            for (int i = 0; i < buttons.length; i++) {
                for (int j = 0; j < buttons[i].length; j++) {
                    board[i][j] = buttons[i][j].getText().toString();
                }
            }
            Move aiMove = ai.predict(board);
            if (aiMove == null) {
                return;
            }
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                buttons[aiMove.getX()][aiMove.getY()].setText("O");

                roundCount++;

                if (checkForWin()) {
                    if (player1Turn) {
                        player1Wins();
                    } else {
                        player2Wins();
                    }
                } else if (roundCount == 9) {
                    draw();
                } else {
                    player1Turn = !player1Turn;
                }
            }, 1000);

        }
    }


    @Override
    public void onClick(View v) {

    }


    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }


        for (int i = 0; i < 3; i++) {
            if (field[0][0].equals(field[0][1])
                    && field[0][0].equals(field[0][2])
                    && !field[0][0].equals("")) {

                win_line_h_1.setVisibility(View.VISIBLE);
                return true;

            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[1][0].equals(field[1][1])
                    && field[1][0].equals(field[1][2])
                    && !field[1][0].equals("")) {

                win_line_h_2.setVisibility(View.VISIBLE);
                return true;

            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[2][0].equals(field[2][1])
                    && field[2][0].equals(field[2][2])
                    && !field[2][0].equals("")) {

                win_line_h_3.setVisibility(View.VISIBLE);
                return true;

            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][0].equals(field[1][0])
                    && field[0][0].equals(field[2][0])
                    && !field[0][0].equals("")) {

                win_line_v_1.setVisibility(View.VISIBLE);
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][1].equals(field[1][1])
                    && field[0][1].equals(field[2][1])
                    && !field[0][1].equals("")) {

                win_line_v_2.setVisibility(View.VISIBLE);
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][2].equals(field[1][2])
                    && field[0][2].equals(field[2][2])
                    && !field[0][2].equals("")) {

                win_line_v_3.setVisibility(View.VISIBLE);
                return true;
            }
        }

        if (field[0][0].equals(field[1][1])
                && field[0][0].equals(field[2][2])
                && !field[0][0].equals("")) {

            win_line_d_1.setVisibility(View.VISIBLE);
            return true;
        }

        if (field[0][2].equals(field[1][1])
                && field[0][2].equals(field[2][0])
                && !field[0][2].equals("")) {

            win_line_d_2.setVisibility(View.VISIBLE);
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (sessionManager != null)
            sessionManager.close();
        sessionManager = null;
    }

    private void player1Wins() {
        player1Points++;
        Toast.makeText(this, "Player 1 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            resetBoard();
        }, waitForWin);

        player1Turn = true;
        player1Dot.setText("");
        player2Dot.setText("-----");
    }

    private void player2Wins() {
        player2Points++;
        Toast.makeText(this, "Player 2 wins!", Toast.LENGTH_SHORT).show();
        updatePointsText();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            resetBoard();
        }, waitForWin);
        player1Turn = true;
        player1Dot.setText("-----");
        player2Dot.setText("");
    }

    private void draw() {
        Toast.makeText(this, "Draw!", Toast.LENGTH_SHORT).show();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            resetBoard();
        }, waitForWin);
        player1Turn = true;
    }

    private void updatePointsText() {
        textViewPlayer1.setText("Player 1: " + player1Points);
        textViewPlayer2.setText("Player 2: " + player2Points);
    }

    private void resetBoard() {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }


        win_line_h_1.setVisibility(View.INVISIBLE);
        win_line_h_2.setVisibility(View.INVISIBLE);
        win_line_h_3.setVisibility(View.INVISIBLE);
        win_line_v_1.setVisibility(View.INVISIBLE);
        win_line_v_2.setVisibility(View.INVISIBLE);
        win_line_v_3.setVisibility(View.INVISIBLE);
        win_line_d_1.setVisibility(View.INVISIBLE);
        win_line_d_2.setVisibility(View.INVISIBLE);
        roundCount = 0;
        player1Turn = true;
    }

    private void resetGame() {
        player1Points = 0;
        player2Points = 0;
        updatePointsText();
        resetBoard();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("roundCount", roundCount);
        outState.putInt("player1Points", player1Points);
        outState.putInt("player2Points", player2Points);
        outState.putBoolean("player1Turn", player1Turn);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        roundCount = savedInstanceState.getInt("roundCount");
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1Turn = savedInstanceState.getBoolean("player1Turn");

    }
}
