package com.jvit.companycoin.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jvit.companycoin.R;

import org.w3c.dom.Text;

import java.util.Locale;

import io.paperdb.Paper;

public class SwitchLanguageActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textJapanese, textEnglish;
    public final static String SWITCH_LANGUAGE = "SWITCH_LANGUAGE";
    public static String language;
    private ImageView imgBack;
    private LinearLayout switchJapanese, switchEnglish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_switch_language);
        initView();
    /*    Paper.init(this);
        String saveLanguage = Paper.book().read("language");
        if (saveLanguage == null){
            Paper.book().write("language","en");
        }
        updateView((String)Paper.book().read("language"));
*/
        switchEnglish.setOnClickListener(this);
        switchJapanese.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    private void initView() {
        switchJapanese = findViewById(R.id.switchJapanese);
        switchEnglish = findViewById(R.id.switchEnglish);
        textJapanese = findViewById(R.id.textJapanese);
        textEnglish  = findViewById(R.id.textEnglish);
        imgBack = findViewById(R.id.img_backSwitchLanguage);
    }
    private void updateView(String language){
        Context context = LocaleHelper.setLocale(this,language);
        Resources resources = context.getResources();

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.switchJapanese:
                language ="jp";
                setAppLocale(language);
//                updateView(language);
                Toast.makeText(this, "Locale to Japanese !", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
                break;
            case R.id.switchEnglish:
                language = "en";
                setAppLocale(language);
//                updateView(language);
                Toast.makeText(this, "Locale to English !", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());
                break;
            case R.id.img_backSwitchLanguage:
                startActivity(new Intent(SwitchLanguageActivity.this, MyPageActivity.class));
                break;
        }
    }

    public void setAppLocale(String language){
        Resources res  = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language.toLowerCase()));
        res.updateConfiguration(conf, dm);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase,"en"));
    }
}
