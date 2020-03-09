package com.jvit.companycoin.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.TransactionsHistory;
import com.jvit.companycoin.adapter.TransactionHistoryAdapter;
import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.R;
import com.jvit.companycoin.object.TransactionHistory;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionHistoryActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView rcViewHistory;
    private TransactionHistoryAdapter historyAdapter;
    private ArrayList<TransactionHistory> historyArrayList;
    private RecyclerView.LayoutManager layoutManager;

    private ImageView btnBack;
    private ApiClient apiClient;
    private SharedPreferences preferencesToken;

    private String token;
    private String avatarSend, avatarReceive, message, nameSend, nameReceive;
    private int icon;
    private boolean isLoading = false;
    private int total,total_page,curr_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        initView();

        apiClient = ApiService.getRetrofit().create(ApiClient.class);
        preferencesToken = getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN,HomeFragment.TOKEN_NULL);
        if (historyArrayList == null){
            historyArrayList = new ArrayList<>();
            apiTransactionHistory();
        }

        historyAdapter = new TransactionHistoryAdapter(this, historyArrayList);
        rcViewHistory.setAdapter(historyAdapter);
        rcViewHistory.setLayoutManager(layoutManager);
        initScrollListener();
        btnBack.setOnClickListener(this);

    }
    private void apiTransactionHistory(){
        final Call<TransactionsHistory> historyCall = apiClient.TRANSACTIONS_HISTORY_CALL("Bearer " + token);
        historyCall.enqueue(new Callback<TransactionsHistory>() {
            @Override
            public void onResponse(Call<TransactionsHistory> call, Response<TransactionsHistory> response) {
                TransactionsHistory transactionsHistory = response.body();
                if (transactionsHistory != null){
                    List<TransactionsHistory.Data> historyData = transactionsHistory.getData();
                    TransactionsHistory.Meta meta = transactionsHistory.getMeta();
                    total = meta.getPagination().getTotal();
                    total_page = meta.getPagination().getTotal_pages();
                    curr_page = meta.getPagination().getCurrent_page();

                    for (TransactionsHistory.Data data: historyData){
                        if (data.getNote() == null){
                            message = "";
                        }else {
                            message = data.getNote();
                        }
                        switch (data.getType()){
                            case 1:
                                icon = R.drawable.ic_history_checkin;
                            case 2:
                                icon = R.drawable.ic_history_sendidea;
                                break;
                            case 3:
                                icon = R.drawable.ic_history_transfer;
                                break;
                            case 7:
                                icon = R.drawable.ic_history_reached_like;
                                break;
                            case 101:
                                icon = R.drawable.ic_history_transaction_exchange;
                                break;

                        }
                        if (data.getUser() == null) {
                            avatarSend = "";
                            nameSend = "Hệ Thống";
                        } else {
                            if (data.getUser().getAvatar_path() == null) {
                                avatarSend = ApiService.url +"_nuxt/img/a000933.png";
                            } else {
                                avatarSend = data.getUser().getAvatar_path();
                            }
                            nameSend = data.getUser().getName();
                        }
                        if (data.getTarget_user() == null) {
                            avatarReceive = "";
                            nameReceive = "Hệ Thống";
                        } else {
                            if (data.getTarget_user().getAvatar_path() == null) {
                                avatarReceive = ApiService.url +"_nuxt/img/a000933.png";
                            } else {
                                avatarReceive = data.getTarget_user().getAvatar_path();
                            }
                            nameReceive = data.getTarget_user().getName();
                        }

                        historyArrayList.add(new TransactionHistory(
                                icon,
                                avatarSend,
                                nameSend,
                                message,
                                avatarReceive,
                                nameReceive,
                                data.getCreated_at(),
                                data.getToken_amount()
                        ));

                    }
                }
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<TransactionsHistory> call, Throwable t) {

            }
        });


    }

    private void initView(){
        rcViewHistory = findViewById(R.id.rcViewHistory);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        btnBack = findViewById(R.id.img_backHistory);
    }

    private void initScrollListener(){
        rcViewHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading && historyArrayList.size() > 1) {
                    if (linearLayoutManager != null &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() == historyArrayList.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        if (historyArrayList.size() < total || curr_page < total_page) {
            historyArrayList.add(null);
            historyAdapter.notifyItemInserted(historyArrayList.size() - 1);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    historyArrayList.remove(historyArrayList.size() - 1);// xóa null
                    int scrollPosition = historyArrayList.size();
                    historyAdapter.notifyItemRemoved(scrollPosition);
                    loadPage();

                    historyAdapter.notifyDataSetChanged();
                }
            }, 1000);
        }
    }

    private void loadPage(){
        curr_page = curr_page + 1;
        final Call<TransactionsHistory> allRank = apiClient.TRANSACTIONS_HISTORY_PAGE_CALL(
                "Bearer " +token,
                String.valueOf(curr_page));
        allRank.enqueue(new Callback<TransactionsHistory>() {
            @Override
            public void onResponse(Call<TransactionsHistory> call, Response<TransactionsHistory> response) {
                TransactionsHistory transactionsHistory = response.body();
                if (transactionsHistory != null) {
                    List<TransactionsHistory.Data> historyData = transactionsHistory.getData();
                    for (TransactionsHistory.Data data: historyData){
                        if (data.getNote() == null){
                            message = "";
                        }else {
                            message = data.getNote();
                        }
                        switch (data.getType()){
                            case 1:
                                icon = R.drawable.ic_history_checkin;
                                break;
                            case 2:
                                icon = R.drawable.ic_history_sendidea;
                                break;
                            case 3:
                                icon = R.drawable.ic_history_transfer;
                                break;
                            case 7:
                                icon = R.drawable.ic_history_reached_like;
                                break;
                            case 101:
                                icon = R.drawable.ic_history_transaction_exchange;
                                break;

                        }

                        if (data.getUser() == null) {
                            avatarSend = "";
                            nameSend = "Hệ Thống";
                        } else {
                            if (data.getUser().getAvatar_path() == null) {
                                avatarSend = "https://mobile1.companycoin.net/_nuxt/img/a000933.png";
                            } else {
                                avatarSend = data.getUser().getAvatar_path();
                            }
                            nameSend = data.getUser().getName();
                        }
                        if (data.getTarget_user() == null) {
                            avatarReceive = "";
                            nameReceive = "Hệ Thống";
                        } else {
                            if (data.getTarget_user().getAvatar_path() == null) {
                                avatarReceive = "https://mobile1.companycoin.net/_nuxt/img/a000933.png";
                            } else {
                                avatarReceive = data.getTarget_user().getAvatar_path();
                            }
                            nameReceive = data.getTarget_user().getName();
                        }

                        historyArrayList.add(new TransactionHistory(
                                icon,
                                avatarSend,
                                nameSend,
                                message,
                                avatarReceive,
                                nameReceive,
                                data.getCreated_at(),
                                data.getToken_amount()
                                ));

                    }
                }
                historyAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Call<TransactionsHistory> call, Throwable t) {
                call.cancel();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_backHistory:
                finish();
                break;
        }
    }

}
