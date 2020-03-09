package com.jvit.companycoin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.ChangePassword;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity implements Callback<ChangePassword> {

    private Button btnForget;
    private TextView textBack;
    private SharedPreferences preferencesToken;
    private String token;
    private EditText editForget;
    private LinearLayout forgetPassActivity;
    private ApiClient apiClient;
    private String CALLBACK_URL = "https://mobile1.companycoin.net/vi/reset-password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        btnForget = findViewById(R.id.btnAcceptForgetPass);
        editForget = findViewById(R.id.editForgetPass);
        textBack = findViewById(R.id.textBackLoginForget);
        forgetPassActivity = findViewById(R.id.foggetPassActivity);
        setupHiddenKeyBoard(forgetPassActivity);
        preferencesToken = getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN, HomeFragment.TOKEN_NULL);
        apiClient = ApiService.getRetrofit().create(ApiClient.class);

        apiForgetPassword();
        textBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResponse(Call<ChangePassword> call, Response<ChangePassword> response) {
        ChangePassword forgetPass = response.body();
        if (forgetPass != null){
            btnForget.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }

    private void apiForgetPassword(){
        apiClient.FORGET_PASSWORD_CALL(
                "Bearer " + token,
                editForget.getText().toString(),
                CALLBACK_URL).enqueue(this);
    }

    public void setupHiddenKeyBoard(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    LoginActivity.hideSoftKeyboard(ForgetPasswordActivity.this);
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
    @Override
    public void onFailure(Call<ChangePassword> call, Throwable t) {
        call.cancel();

    }
}
