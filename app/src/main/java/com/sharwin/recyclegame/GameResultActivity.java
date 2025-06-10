package com.sharwin.recyclegame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameResultActivity extends AppCompatActivity {

    TextView finalScoreText;
    Button btnContinueToQuiz;
    MediaPlayer resultPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        finalScoreText = findViewById(R.id.finalScoreText);
        btnContinueToQuiz = findViewById(R.id.btnContinueToQuiz);

        // Animations
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.score_bounce);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        finalScoreText.startAnimation(scaleAnim);
        finalScoreText.startAnimation(bounce);
        btnContinueToQuiz.startAnimation(fadeIn);

        // Get score and display
        int gameScore = getIntent().getIntExtra("gameScore", 0);
        finalScoreText.setText("Your Score: " + gameScore);

        // Play sound based on score
        if (gameScore >= 40) {
            resultPlayer = MediaPlayer.create(this, R.raw.clap);
        } else {
            resultPlayer = MediaPlayer.create(this, R.raw.sad);
        }
        resultPlayer.setVolume(1.0f, 1.0f);
        resultPlayer.start();

        btnContinueToQuiz.setOnClickListener(view -> {
            if (resultPlayer != null) {
                resultPlayer.stop();
                resultPlayer.release();
                resultPlayer = null;
            }
            Intent intent = new Intent(GameResultActivity.this, DoDontActivity.class);
            intent.putExtra("finalScore", gameScore);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (resultPlayer != null) {
            resultPlayer.release();
            resultPlayer = null;
        }
    }
}
