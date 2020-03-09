package com.jvit.companycoin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.adapter.QnAAdapter;
import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.object.QnA;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.QuestionAndAnswer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QnAActivity extends AppCompatActivity  implements View.OnClickListener {
    private RecyclerView rcViewQNA;
    private QnAAdapter adapter;
    private ArrayList<QnA> listQnA;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView btnBack;
    private ApiClient apiClient;
    private SharedPreferences preferencesToken;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qna);
        rcViewQNA = findViewById(R.id.rcViewQNA);
        btnBack = findViewById(R.id.img_backQNA);

        preferencesToken = getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN,HomeFragment.TOKEN_NULL);

        apiClient = ApiService.getRetrofit().create(ApiClient.class);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        if (listQnA == null) {
            listQnA = new ArrayList<>();
            apiQnA();
        }

        btnBack.setOnClickListener(this);
    }

    private void apiQnA(){
        Call<QuestionAndAnswer> questionAndAnswerCall = apiClient.QUESTION_AND_ANSWER_CALL("Bearer " + token);
        questionAndAnswerCall.enqueue(new Callback<QuestionAndAnswer>() {
            @Override
            public void onResponse(Call<QuestionAndAnswer> call, Response<QuestionAndAnswer> response) {
                QuestionAndAnswer questionAndAnswer = response.body();
                List<QuestionAndAnswer.QnA> listQnA = questionAndAnswer.getQnAList();
                for (QuestionAndAnswer.QnA itemQnA : listQnA){
                    QnAActivity.this.listQnA.add(new QnA(itemQnA.getQuestion(), R.drawable.add, itemQnA.getAnswer()));
                }
                adapter = new QnAAdapter(QnAActivity.this, QnAActivity.this.listQnA);
                rcViewQNA.setAdapter(adapter);
                rcViewQNA.setLayoutManager(layoutManager);
            }

            @Override
            public void onFailure(Call<QuestionAndAnswer> call, Throwable t) {
                call.cancel();

            }
        });


    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_backQNA:
                finish();
                break;
        }
    }
}
