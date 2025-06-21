package com.sharwin.recyclegame;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DoDontActivity extends AppCompatActivity {

    private TextView questionText, feedbackText, timerText, scoreText, questionNumber, additionalInfo, streakText;
    private ImageView itemImage, feedbackIcon;
    private MediaPlayer correctSound, wrongSound;
    private ProgressBar progressBar;
    private FloatingActionButton fabHint;
    private Vibrator vibrator;

    private Button btnRecycle, btnTrash, btnNextItem;
    private TextView itemLabel;
    private MaterialCardView feedbackCard, streakCard;

    private int currentIndex = 0;
    private int gameScore = 0; // Score from previous game
    private int score = 0;
    private int streak = 0; // Track consecutive correct answers
    private CountDownTimer countDownTimer;
    private final long questionTime = 10000; // 15 seconds
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
            true,   // winebottles
            false,  // diaper
            false,  // plate
            false,  // polisterin
            true,   // smartphone
            true    // open_box
    };

    private String[] itemNames = {
            "🥤 Plastic Straws",
            "🍕 Greasy Pizza Slice",
            "📰 Old Magazine",
            "🏺 Glass Jars",
            "🍷 Wine Bottles",
            "👶 Used Diaper",
            "🍽️ Broken Plate",
            "📦 Polystyrene Food Box",
            "📱 Used Smartphone",
            "📦 Open Cardboard Box"
    };

    // Educational info for each item
    private String[] itemInfo = {
            "Plastic straws are too small for recycling machinery and often end up contaminating recyclables.",
            "Greasy food containers contaminate other recyclables and can't be processed properly.",
            "Clean paper magazines are perfectly recyclable and become new paper products!",
            "Glass jars are 100% recyclable and can be reused infinitely without quality loss!",
            "Wine bottles are valuable recyclables - they become new bottles, fiberglass, and countertops!",
            "Diapers contain mixed materials and bodily waste, making them non-recyclable.",
            "Broken ceramics and dishes don't melt at the same temperature as glass recyclables.",
            "Polystyrene foam breaks into tiny pieces that contaminate recycling streams.",
            "Smartphones contain valuable metals that can be recovered through e-waste recycling!",
            "Clean cardboard boxes are highly recyclable and become new cardboard products!"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodont);

        gameScore = getIntent().getIntExtra("finalScore", 0);
        score = gameScore;

        // Initialize all views
        initializeViews();

        // Initialize sounds and vibration
        correctSound = MediaPlayer.create(this, R.raw.correct);
        wrongSound = MediaPlayer.create(this, R.raw.wrong);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Set initial values
        updateScoreDisplay();
        updateProgressBar();
        feedbackText.setText("");
        feedbackCard.setVisibility(View.GONE);
        btnNextItem.setVisibility(View.GONE);
        streakCard.setVisibility(View.GONE);

        showItem();
        setupClickListeners();
    }

    private void initializeViews() {
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
        questionNumber = findViewById(R.id.questionNumber);
        progressBar = findViewById(R.id.progressBar);
        fabHint = findViewById(R.id.fabHint);
        additionalInfo = findViewById(R.id.additionalInfo);
        feedbackIcon = findViewById(R.id.feedbackImage);
        streakCard = findViewById(R.id.streakCard);
        streakText = findViewById(R.id.streakText);
    }

    private void setupClickListeners() {
        btnRecycle.setOnClickListener(v -> {
            animateButton(btnRecycle);
            vibrateDevice();
            stopTimer();
            checkAnswer(true);
        });

        btnTrash.setOnClickListener(v -> {
            animateButton(btnTrash);
            vibrateDevice();
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
                intent.putExtra("streak", streak);
                startActivity(intent);
                finish();
            }
        });

        fabHint.setOnClickListener(v -> {
            animateHintButton();
            showHint();
        });
    }

    private void showItem() {
        // Animate item entrance
        animateItemEntrance();

        itemImage.setImageResource(itemImages[currentIndex]);
        itemLabel.setText(itemNames[currentIndex]);
        questionNumber.setText(String.valueOf(currentIndex + 1));

        // Reset UI state
        feedbackText.setText("");
        feedbackCard.setVisibility(View.GONE);
        btnNextItem.setVisibility(View.GONE);
        btnRecycle.setEnabled(true);
        btnTrash.setEnabled(true);
        timerText.setText("15s");

        updateProgressBar();
        startTimer();
    }

    private void checkAnswer(boolean userChoice) {
        boolean correctAnswer = isRecyclable[currentIndex];
        feedbackCard.setVisibility(View.VISIBLE);
        animateCardEntrance(feedbackCard);

        if (userChoice == correctAnswer) {
            handleCorrectAnswer();
        } else {
            handleWrongAnswer();
        }

        updateScoreDisplay();
        btnRecycle.setEnabled(false);
        btnTrash.setEnabled(false);

        // Show additional educational info
        additionalInfo.setText(itemInfo[currentIndex]);

        // Delay showing next button
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                btnNextItem.setVisibility(View.VISIBLE);
                animateCardEntrance(btnNextItem);
            }
        }.start();
    }

    private void handleCorrectAnswer() {
        correctSound.start();
        vibrateSuccess();
        score += 10;
        streak++;

        feedbackText.setText("🎉 Excellent! You're helping save the planet!");
        feedbackText.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        // Set feedback icon to celebration
        if (feedbackIcon != null) {
            feedbackIcon.setImageResource(android.R.drawable.star_on);
        }

        // Show streak if 3 or more correct in a row
        if (streak >= 3) {
            showStreak();
        }
    }

    private void handleWrongAnswer() {
        wrongSound.start();
        vibrateError();
        score = Math.max(0, score - 5);
        streak = 0; // Reset streak
        hideStreak();

        feedbackText.setText("❌ Oops! That's not right. -5 points");
        feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        // Set feedback icon to error
        if (feedbackIcon != null) {
            feedbackIcon.setImageResource(android.R.drawable.ic_dialog_alert);
        }
    }

    private void showStreak() {
        streakText.setText("🔥 " + streak + " in a row!");
        streakCard.setVisibility(View.VISIBLE);
        animateCardEntrance(streakCard);
    }

    private void hideStreak() {
        streakCard.setVisibility(View.GONE);
    }

    private void showHint() {
        String hint = isRecyclable[currentIndex] ?
                "💡 This item CAN be recycled!" :
                "💡 This item should go in the TRASH!";

        // You could show this in a toast or temporary text view
        // For now, we'll just vibrate as feedback
        vibrateDevice();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(questionTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                timerText.setText(secondsLeft + "s");

                // Change timer color as time runs out
                if (secondsLeft <= 5) {
                    timerText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    timerText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                }
            }

            @Override
            public void onFinish() {
                timerText.setText("0s");
                wrongSound.start();
                vibrateError();
                streak = 0; // Reset streak on timeout
                hideStreak();

                feedbackCard.setVisibility(View.VISIBLE);
                animateCardEntrance(feedbackCard);
                feedbackText.setText("⏰ Time's up! No points awarded.");
                feedbackText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

                if (feedbackIcon != null) {
                    feedbackIcon.setImageResource(android.R.drawable.ic_dialog_alert);
                }

                btnRecycle.setEnabled(false);
                btnTrash.setEnabled(false);
                btnNextItem.setVisibility(View.VISIBLE);
                additionalInfo.setText(itemInfo[currentIndex]);
            }
        };
        countDownTimer.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void updateProgressBar() {
        int progress = (int) (((float) (currentIndex + 1) / itemImages.length) * 100);

        // Animate progress bar
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);
        progressAnimator.setDuration(500);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.start();
    }

    private void updateScoreDisplay() {
        // Animate score change
        ValueAnimator scoreAnimator = ValueAnimator.ofInt(Integer.parseInt(scoreText.getText().toString().replaceAll("[^0-9]", "")), score);
        scoreAnimator.setDuration(500);
        scoreAnimator.addUpdateListener(animation -> scoreText.setText(String.valueOf(animation.getAnimatedValue())));
        scoreAnimator.start();
    }

    // Animation methods
    private void animateButton(View button) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.95f, 1.0f, 0.95f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(100);
        scaleAnimation.setRepeatCount(1);
        scaleAnimation.setRepeatMode(Animation.REVERSE);
        button.startAnimation(scaleAnimation);
    }

    private void animateHintButton() {
        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(fabHint, "rotation", 0f, 360f);
        rotateAnimator.setDuration(500);
        rotateAnimator.start();
    }

    private void animateItemEntrance() {
        ScaleAnimation scaleUp = new ScaleAnimation(
                0.8f, 1.0f, 0.8f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleUp.setDuration(300);
        scaleUp.setInterpolator(new AccelerateDecelerateInterpolator());
        itemImage.startAnimation(scaleUp);
    }

    private void animateCardEntrance(View view) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.8f, 1f);

        fadeIn.setDuration(300);
        scaleX.setDuration(300);
        scaleY.setDuration(300);

        fadeIn.start();
        scaleX.start();
        scaleY.start();
    }

    // Haptic feedback methods
    private void vibrateDevice() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(50);
            }
        }
    }

    private void vibrateSuccess() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(100);
            }
        }
    }

    private void vibrateError() {
        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                long[] pattern = {0, 100, 100, 100};
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1));
            } else {
                long[] pattern = {0, 100, 100, 100};
                vibrator.vibrate(pattern, -1);
            }
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