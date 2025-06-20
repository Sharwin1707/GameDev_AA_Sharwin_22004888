package com.sharwin.recyclegame;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class GameResultActivity extends AppCompatActivity {

    // UI Elements
    TextView finalScoreText, timeText, accuracyText, performanceTitle, performanceMessage, scoreLabel;
    MaterialButton btnContinueToQuiz, btnShareResult;
    ImageView trophyIcon;
    LinearLayout celebrationBanner, statsRow, buttonContainer;
    MaterialCardView scoreCard, messageCard;

    // Media
    MediaPlayer resultPlayer;

    // Data
    int gameScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        initializeViews();
        setupData();
        startAnimationSequence();
        setupClickListeners();
    }

    private void initializeViews() {
        // Text Views
        finalScoreText = findViewById(R.id.finalScoreText);
        timeText = findViewById(R.id.timeText);
        accuracyText = findViewById(R.id.accuracyText);
        performanceTitle = findViewById(R.id.performanceTitle);
        performanceMessage = findViewById(R.id.performanceMessage);
        scoreLabel = findViewById(R.id.scoreLabel);

        // Buttons
        btnContinueToQuiz = findViewById(R.id.btnContinueToQuiz);
        btnShareResult = findViewById(R.id.btnShareResult);

        // Image Views
        trophyIcon = findViewById(R.id.trophyIcon);

        // Layouts and Cards
        celebrationBanner = findViewById(R.id.celebrationBanner);
        statsRow = findViewById(R.id.statsRow);
        buttonContainer = findViewById(R.id.buttonContainer);
        scoreCard = findViewById(R.id.scoreCard);
        messageCard = findViewById(R.id.messageCard);
    }

    private void setupData() {
        // Get score from intent
        gameScore = getIntent().getIntExtra("gameScore", 0);

        // Calculate mock data (you can replace with real data)
        int minutes = gameScore / 20; // Mock time calculation
        int seconds = (gameScore % 20) * 3;
        String timeString = String.format("%d:%02d", minutes, seconds);

        int accuracy = Math.min(95, 60 + (gameScore * 2)); // Mock accuracy

        // Set data
        timeText.setText(timeString);
        accuracyText.setText(accuracy + "%");

        // Set performance message based on score
        setPerformanceMessage(gameScore);
    }

    private void setPerformanceMessage(int score) {
        if (score >= 80) {
            performanceTitle.setText("Outstanding! 🏆");
            performanceMessage.setText("You're a sorting master! Ready to tackle the quiz?");
        } else if (score >= 60) {
            performanceTitle.setText("Great Job! ⭐");
            performanceMessage.setText("Excellent sorting skills! Let's continue your journey.");
        } else if (score >= 40) {
            performanceTitle.setText("Good Work! 👍");
            performanceMessage.setText("Nice progress! Keep practicing to improve further.");
        } else {
            performanceTitle.setText("Keep Trying! 💪");
            performanceMessage.setText("Practice makes perfect! Don't give up, you've got this!");
        }
    }

    private void startAnimationSequence() {
        // Play sound based on score
        playResultSound();

        // Start animation sequence with delays
        Handler handler = new Handler();

        // 1. Show celebration banner (0ms)
        animateFadeIn(celebrationBanner, 0);

        // 2. Animate trophy and score card (300ms)
        handler.postDelayed(() -> {
            animateTrophyBounce();
            animateScoreCounter();
        }, 300);

        // 3. Show stats row (800ms)
        handler.postDelayed(() -> animateFadeIn(statsRow, 0), 800);

        // 4. Show performance message (1200ms)
        handler.postDelayed(() -> animateFadeIn(messageCard, 0), 1200);

        // 5. Show buttons (1600ms)
        handler.postDelayed(() -> animateFadeIn(buttonContainer, 0), 1600);

        // 6. Add score card pulse animation (2000ms)
        handler.postDelayed(this::animateScoreCardPulse, 2000);
    }

    private void animateFadeIn(View view, long delay) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(600);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setStartDelay(delay);
        fadeIn.start();
    }

    private void animateTrophyBounce() {
        // Trophy scale animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(trophyIcon, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(trophyIcon, "scaleY", 0f, 1.2f, 1f);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(trophyIcon, "rotation", -10f, 10f, 0f);

        AnimatorSet trophySet = new AnimatorSet();
        trophySet.playTogether(scaleX, scaleY, rotation);
        trophySet.setDuration(800);
        trophySet.setInterpolator(new BounceInterpolator());
        trophySet.start();

        // Score label fade in
        animateFadeIn(scoreLabel, 400);
    }

    private void animateScoreCounter() {
        // Animate score from 0 to final score
        ValueAnimator scoreAnimator = ValueAnimator.ofInt(0, gameScore);
        scoreAnimator.setDuration(1000);
        scoreAnimator.setInterpolator(new DecelerateInterpolator());
        scoreAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            finalScoreText.setText(animatedValue + " Points");
        });

        // Fade in the score text
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(finalScoreText, "alpha", 0f, 1f);
        fadeIn.setDuration(300);

        AnimatorSet scoreSet = new AnimatorSet();
        scoreSet.playTogether(scoreAnimator, fadeIn);
        scoreSet.setStartDelay(400);
        scoreSet.start();
    }

    private void animateScoreCardPulse() {
        ObjectAnimator pulseX = ObjectAnimator.ofFloat(scoreCard, "scaleX", 1f, 1.05f, 1f);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(scoreCard, "scaleY", 1f, 1.05f, 1f);

        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.playTogether(pulseX, pulseY);
        pulseSet.setDuration(1000);
        pulseSet.setInterpolator(new DecelerateInterpolator());
        pulseSet.start();
    }

    private void playResultSound() {
        try {
            if (gameScore >= 60) {
                resultPlayer = MediaPlayer.create(this, R.raw.clap);
            } else if (gameScore >= 30) {
                // You can add a "good" sound here if you have one
                resultPlayer = MediaPlayer.create(this, R.raw.clap);
            } else {
                resultPlayer = MediaPlayer.create(this, R.raw.sad);
            }

            if (resultPlayer != null) {
                resultPlayer.setVolume(0.7f, 0.7f);
                resultPlayer.start();
            }
        } catch (Exception e) {
            // Handle sound loading error gracefully
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        // Continue button
        btnContinueToQuiz.setOnClickListener(view -> {
            // Add button press animation
            animateButtonPress(view);

            // Navigate after short delay
            new Handler().postDelayed(() -> {
                stopAndReleaseMedia();
                Intent intent = new Intent(GameResultActivity.this, DoDontActivity.class);
                intent.putExtra("finalScore", gameScore);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }, 150);
        });

        // Share button
        btnShareResult.setOnClickListener(view -> {
            animateButtonPress(view);
            shareResult();
        });

        // Score card click for fun interaction
        scoreCard.setOnClickListener(view -> {
            animateScoreCardPulse();
            // Optional: Play a small celebration sound
        });
    }

    private void animateButtonPress(View button) {
        ObjectAnimator scaleDown = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.95f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.95f);
        ObjectAnimator scaleUp = ObjectAnimator.ofFloat(button, "scaleX", 0.95f, 1f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 0.95f, 1f);

        AnimatorSet scaleDown_ = new AnimatorSet();
        scaleDown_.playTogether(scaleDown, scaleDownY);
        scaleDown_.setDuration(100);

        AnimatorSet scaleUp_ = new AnimatorSet();
        scaleUp_.playTogether(scaleUp, scaleUpY);
        scaleUp_.setDuration(100);

        AnimatorSet buttonPress = new AnimatorSet();
        buttonPress.playSequentially(scaleDown_, scaleUp_);
        buttonPress.start();
    }

    private void shareResult() {
        String shareText = String.format(
                "🎮 Just scored %d points in the Recycling Sorting Game! 🌱♻️\n\n" +
                        "Think you can beat my score? Download the app and try! 🏆\n\n" +
                        "#RecyclingGame #EcoFriendly #SaveTheEarth",
                gameScore
        );

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My Recycling Game Score!");

        startActivity(Intent.createChooser(shareIntent, "Share your score!"));
    }

    private void stopAndReleaseMedia() {
        if (resultPlayer != null) {
            try {
                if (resultPlayer.isPlaying()) {
                    resultPlayer.stop();
                }
                resultPlayer.release();
                resultPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAndReleaseMedia();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (resultPlayer != null && resultPlayer.isPlaying()) {
            resultPlayer.pause();
        }
    }
}