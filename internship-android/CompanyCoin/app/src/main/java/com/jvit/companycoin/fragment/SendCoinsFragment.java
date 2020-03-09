package com.jvit.companycoin.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jvit.companycoin.api.Coin;
import com.jvit.companycoin.activity.LoginActivity;
import com.jvit.companycoin.activity.SuggestActivity;
import com.jvit.companycoin.adapter.FindUserAdapter;
import com.jvit.companycoin.object.FindUser;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.FindUserKeyword;
import com.jvit.companycoin.activity.SendCoinSuccessActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendCoinsFragment extends Fragment{
    public SendCoinsFragment(){}
    private AutoCompleteTextView editEmail;
    private FindUserAdapter findUserAdapter;
    private ArrayList<FindUser> findUserArrayList;
    private EditText editComment, editSendCoin;
    private TextView textComment, textCheckCoin, textCheckEmail, textCheck, textCheckComment;
    private Button btnSendCoin;
    private CheckBox checkBox;
    private View view;
    private LinearLayout sendFragment;
    public static TextView textCoinLimit;
    public static int coinUser;

    private String isEmail = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
    private Pattern pattern;
    public static ApiClient apiClient;
    private String token;
    private SharedPreferences preferencesToken;

    public static boolean tradeHistory;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            return view;
        }
        view = inflater.inflate(R.layout.fragment_send,container,false);
        initView();

        setupHiddenKeyBoard(sendFragment);
        preferencesToken = getActivity().getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN, HomeFragment.TOKEN_NULL);
        apiClient = ApiService.getRetrofit().create(ApiClient.class);

        if (findUserArrayList == null){
            findUserArrayList = new ArrayList<>();
            apiFindUser();
        }

        pattern = Pattern.compile(isEmail);

        renderCoinLimit();

        checkComment();
        checkSendCoin();
        checkEmail();

        clickSendCoin();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void renderCoinLimit() {
        if (Integer.valueOf(HomeFragment.coinUser) < SuggestActivity.coinUser) {
            textCoinLimit.setText("/"+SuggestActivity.coinUser+" "+getActivity().getResources().getString(R.string.coin));
        }else {
            textCoinLimit.setText("/"+HomeFragment.coinUser+" "+getActivity().getResources().getString(R.string.coin));
        }
    }

    private void checkComment() {
        editComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textComment.setText(charSequence.length()+"/300");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void checkEmail() {
        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = editEmail.getText().toString();
                if (!email.equals("") && isEmail(email)){
                    textCheckEmail.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void clickSendCoin() {
        btnSendCoin.setOnClickListener(new View.OnClickListener() {
            String email = editEmail.getText().toString();
            String token_user = editSendCoin.getText().toString();

            @Override
            public void onClick(View view) {
                boolean done = true;
                email = editEmail.getText().toString();
                token_user = editSendCoin.getText().toString();

                if (email.equals("") || !isEmail(email)){
                    textCheckEmail.setText(getActivity().getResources().getString(R.string.email_is_required));
                    done = false;
                }
                if (token_user.equals("")) {
                    textCheckCoin.setText(getActivity().getResources().getString(R.string.token_is_required));
                    done = false;
                }
                if (!checkBox.isChecked()) {
                    textCheck.setText(getActivity().getString(R.string.please_check_box));
                    done = false;
                }
                if (checkBox.isChecked()){
                    textCheck.setText("");
                }
                if (editComment.getText().toString().length() <30
                        && editComment.getText().toString().length()> 0){
                    textCheckComment.setText(getActivity().getString(R.string.please_comment_check));
                    done = false;
                }

                if (done) {
                    apiSendCoin();
                    sendCoinSuccess();
                }
            }
        });
    }

    private void checkSendCoin() {
        editSendCoin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0  && Integer.valueOf(editSendCoin.getText().toString()) >
                        Integer.valueOf(
                                Integer.valueOf(textCoinLimit.getText().toString().split(" ")[0].substring(1)))){
                    editSendCoin.setText(String.valueOf(Integer.valueOf(Integer.valueOf(textCoinLimit.getText().toString().split(" ")[0].substring(1)))));
                }
                if (!editSendCoin.getText().toString().equals("")) {
                    textCheckCoin.setText("");
                }
                if (editSendCoin.getText().toString().equals("0")){
                    textCheckCoin.setText("Vui lòng nhập coin lớn hơn hoặc bằng 1");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupHiddenKeyBoard(View view) {

          if (!(view instanceof EditText)) {
              view.setOnTouchListener(new View.OnTouchListener() {
                  public boolean onTouch(View v, MotionEvent event) {
                      LoginActivity.hideSoftKeyboard(getActivity());
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
    private boolean isEmail(String email) {
        String regExpn ="^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

        Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    private void sendCoinSuccess(){
        textCheckCoin.setText("");
        textCheck.setText("");
        textCheckComment.setText("");
        textCheckEmail.setText("");
        editComment.setText("");
        editEmail.setText("");
        editSendCoin.setText("");
        checkBox.setChecked(false);
    }
    private void initView(){
        textCheckComment = view.findViewById(R.id.textErrorCommentSugget);
        sendFragment = view.findViewById(R.id.sendFragment);
        textCoinLimit = view.findViewById(R.id.textLimitCoinSendCoin);
        textCheck = view.findViewById(R.id.textCheckBoxSendCoin);
        checkBox = view.findViewById(R.id.checkboxSendCoin);
        textCheckCoin = view.findViewById(R.id.textCheckCoinSendCoin);
        textCheckEmail = view.findViewById(R.id.textCheckMailSendCoin);
        editSendCoin = view.findViewById(R.id.editCoinSendCoin);
        editComment = view.findViewById(R.id.editCommentSendCoin);
        editEmail = view.findViewById(R.id.editInputMailSendCoin);
        textComment = view.findViewById(R.id.textLimitCommentSendCoin);
        btnSendCoin = view.findViewById(R.id.btnSendCoinFragment);
        checkBox = view.findViewById(R.id.checkboxSendCoin);
    }

    private void apiFindUser() {
        apiClient.FIND_USER_CALL("Bearer " + token).enqueue(new Callback<FindUserKeyword>() {
            @Override
            public void onResponse(Call<FindUserKeyword> call, Response<FindUserKeyword> response) {
                FindUserKeyword findUserKeyword = response.body();
                if (findUserKeyword != null) {
                    List<FindUserKeyword.DataUser> findUserList = findUserKeyword.getDataUserList();
                    for (FindUserKeyword.DataUser dataUser : findUserList) {
                        findUserArrayList.add(new FindUser(dataUser.getAvatar_path(), dataUser.getName(),
                                dataUser.getEmail()));
                    }
                }
                findUserAdapter = new FindUserAdapter(getActivity(), R.layout.item_finduser, findUserArrayList);
                editEmail.setAdapter(findUserAdapter);
            }

            @Override
            public void onFailure(Call<FindUserKeyword> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void apiSendCoin(){
        apiClient.COIN_CALL(
                "Bearer " +token,
                editEmail.getText().toString(),
                Integer.valueOf(editSendCoin.getText().toString()),
                editComment.getText().toString()
        ).enqueue(new Callback<Coin>() {
            @Override
            public void onResponse(Call<Coin> call, Response<Coin> response) {
                Coin coin = response.body();
                if (coin != null) {
                    SendCoinsFragment.coinUser = coin.getData().getToken_amount();
                    textCoinLimit.setText("/" + coinUser + " " + getActivity().getResources().getString(R.string.coin));
                    HomeFragment.textCoin.setText(""+coinUser);

                    startActivity(new Intent(getActivity(), SendCoinSuccessActivity.class));
                }else {
                    renderCoinLimit();
                }
            }

            @Override
            public void onFailure(Call<Coin> call, Throwable t) {
                call.cancel();

            }
        });
    }
}
