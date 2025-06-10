package com.sharwin.recyclegame;



import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MusicManager.startBackgroundMusic(this);

        LinearLayout playBtn = findViewById(R.id.playButton);
        LinearLayout exitBtn = findViewById(R.id.exitButton);
        LinearLayout infoBtn = findViewById(R.id.infoButton);  // Add this line

        playBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecycleVideoActivity.class);
            startActivity(intent);
        });

        exitBtn.setOnClickListener(v -> finish());

        // Add the listener for your info button here
        infoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InstructionActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicManager.stopBackgroundMusic();
    }

}




