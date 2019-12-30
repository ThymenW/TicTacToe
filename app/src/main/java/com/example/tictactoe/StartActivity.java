package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tictactoe.models.Mode;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        Button offline = findViewById(R.id.offline_new_game);
        Button online = findViewById(R.id.online_new_game);
        Button ai = findViewById(R.id.ai_new_game);

        offline.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("GAME_MODE", Mode.OFFLINE);
            startActivity(intent);
        });
        online.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("GAME_MODE", Mode.ONLINE);
            startActivity(intent);
        });
        ai.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("GAME_MODE", Mode.AI);
            startActivity(intent);
        });
    }
}
