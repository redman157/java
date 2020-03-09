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

import com.jvit.companycoin.adapter.RankAllAdapter;
import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.object.ItemUser;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.AllRank;
import com.jvit.companycoin.api.ItemUserRank;
import com.jvit.companycoin.api.Pagination;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankActivity extends AppCompatActivity implements View.OnClickListener{
    private RecyclerView rcViewAllRank;
    private RankAllAdapter rankAllAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ItemUser> listUser = new ArrayList<>();
    private ImageView imgBack;
    private ApiClient apiClient;
    private SharedPreferences preferencesToken;
    private String token;
    private boolean isLoading = false;
    private int total,total_page,per_page,curr_page;
    private int page=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        rcViewAllRank = findViewById(R.id.rcViewAllRank);
        imgBack = findViewById(R.id.img_backRankAll);
        listUser = new ArrayList<>();

        preferencesToken = getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN,HomeFragment.TOKEN_NULL);
        apiClient = ApiService.getRetrofit().create(ApiClient.class);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        apiAllRank();
        initScrollListener();

        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_backRankAll:
                finish();
                break;
        }
    }

    private void apiAllRank(){
        final Call<AllRank> allRank = apiClient.USER_ALL_RANK_CALL("Bearer " +token);
        allRank.enqueue(new Callback<AllRank>() {
            @Override
            public void onResponse(Call<AllRank> call, Response<AllRank> response) {
                listUser = new ArrayList<>();
                AllRank pageAllRank = response.body();
                if (pageAllRank != null) {
                    List<ItemUserRank> dataRank = pageAllRank.getDataAllRank();

                    AllRank.PaginationResponse paginationResponse = pageAllRank.getMeta();
                    Pagination pagination = paginationResponse.getPagination();

                    total = pagination.getTotal();
                    per_page = pagination.getPer_page();
                    total_page = pagination.getTotal_pages();
                    curr_page = pagination.getCurrent_page();

                    for (ItemUserRank UserRank : dataRank) {
                        listUser.add(new ItemUser(
                                UserRank.getRank_no(),
                                UserRank.getAvatar_path(),
                                UserRank.getName(),
                                UserRank.getToken_amount(),
                                UserRank.getIs_token_increase()
                        ));
                    }
                }
                rankAllAdapter = new RankAllAdapter(RankActivity.this, listUser);
                rankAllAdapter.notifyDataSetChanged();

                rcViewAllRank.setAdapter(rankAllAdapter);
                rcViewAllRank.setLayoutManager(layoutManager);
            }


            @Override
            public void onFailure(Call<AllRank> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void initScrollListener(){
        rcViewAllRank.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading && listUser.size() > 1) {
                    if (linearLayoutManager != null &&
                            linearLayoutManager.findLastCompletelyVisibleItemPosition() == listUser.size() - 1) {
                        //bottom of list!
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {

        if (listUser.size() < total || page < total_page) {
            listUser.add(null);
            rankAllAdapter.notifyItemInserted(listUser.size() - 1);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listUser.remove(listUser.size() - 1);// xóa null
                    int scrollPosition = listUser.size();
                    rankAllAdapter.notifyItemRemoved(scrollPosition);
                    loadPage();

                    rankAllAdapter.notifyDataSetChanged();
                }
            }, 1000);
        }
    }

    private void loadPage(){
        page = page + 1;
        final Call<AllRank> allRank = apiClient.USER_ALL_RANK_PAGE_CALL("Bearer " +token,String.valueOf(page));
        allRank.enqueue(new Callback<AllRank>() {
            @Override
            public void onResponse(Call<AllRank> call, Response<AllRank> response) {
                AllRank allRankUserPage = response.body();
                if (allRankUserPage != null) {
                    List<ItemUserRank> dataRankPage = allRankUserPage.getDataAllRank();
                    for (ItemUserRank UserRankPage : dataRankPage) {
                        listUser.add(new ItemUser(
                                UserRankPage.getRank_no(),
                                UserRankPage.getAvatar_path(),
                                UserRankPage.getName(),
                                UserRankPage.getToken_amount(),
                                UserRankPage.getIs_token_increase()
                        ));
                    }
                }
                rankAllAdapter.notifyDataSetChanged();
                isLoading = false;
            }

            @Override
            public void onFailure(Call<AllRank> call, Throwable t) {
                call.cancel();

            }
        });

    }
}
