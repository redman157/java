package com.example.intentexam1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private ImageView imgQuestion;
    private ImageView imgReply;
    private TextView textCount;
    public static ArrayList<String> listName;
    final int REQUEST_CODE_IMAGE = 113;
    String ImageOri = "";// hinh gốc
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textCount = findViewById(R.id.textCount);
        imgQuestion = findViewById(R.id.imgQuestion);
        imgReply = findViewById(R.id.imgReply);
        String[] name = getResources().getStringArray(R.array.list_name);
        listName = new ArrayList<>(Arrays.asList(name));

        Collections.shuffle(listName);
        ImageOri = listName.get(5);// set image goc thanhg String
        int idImg = getResources().getIdentifier(listName.get(5),"drawable",getPackageName());
        imgQuestion.setImageResource(idImg);

        imgReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReplyActivity.class);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data !=null){
            String image = data.getStringExtra("imagechoose");
            int idImg = getResources().getIdentifier(image,"drawable", getPackageName());
            imgReply.setImageResource(idImg);
            if(ImageOri.equals(image)){
                count += 1;
                textCount.setText(count + "");
                Toast.makeText(MainActivity.this,"Chính xác !! Bạn Bị Cộng 1 Điểm",Toast.LENGTH_SHORT).show();

                new CountDownTimer(2000, 100) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        Collections.shuffle(listName);
                        ImageOri = listName.get(5);// set image goc thanhg String
                        int idImg = getResources().getIdentifier(listName.get(5),"drawable",getPackageName());
                        imgQuestion.setImageResource(idImg);
                    }
                }.start();
            }else {
                Toast.makeText(MainActivity.this,"Sai rồi !! \n Bạn Bị Trừ 1 Điểm",Toast.LENGTH_SHORT).show();
                count -= 1;
                textCount.setText(count + "");
            }
            if(requestCode == REQUEST_CODE_IMAGE  && resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this, "Bạn Chưa Chọn Ảnh \n Bạn Bị Trừ 1 Điểm", Toast.LENGTH_SHORT);
                count -=1 ;
                textCount.setText(count + "");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reload, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.reload){
            Collections.shuffle(listName);
            ImageOri = listName.get(5);// set image goc thanhg String
            int idImg = getResources().getIdentifier(listName.get(5),"drawable",getPackageName());
            imgQuestion.setImageResource(idImg);
        }
        return super.onOptionsItemSelected(item);
    }


}
