package com.sharwin.recyclegame;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity
{


    private int score = 0;
    private MediaPlayer correctSound;
    private MediaPlayer wrongSound;
    private TextView scoreText, timerText;
    private CountDownTimer countDownTimer;

    private ImageView feedbackImage;
    private long timeLeftInMillis = 20000; // 20 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MusicManager.resumeMusic();
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
        animateGameStart();
    }

    private void animateGameStart() {
        // Animate all trash items appearing with stagger effect
        ImageView[] trashItems = {
                findViewById(R.id.trash_box),
                findViewById(R.id.trash_food),
                findViewById(R.id.trash_steel),
                findViewById(R.id.trash_tissue),
                findViewById(R.id.trash_plastic),
                findViewById(R.id.trash_newspaper)
        };

        // Animate bins appearing
        ImageView[] bins = {
                findViewById(R.id.paperBin),
                findViewById(R.id.organicBin),
                findViewById(R.id.metalBin),
                findViewById(R.id.plasticBin)
        };

        // Start with everything invisible
        for (ImageView item : trashItems) {
            item.setAlpha(0f);
            item.setScaleX(0f);
            item.setScaleY(0f);
        }

        for (ImageView bin : bins) {
            bin.setAlpha(0f);
            bin.setScaleX(0.8f);
            bin.setScaleY(0.8f);
        }

        // Animate bins first
        for (int i = 0; i < bins.length; i++) {
            int finalI = i;
            bins[i].postDelayed(() -> {
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(bins[finalI], "scaleX", 0.8f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(bins[finalI], "scaleY", 0.8f, 1f);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(bins[finalI], "alpha", 0f, 1f);

                AnimatorSet set = new AnimatorSet();
                set.playTogether(scaleX, scaleY, alpha);
                set.setDuration(500);
                set.setInterpolator(new OvershootInterpolator());
                set.start();
            }, i * 100);
        }

        // Then animate trash items with bounce effect
        for (int i = 0; i < trashItems.length; i++) {
            final int index = i;
            trashItems[i].postDelayed(() -> {
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(trashItems[index], "scaleX", 0f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(trashItems[index], "scaleY", 0f, 1f);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(trashItems[index], "alpha", 0f, 1f);

                AnimatorSet set = new AnimatorSet();
                set.playTogether(scaleX, scaleY, alpha);
                set.setDuration(600);
                set.setInterpolator(new BounceInterpolator());
                set.start();

                // Add idle floating animation
                addIdleAnimation(trashItems[index]);
            }, 800 + (index * 150));
        }
    }

    private void addIdleAnimation(ImageView view) {
        // Subtle floating animation
        ObjectAnimator floatUp = ObjectAnimator.ofFloat(view, "translationY", 0f, -15f);
        floatUp.setDuration(2000);
        floatUp.setRepeatCount(ValueAnimator.INFINITE);
        floatUp.setRepeatMode(ValueAnimator.REVERSE);
        floatUp.setInterpolator(new AccelerateDecelerateInterpolator());
        floatUp.start();

        // Subtle rotation
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", -2f, 2f);
        rotate.setDuration(3000);
        rotate.setRepeatCount(ValueAnimator.INFINITE);
        rotate.setRepeatMode(ValueAnimator.REVERSE);
        rotate.setInterpolator(new AccelerateDecelerateInterpolator());
        rotate.start();
    }

    private void setupDragAndDrop() {
        // Setup each trash item
        setupDrag(findViewById(R.id.trash_box), "paper");
        setupDrag(findViewById(R.id.trash_food), "organic");
        setupDrag(findViewById(R.id.trash_steel), "metal");
        setupDrag(findViewById(R.id.trash_tissue), "paper");
        setupDrag(findViewById(R.id.trash_plastic), "plastic");
        setupDrag(findViewById(R.id.trash_newspaper), "paper");

        // Setup bins
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

    private void setupDrag(ImageView trashItem, String type) {
        trashItem.setTag(type);
        trashItem.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Animate pick up
                animatePickup(v);

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
            return false;
        });
    }

    private void animatePickup(View view) {
        // Scale up slightly when picked up
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY);
        set.setDuration(150);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
    }

    private View.OnDragListener dragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    animateBinHover(v, true);
                    break;

                case DragEvent.ACTION_DRAG_EXITED:
                    animateBinHover(v, false);
                    break;

                case DragEvent.ACTION_DROP:
                    animateBinHover(v, false);

                    View draggedView = (View) event.getLocalState();
                    String trashType = (String) draggedView.getTag();
                    String binType = (String) v.getTag();

                    if (trashType != null && trashType.equals(binType)) {
                        animateCorrectDrop(draggedView, v);
                        showFeedbackImage(R.drawable.tick);
                        correctSound.start();
                        updateScore(10);
                        checkGameCompletion();
                    } else {
                        animateWrongDrop(draggedView);
                        showFeedbackImage(R.drawable.cross);
                        wrongSound.start();
                        updateScore(-5);
                    }
                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    View draggedItem = (View) event.getLocalState();
                    // Reset scale when drag ends
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(draggedItem, "scaleX", draggedItem.getScaleX(), 1f);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(draggedItem, "scaleY", draggedItem.getScaleY(), 1f);

                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(scaleX, scaleY);
                    set.setDuration(200);
                    set.start();
                    break;
            }
            return true;
        }
    };

    private void animateBinHover(View bin, boolean isHovering) {
        float targetScale = isHovering ? 1.1f : 1f;
        float targetAlpha = isHovering ? 0.8f : 1f;

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(bin, "scaleX", bin.getScaleX(), targetScale);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(bin, "scaleY", bin.getScaleY(), targetScale);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(bin, "alpha", bin.getAlpha(), targetAlpha);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, alpha);
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
    }

    private void animateCorrectDrop(View trashItem, View bin) {
        // Create celebration burst effect
        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(trashItem, "scaleX", 1f, 1.3f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(trashItem, "scaleY", 1f, 1.3f);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(trashItem, "alpha", 1f, 0f);

        AnimatorSet celebrationSet = new AnimatorSet();
        celebrationSet.playTogether(scaleUpX, scaleUpY, fadeOut);
        celebrationSet.setDuration(300);
        celebrationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        celebrationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                trashItem.setVisibility(View.INVISIBLE);
                // Reset for potential reuse
                trashItem.setAlpha(1f);
                trashItem.setScaleX(1f);
                trashItem.setScaleY(1f);
            }
        });

        celebrationSet.start();

        // Animate bin celebration
        ObjectAnimator binBounce = ObjectAnimator.ofFloat(bin, "scaleY", 1f, 1.2f, 1f);
        binBounce.setDuration(400);
        binBounce.setInterpolator(new BounceInterpolator());
        binBounce.start();
    }

    private void animateWrongDrop(View trashItem) {
        // Shake animation for wrong drop
        ObjectAnimator shake = ObjectAnimator.ofFloat(trashItem, "translationX", 0f, -25f, 25f, -15f, 15f, -5f, 5f, 0f);
        shake.setDuration(500);
        shake.start();

        // Brief red tint effect using background tint
        if (trashItem instanceof ImageView) {
            ImageView imageView = (ImageView) trashItem;

            // Create a red tint effect
            ValueAnimator tintAnimator = ValueAnimator.ofFloat(0f, 1f, 0f);
            tintAnimator.setDuration(300);
            tintAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                int alpha = (int) (255 * value);
                imageView.setColorFilter(Color.argb(alpha, 255, 0, 0), android.graphics.PorterDuff.Mode.SRC_ATOP);
            });
            tintAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    imageView.clearColorFilter(); // Clear the filter when done
                }
            });
            tintAnimator.start();
        }
    }

    private void updateScore(int points) {
        int oldScore = score;
        score = Math.max(0, score + points);

        // Animate score change
        ValueAnimator scoreAnimator = ValueAnimator.ofInt(oldScore, score);
        scoreAnimator.setDuration(300);
        scoreAnimator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            scoreText.setText("Score: " + animatedValue);
        });
        scoreAnimator.start();

        // Scale animation for score text
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(scoreText, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(scoreText, "scaleY", 1f, 1.2f, 1f);

        AnimatorSet scoreSet = new AnimatorSet();
        scoreSet.playTogether(scaleX, scaleY);
        scoreSet.setDuration(300);
        scoreSet.setInterpolator(new OvershootInterpolator());
        scoreSet.start();
    }

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
            updateScore(50); // Completion bonus

            if (countDownTimer != null) {
                countDownTimer.cancel();
            }

            // Victory animation
            animateVictory();
        }
    }

    private void animateVictory() {
        // Animate all bins in celebration
        ImageView[] bins = {
                findViewById(R.id.paperBin),
                findViewById(R.id.organicBin),
                findViewById(R.id.metalBin),
                findViewById(R.id.plasticBin)
        };

        for (int i = 0; i < bins.length; i++) {
            int finalI = i;
            bins[i].postDelayed(() -> {
                ObjectAnimator jump = ObjectAnimator.ofFloat(bins[finalI], "translationY", 0f, -30f, 0f);
                jump.setDuration(600);
                jump.setInterpolator(new BounceInterpolator());
                jump.start();
            }, i * 100);
        }

        // Delayed transition to quiz
        findViewById(android.R.id.content).postDelayed(this::goToQuiz, 1500);
    }

    private void showFeedbackImage(int resId) {
        feedbackImage.setImageResource(resId);
        feedbackImage.setVisibility(View.VISIBLE);
        feedbackImage.setAlpha(0f);
        feedbackImage.setScaleX(0.3f);
        feedbackImage.setScaleY(0.3f);

        // Pop-in animation
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(feedbackImage, "scaleX", 0.3f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(feedbackImage, "scaleY", 0.3f, 1.2f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(feedbackImage, "alpha", 0f, 1f);

        AnimatorSet popIn = new AnimatorSet();
        popIn.playTogether(scaleX, scaleY, alpha);
        popIn.setDuration(300);
        popIn.setInterpolator(new OvershootInterpolator());
        popIn.start();

        // Auto-hide after delay
        feedbackImage.postDelayed(() -> {
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(feedbackImage, "alpha", 1f, 0f);
            ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(feedbackImage, "scaleX", 1f, 0.3f);
            ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(feedbackImage, "scaleY", 1f, 0.3f);

            AnimatorSet popOut = new AnimatorSet();
            popOut.playTogether(fadeOut, scaleOutX, scaleOutY);
            popOut.setDuration(200);
            popOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    feedbackImage.setVisibility(View.GONE);
                    feedbackImage.setAlpha(1f);
                    feedbackImage.setScaleX(1f);
                    feedbackImage.setScaleY(1f);
                }
            });
            popOut.start();
        }, 1200);
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                timerText.setText("Time: " + seconds);

                // Add urgency animation when time is running low
                if (seconds <= 5 && seconds > 0) {
                    ObjectAnimator pulse = ObjectAnimator.ofFloat(timerText, "scaleX", 1f, 1.1f, 1f);
                    ObjectAnimator pulseY = ObjectAnimator.ofFloat(timerText, "scaleY", 1f, 1.1f, 1f);
                    AnimatorSet pulseSet = new AnimatorSet();
                    pulseSet.playTogether(pulse, pulseY);
                    pulseSet.setDuration(200);
                    pulseSet.start();

                    // Change color to red for urgency
                    timerText.setTextColor(Color.RED);
                } else if (seconds > 5) {
                    timerText.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onFinish() {
                goToQuiz();
            }
        }.start();
    }

    private void goToQuiz() {
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
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}