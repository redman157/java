package com.jvit.companycoin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jvit.companycoin.R;

public class ChangePassSuccessActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnBack;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_success);
        btnBack = findViewById(R.id.btnChangePasswordSucces);
        imgBack = findViewById(R.id.img_backChangePasswordSucces);
        btnBack.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnChangePasswordSucces:
            case R.id.img_backChangePasswordSucces:
                startActivity(new Intent(ChangePassSuccessActivity.this, MyPageActivity.class));
                break;
        }
    }
}
