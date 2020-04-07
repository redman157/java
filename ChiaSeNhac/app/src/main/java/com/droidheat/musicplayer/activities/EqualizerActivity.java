package com.droidheat.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.droidheat.musicplayer.R;

public class EqualizerActivity extends AppCompatActivity implements OnClickListener {
    private ImageButton imb_BackMusic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);
        initView();
        assignView();
    }
    private void initView(){
        imb_BackMusic = findViewById(R.id.imb_BackMusic);
    }
    private void assignView(){
        imb_BackMusic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imb_BackMusic:
                startActivity(new Intent(this, HomeActivity.class));
                break;
        }
    }
}
