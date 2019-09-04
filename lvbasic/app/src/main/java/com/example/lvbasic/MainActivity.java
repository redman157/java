package com.example.lvbasic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lvLanguage;
    ArrayList<Language> listLanguage;
    LanguageAdapter languageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AnhXa();

        languageAdapter = new LanguageAdapter(this,listLanguage);
        lvLanguage.setAdapter(languageAdapter);

    }
    private void AnhXa(){
        lvLanguage = findViewById(R.id.lvLanguage);
        listLanguage = new ArrayList<>();

        listLanguage.add(new Language(R.drawable.java,"Sơn","Java"));
        listLanguage.add(new Language(R.drawable.python,"Tú","Python"));
        listLanguage.add(new Language(R.drawable.php,"Tuấn","PHP"));
        listLanguage.add(new Language(R.drawable.javascript,"Kiệt","Javascript"));
        listLanguage.add(new Language(R.drawable.sql,"Vũ","SQL"));
    }

}
