package com.sharwin.recyclegame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    MediaPlayer resultPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView scoreDisplay = findViewById(R.id.finalScoreText);
        int score = getIntent().getIntExtra("totalScore", 0);
        scoreDisplay.setText("Your Score: " + score);

        // Play different sounds based on score
        if (score >= 150) {
            resultPlayer = MediaPlayer.create(this, R.raw.clap);
        } else {
            resultPlayer = MediaPlayer.create(this, R.raw.sad);
        }

        resultPlayer.setVolume(1.0f, 1.0f); // Optional: adjust volume
        resultPlayer.start();

        Button playAgainBtn = findViewById(R.id.playAgainButton);
        Button exitBtn = findViewById(R.id.exitButton);

        playAgainBtn.setOnClickListener(v -> {
            if (resultPlayer != null) resultPlayer.stop();
            startActivity(new Intent(ResultActivity.this, MainActivity.class));
        });

        exitBtn.setOnClickListener(v -> {
            if (resultPlayer != null) resultPlayer.stop();
            finishAffinity();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resultPlayer != null) {
            resultPlayer.release();
        }
    }
}
