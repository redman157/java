package com.example.templatercview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public static RecyclerView recyclerView;
    public static RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rcView);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        Template template = new InfoUser(this);
        template.show();
    }


}
