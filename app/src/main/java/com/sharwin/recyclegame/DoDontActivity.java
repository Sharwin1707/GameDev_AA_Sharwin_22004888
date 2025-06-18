package com.sharwin.recyclegame;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class DoDontActivity extends AppCompatActivity {

    private TextView questionText, feedbackText, timerText, scoreText;
    private ImageView itemImage;
    private MediaPlayer correctSound, wrongSound;

    private Button btnRecycle, btnTrash, btnNextItem;
    private TextView itemLabel;
    private MaterialCardView feedbackCard;

    private int currentIndex = 0;
    private int gameScore = 0; // Score from previous game
    private int score = 0;
    private CountDownTimer countDownTimer;
    private final long questionTime = 15000; // 15 seconds
    private final long interval = 1000; // 1 second ticks

    private int[] itemImages = {
            R.drawable.straws,
            R.drawable.pizza,
            R.drawable.magazine,
            R.drawable.jars,
            R.drawable.winebottles,
            R.drawable.diaper,
            R.drawable.plate,
            R.drawable.polisterin,
            R.drawable.smartphone,
            R.drawable.open_box
    };

    private boolean[] isRecyclable = {
            false,  // straws
            false,  // pizza
            true,   // magazine
            true,   // jars
            true,  // winebottles
            false,  // diaper
            false,  // plate
            false,   // polisterin
            true,   // smartphone
            true    // open_box
    };

    private String[] itemNames = {
            "Plastic Straws",
            "Greasy Pizza Slice",
            "Old Magazine",
            "Glass Jars",
            "Wine Bottles",
            "Used Diaper",
            "Broken Plate",
            "Polystyrene Food Box",
            "Used Smartphone",
            "Open Cardboard Box"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodont);

        gameScore = getIntent().getIntExtra("finalScore", 0);
        score = gameScore;

        questionText = findViewById(R.id.questionText);
        itemImage = findViewById(R.id.itemImage);
        feedbackText = findViewById(R.id.feedbackText);
        btnRecycle = findViewById(R.id.btnRecycle);
        btnTrash = findViewById(R.id.btnTrash);
        btnNextItem = findViewById(R.id.btnNextItem);
        timerText = findViewById(R.id.timerText);
        scoreText = findViewById(R.id.scoreText);
        itemLabel = findViewById(R.id.itemLabel);
        feedbackCard = findViewById(R.id.feedbackCard);
        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.wrong);



        scoreText.setText("Score: " + score);
        feedbackText.setText("");
        feedbackCard.setVisibility(View.GONE);
        btnNextItem.setVisibility(View.GONE);

        showItem();

        btnRecycle.setOnClickListener(v -> {
            stopTimer();
            checkAnswer(true);
        });

        btnTrash.setOnClickListener(v -> {
            stopTimer();
            checkAnswer(false);
        });

        btnNextItem.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex < itemImages.length) {
                showItem();
            } else {
                Intent intent = new Intent(DoDontActivity.this, DoDontResult.class);
                intent.putExtra("finalScore", score);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showItem() {
        itemImage.setImageResource(itemImages[currentIndex]);
        itemLabel.setText(itemNames[currentIndex]);
        feedbackText.setText("");
        feedbackCard.setVisibility(View.GONE);
        btnNextItem.setVisibility(View.GONE);
        btnRecycle.setEnabled(true);
        btnTrash.setEnabled(true);
        timerText.setText("Time: 15");

        startTimer();
    }

    private void checkAnswer(boolean userChoice) {
        boolean correctAnswer = isRecyclable[currentIndex];
        feedbackCard.setVisibility(View.VISIBLE);

        if (userChoice == correctAnswer) {
            correctSound.start();  // Play correct sound
            score += 10;
            feedbackText.setText("Correct! 👍");
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            wrongSound.start();  // Play wrong sound
            score = Math.max(0, score - 5); // Deduct 5 points, but don't go below 0
            feedbackText.setText("Oops! That's not right. -5 points");
            feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        scoreText.setText("Score: " + score);
        btnRecycle.setEnabled(false);
        btnTrash.setEnabled(false);

        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                btnNextItem.setVisibility(View.VISIBLE);
            }
        }.start();
    }


    private void startTimer() {
        countDownTimer = new CountDownTimer(questionTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                timerText.setText("Time: 0");
                wrongSound.start();  // Play wrong sound
                feedbackCard.setVisibility(View.VISIBLE);
                feedbackText.setText("Time's up! No points.");
                feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                btnRecycle.setEnabled(false);
                btnTrash.setEnabled(false);
                btnNextItem.setVisibility(View.VISIBLE);
            }
        };
        countDownTimer.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (correctSound != null) correctSound.release();
        if (wrongSound != null) wrongSound.release();
    }

}
