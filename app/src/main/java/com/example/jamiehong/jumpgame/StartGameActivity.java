package com.example.jamiehong.jumpgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);
    }

    public void StartGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
