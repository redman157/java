package com.jvit.companycoin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.Login;
import com.jvit.companycoin.fragment.HomeFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText editUser, editPass;
    Button btnLogin;
    TextView textForget;
    public static SharedPreferences preferencesToken;
    private SharedPreferences.Editor editorToken;
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[a-z]).{6,}$";
    private static final String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private ApiClient apiClient;
    private TextView textCheckUser, textCheckPass;
    private final String SAVE_TOKEN = "SAVE_TOKEN";
    private String token = null;
    private final String SAVE_LOGIN = "SAVE_LOGIN";
    private final String EMAIL = "email";
    private final String PASSWORD = "password";
    private final String EMAIL_NULL = "email_null";
    private final String PASSWORD_NULL = "password_null";
    public final String CHECKLOGIN = "check_login";
    public static SharedPreferences preferencesSaveLogin;
    public static SharedPreferences.Editor editorSaveLogin;
    private CheckBox checkRemember;
    private boolean checkLogin = false;
    private LinearLayout loginActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        apiClient = ApiService.getRetrofit().create(ApiClient.class);

        preferencesSaveLogin = getSharedPreferences(SAVE_LOGIN, MODE_PRIVATE);
        editorSaveLogin = preferencesSaveLogin.edit();
//        Context context = LocaleHelper.setLocale(LoginActivity.this,SwitchLanguageActivity.language);
        setupHiddenKeyBoard(loginActivity);

        if (!preferencesSaveLogin.getString(EMAIL, EMAIL_NULL).equals(EMAIL_NULL)){
            apiUserLogin(preferencesSaveLogin.getString(EMAIL,EMAIL_NULL),
                    preferencesSaveLogin.getString(PASSWORD,EMAIL_NULL));

            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
        }
        editUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = editUser.getText().toString();
                if (email.equals("") || isEmailValid(email)){
                    textCheckUser.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String pass = editPass.getText().toString();
                if (!pass.equals("")){
                    textCheckPass.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            String email = editUser.getText().toString();
            String pass = editPass.getText().toString();
            @Override
            public void onClick(View view) {

                boolean done = true;
                email = editUser.getText().toString();
                pass = editPass.getText().toString();
                if (email.equals("") || !isEmailValid(email)){
                    textCheckUser.setText(R.string.email_is_required);
                    done = false;
                }
                if (pass.equals("")){
                    textCheckPass.setText(R.string.check_pass);
                    done = false;
                }

                if (done) {
                    apiUserLogin(email,pass);
                }

            }
        });
        textForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    private void initView() {
        checkRemember = findViewById(R.id.checkboxRememberLogin);
        textCheckPass = findViewById(R.id.textCheckPassLogin);
        textCheckUser = findViewById(R.id.textCheckEMailLogin);
        textForget = findViewById(R.id.textForget);
        editUser = findViewById(R.id.editUser);
        editPass = findViewById(R.id.editPass);
        btnLogin = findViewById(R.id.btnLogin);
        loginActivity = findViewById(R.id.loginActivity);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (checkLogin){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            apiUserLogin(preferencesSaveLogin.getString(EMAIL,EMAIL_NULL),
                    preferencesSaveLogin.getString(PASSWORD,PASSWORD_NULL));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLogin){
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            apiUserLogin(preferencesSaveLogin.getString(EMAIL,EMAIL_NULL),
                    preferencesSaveLogin.getString(PASSWORD,PASSWORD_NULL));
        }
    }

    private void loginSuccess(){
        editPass.setText("");
        editUser.setText("");
        checkRemember.setChecked(false);
    }

    private boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    private boolean isPassword(String pass){
        CharSequence inputStr = pass;

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches())
            return true;
        else
            return false;
    }

    private void apiUserLogin(String email, String password){
        Call<Login> loginResponseCall = apiClient.LOGIN_CALL(email , password);
        loginResponseCall.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                Login login = response.body();

                if (login != null) {
                    token = login.getCreateToken().getToken();
                    preferencesToken = getSharedPreferences(SAVE_TOKEN, MODE_PRIVATE);
                    editorToken = preferencesToken.edit();
                    editorToken.putString(HomeFragment.TOKEN, token);
                    editorToken.apply();
                    textCheckPass.setText("");

                    if (editUser.getText().toString().length() > 0) {
                        if (checkRemember.isChecked()) {
                            preferencesSaveLogin = getSharedPreferences(SAVE_LOGIN, MODE_PRIVATE);
                            editorSaveLogin = preferencesSaveLogin.edit();
                            editorSaveLogin.putString(EMAIL, editUser.getText().toString());
                            editorSaveLogin.putString(PASSWORD,editPass.getText().toString());
                            editorSaveLogin.apply();
                            checkLogin = true;
                        } else {
                            checkLogin = false;
                        }
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);

                        if (!checkLogin) {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            loginSuccess();
                        }
                    }
                } else {
                    textCheckPass.setText(getResources().getString(R.string.login_faild));
                }

            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                call.cancel();

            }
        });
    }

    private void setupHiddenKeyBoard(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(LoginActivity.this);
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
    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }


    /*private void showDialog() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit);
        TextView textTitle = dialog.findViewById(R.id.textTitleDiaglogExit);
        TextView btnYes = dialog.findViewById(R.id.btnYesDiaglogExit);
        TextView btnNo = dialog.findViewById(R.id.btnNoDiaglogExit);

        textTitle.setText(getResources().getString(R.string.exit_application));
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        showDialog();
    }*/
}




