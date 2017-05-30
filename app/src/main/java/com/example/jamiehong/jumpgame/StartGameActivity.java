package com.example.jamiehong.jumpgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartGameActivity extends AppCompatActivity {

    // Method created on creation of class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);
    }

    // Creates an Intent
    // Start the MainActivity, which begins the game
    public void StartGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
