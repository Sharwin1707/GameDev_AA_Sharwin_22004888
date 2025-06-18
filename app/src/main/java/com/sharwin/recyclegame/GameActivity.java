package com.sharwin.recyclegame;


import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.media.MediaPlayer;


import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private int score = 0;
    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private TextView scoreText, timerText;
    private CountDownTimer countDownTimer;

    private ImageView feedbackImage;
    private long timeLeftInMillis = 20000; // 20 seconds



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MusicManager.resumeMusic(); // Ensure music resumes if user exits the activity any other way
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        scoreText = findViewById(R.id.scoreText);
        timerText = findViewById(R.id.timerText);
        feedbackImage = findViewById(R.id.feedbackImage);
        feedbackImage.setVisibility(View.GONE);
        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.wrong);

        setupDragAndDrop();
        startTimer();
    }

    private void setupDragAndDrop() {
        // Setup each trash item
        setupDrag(findViewById(R.id.trash_box), "paper");
        setupDrag(findViewById(R.id.trash_food), "organic");
        setupDrag(findViewById(R.id.trash_steel), "metal");
        setupDrag(findViewById(R.id.trash_tissue), "paper");
        setupDrag(findViewById(R.id.trash_plastic), "plastic");
        setupDrag(findViewById(R.id.trash_newspaper), "paper");

        // Setup bins (example)
        ImageView paperBin = findViewById(R.id.paperBin);
        paperBin.setTag("paper");
        paperBin.setOnDragListener(dragListener);

        ImageView organicBin = findViewById(R.id.organicBin);
        organicBin.setTag("organic");
        organicBin.setOnDragListener(dragListener);

        ImageView metalBin = findViewById(R.id.metalBin);
        metalBin.setTag("metal");
        metalBin.setOnDragListener(dragListener);

        ImageView plasticBin = findViewById(R.id.plasticBin);
        plasticBin.setTag("plastic");
        plasticBin.setOnDragListener(dragListener);
    }

    private void setupBin(ImageView bin, String type) {
        bin.setTag(type);
        bin.setOnDragListener(dragListener);
    }

    private void setupDrag(ImageView trashItem, String type) {
        trashItem.setTag(type);
        trashItem.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
            return false;
        });
    }

    private View.OnDragListener dragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setAlpha(0.7f);
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setAlpha(1.0f);
                    break;

                case DragEvent.ACTION_DROP:
                    v.setAlpha(1.0f);

                    View draggedView = (View) event.getLocalState();
                    String trashType = (String) draggedView.getTag();
                    String binType = (String) v.getTag();

                    if (trashType != null && trashType.equals(binType)) {
                        showFeedbackImage(R.drawable.tick);
                        correctSound.start(); // ✅ Play correct sound
                        score += 10;
                        scoreText.setText("Score: " + score);
                        draggedView.setVisibility(View.INVISIBLE);
                        checkGameCompletion();
                    } else {
                        showFeedbackImage(R.drawable.cross);
                        wrongSound.start(); // ❌ Play wrong sound

                    }


                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f);
                    break;
            }
            return true;
        }
    };

    private void checkGameCompletion() {
        ImageView[] trashItems = {
                findViewById(R.id.trash_box),
                findViewById(R.id.trash_food),
                findViewById(R.id.trash_steel),
                findViewById(R.id.trash_tissue),
                findViewById(R.id.trash_plastic),
                findViewById(R.id.trash_newspaper)
        };

        boolean allSorted = true;
        for (ImageView item : trashItems) {
            if (item.getVisibility() == View.VISIBLE) {
                allSorted = false;
                break;
            }
        }

        if (allSorted) {
            score += 50; // Completion bonus
            scoreText.setText("Score: " + score);

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            goToQuiz();
        }
    }

    private void showFeedbackImage(int resId) {
        feedbackImage.setImageResource(resId);
        feedbackImage.setVisibility(View.VISIBLE);
        feedbackImage.setScaleX(0.5f);
        feedbackImage.setScaleY(0.5f);
        feedbackImage.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(200)
                .start();

        feedbackImage.postDelayed(() -> {
            feedbackImage.animate()
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .alpha(0.0f)
                    .setDuration(200)
                    .withEndAction(() -> {
                        feedbackImage.setVisibility(View.GONE);
                        feedbackImage.setAlpha(1.0f);
                        feedbackImage.setScaleX(1.0f);
                        feedbackImage.setScaleY(1.0f);
                    })
                    .start();
        }, 1500);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Time: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                goToQuiz();
            }
        }.start();
    }

    private void goToQuiz() {
        //Toast.makeText(GameActivity.this, "Going to Quiz...", Toast.LENGTH_SHORT).show(); // Debug
        Intent intent = new Intent(GameActivity.this, GameResultActivity.class);
        intent.putExtra("gameScore", score);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (correctSound != null) {
            correctSound.release();
            correctSound = null;
        }
        if (wrongSound != null) {
            wrongSound.release();
            wrongSound = null;
        }
    }


}


