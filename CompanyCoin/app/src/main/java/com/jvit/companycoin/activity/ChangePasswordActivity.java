package com.jvit.companycoin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.ChangePassword;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textCheckPassPresent, textCheckPassNew, textCheckPassNewAgain;
    private EditText editPassPresent, editNewPass, editAgainNewPass;
    private Button btnSend;
    private ImageView btnBack;
    private ApiClient apiClient;
    private LinearLayout changePassActivity;
    private String token;
    private SharedPreferences preferencesToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initView();
        autoCheckPassword();
        setupHiddenKeyBoard(changePassActivity);
        preferencesToken = getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN,HomeFragment.TOKEN_NULL);

        apiClient = ApiService.getRetrofit().create(ApiClient.class);
        btnBack.setOnClickListener(this);
        btnSend.setOnClickListener(this);
    }
    private void autoCheckPassword(){
        editNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >8 || !s.toString().equals("")){
                    textCheckPassNew.setText("");

                }
                if (s.length()>8){
                    editNewPass.setBackground(getResources().getDrawable(R.drawable.custom_edit_stoken));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editPassPresent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 8 || !s.toString().equals("")){
                    textCheckPassPresent.setText("");

                }
                if (s.length()>=8){
                    editPassPresent.setBackground(getResources().getDrawable(R.drawable.custom_edit_stoken));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editAgainNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 8 || !s.toString().equals("")){
                    textCheckPassNewAgain.setText("");

                }
                if (s.length()>= 8){
                    editAgainNewPass.setBackground(getResources().getDrawable(R.drawable.custom_edit_stoken));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    public void setupHiddenKeyBoard(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    LoginActivity.hideSoftKeyboard(ChangePasswordActivity.this);
                    return false;
                }
            });
        }

        //If apiClient layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupHiddenKeyBoard(innerView);
            }
        }
    }
    private void initView(){
        changePassActivity = findViewById(R.id.changePassActivity);
        editPassPresent = findViewById(R.id.editPresentPassReset);
        editNewPass = findViewById(R.id.editNewPassReset);
        editAgainNewPass = findViewById(R.id.editAgainNewPassReset);
        btnBack = findViewById(R.id.img_backResetPassword);
        btnSend = findViewById(R.id.btnSendResetPassword);
        textCheckPassNew = findViewById(R.id.textCheckPassNewReset);
        textCheckPassNewAgain = findViewById(R.id.textCheckAgainPassReset);
        textCheckPassPresent = findViewById(R.id.textCheckPassPresentReset);
    }
    @Override

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSendResetPassword:
                boolean done = true;
                if (editPassPresent.getText().toString().equals("")){
                    textCheckPassPresent.setText(getResources().getString(R.string.new_password));
                    editPassPresent.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_change_pass));
                    done = false;
                }
                if (editNewPass.getText().toString().equals("")){
                    textCheckPassNew.setText(getResources().getString(R.string.new_password));
                    editNewPass.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_change_pass));
                    done = false;
                }
                if (editAgainNewPass.getText().toString().equals("")){
                    textCheckPassNewAgain.setText(getResources().getString(R.string.password_confirmation));
                    editAgainNewPass.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_change_pass));
                    done = false;
                }
                if (editPassPresent.getText().toString().length()< 8 &&editPassPresent.getText().toString().length() >= 1 ){
                    textCheckPassPresent.setText(getResources().getString(R.string.password_must));
                    editPassPresent.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_change_pass));
                    done = false;
                }
                if (editNewPass.getText().toString().length()< 8 &&editPassPresent.getText().toString().length() >= 1){
                    textCheckPassNew.setText(getResources().getString(R.string.new_password));
                    editNewPass.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_change_pass));
                    done = false;
                }
                if (editAgainNewPass.getText().toString().length()< 8&&editPassPresent.getText().toString().length() >= 1){
                    textCheckPassNewAgain.setText(getResources().getString(R.string.new_password_must));
                    editAgainNewPass.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_change_pass));
                    done = false;
                }

                if (!editAgainNewPass.getText().toString().equals(editNewPass.getText().toString())){
                    textCheckPassNewAgain.setText(getResources().getString(R.string.compare_password));
                    editNewPass.setBackground(getResources().getDrawable(R.drawable.custom_edit_error_change_pass));
                    done = false;
                }
                if (done){
                    apiChangePassword();
                }
                break;
            case R.id.img_backResetPassword:
                finish();
                break;
        }
    }
    private void apiChangePassword(){
        final Call<ChangePassword> changePasswordCall =
                apiClient.CHANGE_PASSWORD_CALL("Bearer " + token,
                        editPassPresent.getText().toString(),
                        editNewPass.getText().toString());
        changePasswordCall.enqueue(new Callback<ChangePassword>() {
            @Override
            public void onResponse(Call<ChangePassword> call, Response<ChangePassword> response) {
                ChangePassword changePassword = response.body();
                if (changePassword!= null){
                    startActivity(new Intent(ChangePasswordActivity.this,
                            ChangePassSuccessActivity.class));
                }
            }

            @Override
            public void onFailure(Call<ChangePassword> call, Throwable t) {
                call.cancel();

            }
        });
    }

}
