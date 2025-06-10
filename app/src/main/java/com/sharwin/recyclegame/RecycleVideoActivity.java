package com.sharwin.recyclegame;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class RecycleVideoActivity extends AppCompatActivity {




    VideoView videoView;
    Button skipButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle_video);

        // Pause background music when entering video activity
        MusicManager.pauseMusic();

        videoView = findViewById(R.id.videoView);
        skipButton = findViewById(R.id.skipButton);

        // Load video from raw folder
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.recycle_video;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.start();

        // Skip button listener
        skipButton.setOnClickListener(v -> {
            MusicManager.resumeMusic(); // Resume background music
            Intent intent = new Intent(RecycleVideoActivity.this, GameActivity.class); // Change to your game activity
            startActivity(intent);
            finish();
        });

        // Auto move to game after video ends
        videoView.setOnCompletionListener(mp -> {
            MusicManager.resumeMusic(); // Resume background music
            Intent intent = new Intent(RecycleVideoActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
