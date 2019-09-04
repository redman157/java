package com.example.intentexam1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ReplyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        myTable =  findViewById(R.id.tableLayoutImage);
        ArrayAdapter<String> li = new ArrayAdapter<String>()
        int n_row = 6;
        int n_col = 3;
        for(int i = 1; i <= n_row;i++){
            TableRow tableRow = new TableRow(this);
            for (int j = 1; j <= n_col; j++){
                ImageView imageView = new ImageView(this);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(300,300);
                imageView.setLayoutParams(layoutParams);
                final int pos = n_col * (i - 1) + j - 1;

                int idImg = getResources().getIdentifier(MainActivity.listName.get(pos),"drawable",getPackageName());
                imageView.setImageResource(idImg);
                // add thêm image vào dòng
                tableRow.addView(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("imagechoose",MainActivity.listName.get(pos));
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                });
            }
            // add cột vào table layout
            myTable.addView(tableRow);
        }
        for (int i = 1; i <= n_row; i++) {
            TableRow tableRow = new TableRow(this);
            for (int j = 1; j <= n_col; j++) {
                ImageView imageView = new ImageView(this);
                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams( ,);
                imageView.setLayoutParams(layoutParams);

                int idImg = getResources().getIdentifier(..get(pos), "", );
                imageView.setImageResource(idImg);
                // add thêm image vào dòng
                tableRow.addView(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("", MainActivity.listName.get(pos));
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
            // add cột vào table layout
            .addView(tableRow);
        }

    }
    private TableLayout myTable;
}
