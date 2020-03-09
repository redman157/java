package com.jvit.internship.createformbycode;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    RelativeLayout layout;
    Resources res;
    RelativeLayout.LayoutParams imgRule, userRule, passRule, checkBoxRule, textForgetRule, btnLoginRule, textAccountRule, textCreateRule;
    static int MATCH_PARENT =  RelativeLayout.LayoutParams.MATCH_PARENT;
    static int WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        layout = findViewById(R.id.relaLayout);
        AnhXa();
        // image view
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.marvel);
        imageView.setId(R.id.imgHinh);
        imgRule.addRule(RelativeLayout.BELOW, layout.getId());
        imgRule.addRule(RelativeLayout.ALIGN_PARENT_LEFT, layout.getId());
        imgRule.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, layout.getId());
        imageView.setLayoutParams(imgRule);
        layout.addView(imageView);

        // edit User
        EditText editUser = new EditText(this);
        userRule.addRule(RelativeLayout.BELOW, imageView.getId());
        userRule.addRule(RelativeLayout.CENTER_HORIZONTAL);
        userRule.setMargins(12,12,12,12);

        editUser.setId(R.id.edtUser);
        editUser.setBackgroundResource(R.drawable.custom_edit);
        editUser.setLayoutParams(userRule);
        editUser.setHint("Enter UserName");
        editUser.setGravity(Gravity.CENTER);
        editUser.setHintTextColor(getResources().getColor(R.color.White));
        layout.addView(editUser);

        // edit password
        EditText editPass = new EditText(this);
        passRule.addRule(RelativeLayout.BELOW, editUser.getId());
        passRule.addRule(RelativeLayout.CENTER_HORIZONTAL);
        passRule.setMargins(12,12,12,12);

        editPass.setId(R.id.edtPass);
        editPass.setBackgroundResource(R.drawable.custom_edit);
        editPass.setLayoutParams(passRule);
        editPass.setHint("Enter PassWord");
        editPass.setGravity(Gravity.CENTER);
        editPass.setHintTextColor(getResources().getColor(R.color.White));
        layout.addView(editPass);

        // checkbox
        CheckBox checkBox = new CheckBox(this);
        checkBoxRule.addRule(RelativeLayout.BELOW, editPass.getId());
        checkBoxRule.addRule(RelativeLayout.ALIGN_LEFT, editPass.getId());
        checkBoxRule.setMargins(0,40,0,40);
        checkBox.setText("Remember Me");
        checkBox.setTextColor(getResources().getColor(R.color.White));
        checkBox.setTextSize(15);
        checkBox.setLayoutParams(checkBoxRule);

        layout.addView(checkBox);

        // text forget pass dòng 3
        TextView textForget = new TextView(this);
        textForgetRule.addRule(RelativeLayout.BELOW, editPass.getId());
        textForgetRule.addRule(RelativeLayout.ALIGN_RIGHT, editPass.getId());

        textForgetRule.setMargins(0,57,0,40);

        textForget.setText("Forget your password?");
        textForget.setId(R.id.txtForget);
        textForget.setTextColor(getResources().getColor(R.color.White));
        textForget.setTextSize(15);
        textForget.setLayoutParams(textForgetRule);
        layout.addView(textForget);

        //Button Login
        Button btnLogin = new Button(this);
        btnLoginRule.addRule(RelativeLayout.BELOW, textForget.getId());
        btnLoginRule.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btnLoginRule.setMargins(0,20,0,0);

        btnLogin.setText("Login");
        btnLogin.setPadding(10,10,10,10);
        btnLogin.setAllCaps(false);
        btnLogin.setTextColor(getResources().getColor(R.color.White));
        btnLogin.setTextSize(20);
        btnLogin.setId(R.id.btnLogin);
        btnLogin.setBackgroundResource(R.drawable.custom_button);
        btnLogin.setLayoutParams(btnLoginRule);
        layout.addView(btnLogin);

        // text have account
        TextView textHave = new TextView(this);
        textAccountRule.addRule(RelativeLayout.BELOW, btnLogin.getId());
        textAccountRule.addRule(RelativeLayout.ALIGN_LEFT, btnLogin.getId());
        textAccountRule.setMargins(70,80,0,0);

        textHave.setId(R.id.txtAccount);
        textHave.setText("Have not account yet?");
        textHave.setTextSize(15);
        textHave.setTextColor(getResources().getColor(R.color.White));
        textHave.setLayoutParams(textAccountRule);
        layout.addView(textHave);

        // text create
        TextView textCreate = new TextView(this);
        textCreateRule.addRule(RelativeLayout.BELOW, btnLogin.getId());
        textCreateRule.addRule(RelativeLayout.RIGHT_OF, textHave.getId());
        textCreateRule.setMargins(0,80,0,0);

        textCreate.setId(R.id.txtCreate);
        textCreate.setText("Create Account");
        textCreate.setTextSize(15);
        textCreate.setTextColor(getResources().getColor(R.color.Blue_Reu));
        textCreate.setLayoutParams(textCreateRule);
        layout.addView(textCreate);
    }

    private void AnhXa(){
        imgRule = new RelativeLayout.LayoutParams(500,700);
        userRule=  new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
        passRule = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
        checkBoxRule = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
        textAccountRule = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
        textCreateRule = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
        textForgetRule = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
        btnLoginRule = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
    }
}
