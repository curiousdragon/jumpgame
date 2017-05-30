package com.example.jamiehong.jumpgame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {
    // activity for ending screen of game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the view
        setContentView(R.layout.activity_game_over);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);
    }

    public void restartGame(View view) {
        // start playing the game again
        // create an Intent that starts MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void returnToHome(View view) {
        // return to main menu: StartGameActivity
        // create an Intent that starts StartGameActivity
        Intent intent = new Intent(this, StartGameActivity.class);
        startActivity(intent);
    }
}
