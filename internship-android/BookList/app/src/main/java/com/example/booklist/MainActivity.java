package com.example.booklist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    TextView textInfo, textTitle;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    BookAdapter adapter;
    ArrayList<Book> listBook;

    APIInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rcView);
        textTitle = findViewById(R.id.textTitle);
        textInfo  = findViewById(R.id.textInfo);

        apiInterface = ApiUtils.getAPIService();
        // thự tự code
        listBook = new ArrayList<>();
        adapter = new BookAdapter(listBook, MainActivity.this);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        loadData();

    }

    private void loadData() {
        Call<MultipleResource> call = apiInterface.doGetListBook();
        call.enqueue(new Callback<MultipleResource>() {
            @Override
            public void onResponse(Call<MultipleResource> call, Response<MultipleResource> response) {
                Log.d("TAG",response.code()+"");
                String title = "";
                String info = "";
                String highLight = "";

                MultipleResource resource =response.body();
                List<MultipleResource.Book> dataList = resource.bookpage_list;

                for (int i = 0 ; i < dataList.size(); i++){
                    title = i+1 +". "+ dataList.get(i).book_name+ " " + dataList.get(i).volume_name +" "+dataList.get(i).chapter_name;
                    info = dataList.get(i).neighbor_text;
                    highLight = dataList.get(i).highlight;
                    Log.d("AAA", highLight);
                    listBook.add(new Book(title,info,highLight));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MultipleResource> call, Throwable t) {
                call.cancel();
            }
        });
    }

}
