package com.jvit.companycoin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jvit.companycoin.R;
import com.jvit.companycoin.fragment.SendCoinsFragment;

public class SendCoinSuccessActivity extends AppCompatActivity {
    private Button btnSendCoin;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendcoinsucces);
        btnSendCoin = findViewById(R.id.btnSendCoinSucces);
        btnBack = findViewById(R.id.img_backmypageSendcoin);
        btnSendCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
