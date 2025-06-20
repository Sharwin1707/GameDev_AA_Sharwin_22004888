package com.sharwin.recyclegame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import androidx.core.content.ContextCompat;
import android.animation.*;
import android.view.animation.*;
import android.media.MediaPlayer;
import android.os.Handler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class DoDontResult extends AppCompatActivity {

    // UI Elements
    private TextView scoreSummary;
    private TextView scoreProgress;
    private TextView summaryTitle;
    private TextView summaryText;
    private TextView feedbackText;
    private TextView achievementText;
    private MaterialButton btnContinue;
    private MaterialButton btnShare;

    // Animation Elements
    private MaterialCardView scoreCard;
    private MaterialCardView achievementBadge;
    private MaterialCardView feedbackCard;
    private ImageView trophyIcon;
    private ImageView sparkle1, sparkle2, sparkle3;
    private ImageView feedbackIcon;
    private View pulseBackground;
    private View trophyGlow;

    private MediaPlayer resultPlayer;
    private Handler animationHandler = new Handler();
    private int finalScore = 0;
    private int totalQuestions = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodont_result);

        initializeViews();
        setupData();
        startEntranceAnimations();
        setupClickListeners();
    }

    private void initializeViews() {
        // Text Views
        scoreSummary = findViewById(R.id.scoreSummary);
        scoreProgress = findViewById(R.id.scoreProgress);
        summaryTitle = findViewById(R.id.summaryTitle);
        summaryText = findViewById(R.id.summaryText);
        feedbackText = findViewById(R.id.feedbackText);
        achievementText = findViewById(R.id.achievementText);

        // Buttons - Removed btnReview as it's not in XML
        btnContinue = findViewById(R.id.btnContinue);
        btnShare = findViewById(R.id.btnShare);

        // Cards and Views
        scoreCard = findViewById(R.id.scoreCard);
        achievementBadge = findViewById(R.id.achievementBadge);
        feedbackCard = findViewById(R.id.feedbackCard);
        trophyIcon = findViewById(R.id.trophyIcon);
        feedbackIcon = findViewById(R.id.feedbackIcon);
        pulseBackground = findViewById(R.id.pulseBackground);
        trophyGlow = findViewById(R.id.trophyGlow);

        // Sparkle effects
        sparkle1 = findViewById(R.id.sparkle1);
        sparkle2 = findViewById(R.id.sparkle2);
        sparkle3 = findViewById(R.id.sparkle3);
    }

    private void setupData() {
        // Get score data
        finalScore = getIntent().getIntExtra("finalScore", 0);
        int correctAnswers = finalScore / 10; // Assuming 10 points per correct answer

        // Set initial visibility
        scoreCard.setAlpha(0f);
        scoreCard.setScaleX(0.3f);
        scoreCard.setScaleY(0.3f);
        achievementBadge.setVisibility(View.GONE);
        feedbackCard.setAlpha(0f);
        btnContinue.setAlpha(0f);
        btnShare.setAlpha(0f);

        // Setup dynamic content based on score
        setupDynamicContent(correctAnswers);
        playResultSound();
    }

    private void setupDynamicContent(int correctAnswers) {
        // Score display with animation counter
        scoreSummary.setText("Your Score: 0");
        scoreProgress.setText("0/" + totalQuestions + " Correct");

        // Dynamic title and feedback based on performance
        String title, feedback, achievement = "";
        int feedbackColorRes, achievementColorRes;

        if (correctAnswers >= 9) {
            title = "🏆 Outstanding! 🏆";
            feedback = "Perfect recycling knowledge! You're an eco-champion!";
            achievement = "ECO MASTER";
            feedbackColorRes = R.color.green_dark; // Define these colors in colors.xml
            achievementColorRes = R.color.orange_dark;
        } else if (correctAnswers >= 7) {
            title = "🌟 Excellent Work! 🌟";
            feedback = "Great job! You know your recycling basics well!";
            achievement = "ECO EXPERT";
            feedbackColorRes = R.color.green_light;
            achievementColorRes = R.color.blue_dark;
        } else if (correctAnswers >= 5) {
            title = "👍 Good Effort! 👍";
            feedback = "Nice work! Keep learning about recycling!";
            achievement = "ECO LEARNER";
            feedbackColorRes = R.color.orange_light;
            achievementColorRes = R.color.purple;
        } else {
            title = "🌱 Keep Learning! 🌱";
            feedback = "Don't give up! Practice makes perfect!";
            achievement = "ECO BEGINNER";
            feedbackColorRes = R.color.orange_dark;
            achievementColorRes = R.color.gray_dark;
        }

        summaryTitle.setText(title);
        feedbackText.setText(feedback);
        feedbackText.setTextColor(ContextCompat.getColor(this, feedbackColorRes));

        if (correctAnswers >= 7) {
            achievementText.setText(achievement);
            achievementBadge.setCardBackgroundColor(ContextCompat.getColor(this, achievementColorRes));
        }
    }

    private void startEntranceAnimations() {
        // Start pulse background animation
        startPulseAnimation();

        // Animate score card entrance
        animationHandler.postDelayed(() -> {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(scoreCard, "scaleX", 0.3f, 1.2f, 1.0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(scoreCard, "scaleY", 0.3f, 1.2f, 1.0f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(scoreCard, "alpha", 0f, 1f);

            AnimatorSet cardSet = new AnimatorSet();
            cardSet.playTogether(scaleX, scaleY, alpha);
            cardSet.setDuration(800);
            cardSet.setInterpolator(new OvershootInterpolator(1.2f));
            cardSet.start();

            // Start trophy glow animation
            startTrophyGlowAnimation();
        }, 300);

        // Animate score counter
        animationHandler.postDelayed(() -> {
            animateScoreCounter();
            startSparkleAnimations();
        }, 800);

        // Show achievement badge if earned
        animationHandler.postDelayed(() -> {
            int correctAnswers = finalScore / 10;
            if (correctAnswers >= 7) {
                showAchievementBadge();
            }
        }, 1200);

        // Animate feedback card
        animationHandler.postDelayed(() -> {
            ObjectAnimator feedbackAlpha = ObjectAnimator.ofFloat(feedbackCard, "alpha", 0f, 1f);
            ObjectAnimator feedbackTranslate = ObjectAnimator.ofFloat(feedbackCard, "translationY", 50f, 0f);

            AnimatorSet feedbackSet = new AnimatorSet();
            feedbackSet.playTogether(feedbackAlpha, feedbackTranslate);
            feedbackSet.setDuration(600);
            feedbackSet.setInterpolator(new DecelerateInterpolator());
            feedbackSet.start();
        }, 1500);

        // Animate buttons
        animateButtons();
    }

    private void startPulseAnimation() {
        ObjectAnimator pulseScale = ObjectAnimator.ofFloat(pulseBackground, "scaleX", 1.0f, 1.1f, 1.0f);
        ObjectAnimator pulseScaleY = ObjectAnimator.ofFloat(pulseBackground, "scaleY", 1.0f, 1.1f, 1.0f);
        ObjectAnimator pulseAlpha = ObjectAnimator.ofFloat(pulseBackground, "alpha", 0.3f, 0.1f, 0.3f);

        pulseScale.setRepeatCount(ValueAnimator.INFINITE);
        pulseScaleY.setRepeatCount(ValueAnimator.INFINITE);
        pulseAlpha.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.playTogether(pulseScale, pulseScaleY, pulseAlpha);
        pulseSet.setDuration(2000);
        pulseSet.start();
    }

    private void startTrophyGlowAnimation() {
        ObjectAnimator glowAlpha = ObjectAnimator.ofFloat(trophyGlow, "alpha", 0.6f, 0.2f, 0.6f);
        glowAlpha.setDuration(1500);
        glowAlpha.setRepeatCount(ValueAnimator.INFINITE);
        glowAlpha.start();

        // Trophy bounce
        ObjectAnimator trophyBounce = ObjectAnimator.ofFloat(trophyIcon, "scaleX", 1.0f, 1.1f, 1.0f);
        ObjectAnimator trophyBounceY = ObjectAnimator.ofFloat(trophyIcon, "scaleY", 1.0f, 1.1f, 1.0f);

        AnimatorSet trophySet = new AnimatorSet();
        trophySet.playTogether(trophyBounce, trophyBounceY);
        trophySet.setDuration(1000);
        trophySet.setInterpolator(new BounceInterpolator());
        trophySet.start();
    }

    private void animateScoreCounter() {
        ValueAnimator scoreAnimator = ValueAnimator.ofInt(0, finalScore);
        scoreAnimator.setDuration(1500);
        scoreAnimator.setInterpolator(new DecelerateInterpolator());

        scoreAnimator.addUpdateListener(animation -> {
            int currentScore = (int) animation.getAnimatedValue();
            int currentCorrect = currentScore / 10;
            scoreSummary.setText("Your Score: " + currentScore);
            scoreProgress.setText(currentCorrect + "/" + totalQuestions + " Correct");
        });

        scoreAnimator.start();
    }

    private void startSparkleAnimations() {
        ImageView[] sparkles = {sparkle1, sparkle2, sparkle3};

        for (int i = 0; i < sparkles.length; i++) {
            final ImageView sparkle = sparkles[i];

            animationHandler.postDelayed(() -> {
                ObjectAnimator sparkleAlpha = ObjectAnimator.ofFloat(sparkle, "alpha", 0f, 1f, 0f);
                ObjectAnimator sparkleScale = ObjectAnimator.ofFloat(sparkle, "scaleX", 0.5f, 1.5f, 0.5f);
                ObjectAnimator sparkleScaleY = ObjectAnimator.ofFloat(sparkle, "scaleY", 0.5f, 1.5f, 0.5f);

                sparkleAlpha.setRepeatCount(ValueAnimator.INFINITE);
                sparkleAlpha.setRepeatMode(ValueAnimator.RESTART);
                sparkleScale.setRepeatCount(ValueAnimator.INFINITE);
                sparkleScale.setRepeatMode(ValueAnimator.RESTART);
                sparkleScaleY.setRepeatCount(ValueAnimator.INFINITE);
                sparkleScaleY.setRepeatMode(ValueAnimator.RESTART);

                AnimatorSet sparkleSet = new AnimatorSet();
                sparkleSet.playTogether(sparkleAlpha, sparkleScale, sparkleScaleY);
                sparkleSet.setDuration(1200);
                sparkleSet.start();
            }, i * 400);
        }
    }

    private void showAchievementBadge() {
        achievementBadge.setVisibility(View.VISIBLE);
        achievementBadge.setAlpha(0f);
        achievementBadge.setScaleX(0.3f);
        achievementBadge.setScaleY(0.3f);

        ObjectAnimator badgeAlpha = ObjectAnimator.ofFloat(achievementBadge, "alpha", 0f, 1f);
        ObjectAnimator badgeScaleX = ObjectAnimator.ofFloat(achievementBadge, "scaleX", 0.3f, 1.2f, 1.0f);
        ObjectAnimator badgeScaleY = ObjectAnimator.ofFloat(achievementBadge, "scaleY", 0.3f, 1.2f, 1.0f);

        AnimatorSet badgeSet = new AnimatorSet();
        badgeSet.playTogether(badgeAlpha, badgeScaleX, badgeScaleY);
        badgeSet.setDuration(600);
        badgeSet.setInterpolator(new OvershootInterpolator(1.5f));
        badgeSet.start();
    }

    private void animateButtons() {
        MaterialButton[] buttons = {btnContinue, btnShare}; // Removed btnReview

        for (int i = 0; i < buttons.length; i++) {
            final MaterialButton button = buttons[i];

            animationHandler.postDelayed(() -> {
                ObjectAnimator buttonAlpha = ObjectAnimator.ofFloat(button, "alpha", 0f, 1f);
                ObjectAnimator buttonTranslate = ObjectAnimator.ofFloat(button, "translationY", 100f, 0f);

                AnimatorSet buttonSet = new AnimatorSet();
                buttonSet.playTogether(buttonAlpha, buttonTranslate);
                buttonSet.setDuration(500);
                buttonSet.setInterpolator(new DecelerateInterpolator());
                buttonSet.start();
            }, 1800 + (i * 150));
        }
    }

    private void playResultSound() {
        try {
            if (finalScore >= 90) {
                resultPlayer = MediaPlayer.create(this, R.raw.clap);
            } else if (finalScore >= 70) {
                resultPlayer = MediaPlayer.create(this, R.raw.clap);
            } else {
                resultPlayer = MediaPlayer.create(this, R.raw.sad);
            }

            if (resultPlayer != null) {
                resultPlayer.setVolume(0.7f, 0.7f);
                resultPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        // Main continue button
        btnContinue.setOnClickListener(v -> {
            animateButtonClick(v);
            animationHandler.postDelayed(() -> {
                stopSounds();
                Intent intent = new Intent(DoDontResult.this, QuizActivity.class);
                intent.putExtra("gameScore", finalScore);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }, 200);
        });

        // Share button
        btnShare.setOnClickListener(v -> {
            animateButtonClick(v);
            shareResult();
        });

        // Score card click for celebration
        scoreCard.setOnClickListener(v -> {
            celebrateScore();
        });
    }

    private void animateButtonClick(View button) {
        ObjectAnimator scaleDown = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 0.95f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1.0f, 0.95f);
        ObjectAnimator scaleUp = ObjectAnimator.ofFloat(button, "scaleX", 0.95f, 1.0f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 0.95f, 1.0f);

        AnimatorSet scaleDownSet = new AnimatorSet();
        scaleDownSet.playTogether(scaleDown, scaleDownY);
        scaleDownSet.setDuration(100);

        AnimatorSet scaleUpSet = new AnimatorSet();
        scaleUpSet.playTogether(scaleUp, scaleUpY);
        scaleUpSet.setDuration(100);

        scaleDownSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scaleUpSet.start();
            }
        });

        scaleDownSet.start();
    }

    private void celebrateScore() {
        // Extra celebration animation when user taps score
        ObjectAnimator celebrate = ObjectAnimator.ofFloat(scoreCard, "rotation", 0f, 10f, -10f, 5f, 0f);
        celebrate.setDuration(600);
        celebrate.start();

        // Extra sparkles
        startSparkleAnimations();
    }

    private void shareResult() {
        String shareText = "🎉 I scored " + finalScore + " points in the Recycling Quiz! 🌱♻️\n" +
                "Learning about recycling do's and don'ts! 🌍\n" +
                "#RecyclingGame #EcoFriendly #Sustainability";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share your result!"));
    }

    private void stopSounds() {
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
        stopSounds();
        if (animationHandler != null) {
            animationHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (resultPlayer != null && resultPlayer.isPlaying()) {
            resultPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resultPlayer != null && !resultPlayer.isPlaying()) {
            try {
                resultPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}