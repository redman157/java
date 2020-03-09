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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.SendComment;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.fragment.SendCoinsFragment;
import com.jvit.companycoin.R;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestActivity extends AppCompatActivity implements Callback<SendComment> {
    private TextView textLimit, textSendContent, textCheckComment, textName;
    private EditText editComment;
    private ImageView imgBack, imgAvatar;
    private ApiClient apiClient;
    private SharedPreferences preferencesToken;
    private String token;

    public static int coinUser;
    private LinearLayout suggestActivity;

    private boolean reloadSendIdea = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);

        initView();

        preferencesToken = getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN, HomeFragment.TOKEN_NULL);
        apiClient = ApiService.getRetrofit().create(ApiClient.class);

        setupHiddenKeyBoard(suggestActivity);
        Picasso.get().load(ApiService.url_path+ HomeFragment.avatar).into(imgAvatar);
        textName.setText(HomeFragment.name);
        editComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textLimit.setText(charSequence.length() +"/1000");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        textSendContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editComment.getText().toString().length() < 30){
                    textCheckComment.setText(getString(R.string.check_content));
                } else if (editComment.getText().toString().isEmpty()){
                    textCheckComment.setText(getString(R.string.please_enter_content));
                } else {
                    apiSuggestIdea();
                }
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void apiSuggestIdea() {
        apiClient.SEND_COMMENT_CALL("Bearer "+token,
                editComment.getText().toString())
                .enqueue(SuggestActivity.this);
    }

    private void setupHiddenKeyBoard(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    LoginActivity.hideSoftKeyboard(SuggestActivity.this);
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
        suggestActivity = findViewById(R.id.suggestActivity);
        textName = findViewById(R.id.textNameMySuggest);
        imgAvatar = findViewById(R.id.imgAvataMySuggest);
        textCheckComment = findViewById(R.id.textCheckCommentSuggest);
        textSendContent = findViewById(R.id.textSendCommentSuggest);
        editComment = findViewById(R.id.editCommentMySuggest);
        textLimit = findViewById(R.id.textNumLimitMySuggest);
        imgBack = findViewById(R.id.img_backSuggest);
    }

    @Override
    public void onResponse(Call<SendComment> call, Response<SendComment> response) {
        SendComment sendComment = response.body();
        if (sendComment!= null) {
            SendComment.Data data = sendComment.getData();
            coinUser = data.getToken();

            HomeFragment.textCoin.setText("" + coinUser);
            if (SendCoinsFragment.textCoinLimit!= null) {
                SendCoinsFragment.textCoinLimit.setText("/" + coinUser + " " + getResources().getString(R.string.coin));
            }
            reloadSendIdea = true;
            Intent intent = new Intent();
            intent.putExtra("reload",reloadSendIdea);
            setResult(RESULT_OK, intent);
            finish();
        }
    }



    @Override
    public void onFailure(Call<SendComment> call, Throwable t) {
        call.cancel();

    }

}
