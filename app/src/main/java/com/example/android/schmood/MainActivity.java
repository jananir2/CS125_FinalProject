package com.example.android.schmood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    public SeekBar seekEmotion;
    public SeekBar seekEnergy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekEmotion = findViewById(R.id.seekBarEmotion);
        seekEnergy = findViewById(R.id.seekBarEnergy);
        final Intent intent = new Intent(MainActivity.this, DisplaySong.class);


        Button enterSong = findViewById(R.id.btnSchmood);
        enterSong.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String emotion = Integer.toString(seekEmotion.getProgress() * 100000);
                String energy = Integer.toString(seekEnergy.getProgress() * 100000);
                intent.putExtra("intFeel", emotion);
                intent.putExtra("intEnergy", energy);
                startActivity(intent);
            }
        });
    }
}
