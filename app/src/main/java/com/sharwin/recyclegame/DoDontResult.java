package com.sharwin.recyclegame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DoDontResult extends AppCompatActivity {

    private TextView scoreSummary;
    private TextView summaryText;
    private Button btnFinish;

    TextView finalScoreText;
    Button btnContinueToQuiz;

    MediaPlayer resultPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodont_result);

        finalScoreText = findViewById(R.id.scoreSummary);
        btnContinueToQuiz = findViewById(R.id.btnFinish);

        // Load animations
        Animation bounce = AnimationUtils.loadAnimation(this, R.anim.score_bounce);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        finalScoreText.startAnimation(scaleAnim);
        finalScoreText.startAnimation(bounce);
        btnContinueToQuiz.startAnimation(fadeIn);

        // Initialize UI elements
        scoreSummary = findViewById(R.id.scoreSummary);
        summaryText = findViewById(R.id.summaryText);
        btnFinish = findViewById(R.id.btnFinish);

        // Get final score from intent
        int finalScore = getIntent().getIntExtra("finalScore", 0);
        scoreSummary.setText("Your Score: " + finalScore);

        // Set summary subtitle
        summaryText.setText("Quiz Summary");

        // ✅ Play sound based on score
        if (finalScore >= 90) {
            resultPlayer = MediaPlayer.create(this, R.raw.clap);
        } else {
            resultPlayer = MediaPlayer.create(this, R.raw.sad);
        }
        resultPlayer.setVolume(1.0f, 1.0f);
        resultPlayer.start();

        // Button to return to QuizActivity
        btnFinish.setOnClickListener(v -> {
            if (resultPlayer != null) {
                resultPlayer.stop();
                resultPlayer.release();
                resultPlayer = null;
            }
            Intent intent = new Intent(DoDontResult.this, QuizActivity.class);
            intent.putExtra("gameScore", finalScore);
            startActivity(intent);
            finish();
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
