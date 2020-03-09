package com.example.listinlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView rcGroup;
    ArrayList<InfoApp> listApp;
    RecyclerView.LayoutManager layoutManagerGroup;
    PlayStoreAdapter storeAdapter;
    ArrayList<PlayStore> playStores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rcGroup = findViewById(R.id.rcGroup);
        createData();
        layoutManagerGroup = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rcGroup.setHasFixedSize(true);
        storeAdapter = new PlayStoreAdapter(MainActivity.this, playStores);
        rcGroup.setLayoutManager(layoutManagerGroup);
        rcGroup.setAdapter(storeAdapter);
    }
    private ArrayList<InfoApp> addGame(){
        listApp = new ArrayList<>();
        listApp.add(new InfoApp(R.drawable.ageofz, "Age Of Z", "4.2"));
        listApp.add(new InfoApp(R.drawable.autocheck, "Auto Chest", "4.9"));
        listApp.add(new InfoApp(R.drawable.bangbang, "Mobile Legend", "4.5"));
        listApp.add(new InfoApp(R.drawable.lienquan, "Liên Quân Mobile", "4.8"));
        listApp.add(new InfoApp(R.drawable.pk2, "Pocket Knight 2", "4.0"));
        return listApp;
    }
    private ArrayList<InfoApp> addCamera(){
        listApp = new ArrayList<>();
        listApp.add(new InfoApp(R.drawable.ulike, "ULike", "4.1"));
        listApp.add(new InfoApp(R.drawable.soda, "Soda", "4.9"));
        listApp.add(new InfoApp(R.drawable.vsco, "Vsco", "4.5"));
        listApp.add(new InfoApp(R.drawable.came, "360 Camera", "4.8"));
        listApp.add(new InfoApp(R.drawable.faceu, "Face You", "4.0"));
        return listApp;
    }
    private ArrayList<InfoApp> addSocial(){
        listApp = new ArrayList<>();
        listApp.add(new InfoApp(R.drawable.facebook, "Facebook", "4.3"));
        listApp.add(new InfoApp(R.drawable.zalo, "Zalo", "4.9"));
        listApp.add(new InfoApp(R.drawable.twit, "Twiter", "4.6"));
        listApp.add(new InfoApp(R.drawable.insta, "Instagram", "4.8"));
        listApp.add(new InfoApp(R.drawable.mess, "Message", "4.0"));
        return listApp;
    }
    private ArrayList<InfoApp> addGameVui(){
        listApp = new ArrayList<>();
        listApp.add(new InfoApp(R.drawable.hoingu, "Hỏi Xoáy Đáp Xoay", "4.2"));
        listApp.add(new InfoApp(R.drawable.nongtrai, "Nông Trại Vui Vẻ", "4.2"));
        listApp.add(new InfoApp(R.drawable.ninja, "Ninja Mobile", "4.5"));
        listApp.add(new InfoApp(R.drawable.chu, "Xếp Chữ", "4.8"));
        listApp.add(new InfoApp(R.drawable.monster, "Quái Vật Không Gian", "4.4"));
        return listApp;
    }
    private ArrayList<InfoApp> addBank(){
        listApp = new ArrayList<>();
        listApp.add(new InfoApp(R.drawable.vietcom, "Vietcombank", "4.2"));
        listApp.add(new InfoApp(R.drawable.acbbank, "ACB Bank", "4.1"));
        listApp.add(new InfoApp(R.drawable.agr, "Agribank", "4.6"));
        listApp.add(new InfoApp(R.drawable.viettin, "ViettinBank", "4.0"));
        listApp.add(new InfoApp(R.drawable.techcome, "TechcomBank", "4.1"));
        return listApp;
    }
    private void createData(){
        playStores = new ArrayList<>();
        playStores.add(new PlayStore("Game", addGame()));
        playStores.add(new PlayStore("Ngân Hàng", addBank()));
        playStores.add(new PlayStore("Camera", addCamera()));
        playStores.add(new PlayStore("Game Vui", addGameVui()));
        playStores.add(new PlayStore("Mạng Xã Hội", addSocial()));
    }
}
