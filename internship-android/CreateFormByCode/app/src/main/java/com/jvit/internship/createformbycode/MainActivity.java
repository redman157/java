package com.jvit.internship.createformbycode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    static int MATCH_PARENT =  LinearLayout.LayoutParams.MATCH_PARENT;
    static int WRAP_CONTENT = LinearLayout.LayoutParams.WRAP_CONTENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.infoLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        // TextWelCome dòng 1
        TextView textWelcome = new TextView(this);
        textWelcome.setText("Welcome");
        textWelcome.setLayoutParams(setView(MATCH_PARENT,WRAP_CONTENT,0,0,70,0,0));
        textWelcome.setGravity(Gravity.CENTER);
        textWelcome.setTextSize(40);
        textWelcome.setTextColor(getResources().getColor(R.color.Blue));

        // TextIsText dòng 2
        LinearLayout linearIsText = new LinearLayout(this);
        linearIsText.setWeightSum(10);
        linearIsText.setOrientation(LinearLayout.HORIZONTAL);
        TextView textIsText = new TextView(this);
        textIsText.setLayoutParams(new LinearLayout.LayoutParams(0,WRAP_CONTENT, 7));
        textIsText.setText("This is text");
        textIsText.setTextSize(20);
        textIsText.setGravity(Gravity.RIGHT);
        linearIsText.addView(textIsText);

        // dòng full name, phone address
        LinearLayout fullName = custom("FullName",20);
        LinearLayout phone = custom("Phone",20);
        LinearLayout addres = custom("Address",20);

        /*
        linear chứa 2 bottom
        * */
        LinearLayout linearButton = new LinearLayout(this);
        linearButton.setWeightSum(10);
        linearButton.setOrientation(LinearLayout.HORIZONTAL);

        // button register
        Button btnRegister = new Button(this);
        btnRegister.setLayoutParams(setView(WRAP_CONTENT,WRAP_CONTENT,7,32,40,16,16));
        btnRegister.setBackgroundColor(getResources().getColor(R.color.Red));
        btnRegister.setText("Register");
        btnRegister.setTextColor(getResources().getColor(R.color.White));


        // button login
        Button btnLogin = new Button(this);
        btnLogin.setLayoutParams(setView(WRAP_CONTENT,WRAP_CONTENT,3,16,40,32,16));
        btnLogin.setText("Login");
        btnLogin.setBackgroundColor(getResources().getColor(R.color.Green));
        btnLogin.setTextColor(getResources().getColor(R.color.White));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        // add 2 button trong linear
        linearButton.addView(btnRegister);
        linearButton.addView(btnLogin);

        // add linear tổng
        linearLayout.addView(textWelcome);
        linearLayout.addView(linearIsText);
        linearLayout.addView(fullName);
        linearLayout.addView(phone);
        linearLayout.addView(addres);
        linearLayout.addView(linearButton);
    }

    private LinearLayout.LayoutParams setView(int Width, int Height, float Weight, int left, int top, int right, int bottom  ){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Width, Height, Weight);
        layoutParams.setMargins(left,top,right,bottom);
        return layoutParams;
    }
    private LinearLayout custom(String Title, int Size){
        LinearLayout linearCustom = new LinearLayout(this);
        linearCustom.setWeightSum(10);
        linearCustom.setOrientation(LinearLayout.HORIZONTAL);
        // khởi tạo
        TextView textTitle = new TextView(this);
        EditText editText = new EditText(this);

        textTitle.setLayoutParams(setView(0,WRAP_CONTENT,3,32,0,0,0));
        textTitle.setText(Title);
        textTitle.setTextSize(Size);

        editText.setLayoutParams(setView(0,WRAP_CONTENT,7,0,0,32,0));
        linearCustom.addView(textTitle);
        linearCustom.addView(editText);
        return linearCustom;
    }
}

