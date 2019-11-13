package com.jvit.companycoin.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jvit.companycoin.activity.InfoGiftExchangeActivity;
import com.jvit.companycoin.adapter.GiftExchangeAdapter;
import com.jvit.companycoin.objectbuild.BuildGiftExchange;
import com.jvit.companycoin.objectbuild.GiftExchange;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.AllGiftExchange;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiftExchangeFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{
    public GiftExchangeFragment(){}
    private Button btnAll, btnNewArrival, btnPopularity;
    private RecyclerView rcGiftExchange;
    private GiftExchangeAdapter giftExchangeAdapter;
    private ArrayList<GiftExchange> listAllGift, listNewGift, listRecommendGift;
    private RecyclerView.LayoutManager layoutManager;
    private View view;
    public static ApiClient apiClient;
    private SwipeRefreshLayout refreshGiftExchange;
    private SharedPreferences preferencesToken;
    private String token;
    private final String FILTER_ALL = "all";
    private final String FILTER_NEW = "new";
    private final String FILTER_RECOMMEND = "recommend";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            return view;
        }

        view = inflater.inflate(R.layout.fragment_giftexchange,container,false);

        preferencesToken = getContext().getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN,HomeFragment.TOKEN_NULL);
        apiClient = ApiService.getRetrofit().create(ApiClient.class);
        initView();

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        addInfoGift(FILTER_ALL);

        refreshGiftExchange.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        refreshGiftExchange.post(new Runnable() {
            @Override
            public void run() {
                refreshGiftExchange.setRefreshing(true);
                addInfoGift(FILTER_ALL);
            }
        });
        btnAll.setOnClickListener(this);
        btnNewArrival.setOnClickListener(this);
        btnPopularity.setOnClickListener(this);
        refreshGiftExchange.setOnRefreshListener(this);
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }
    private void initView(){
        refreshGiftExchange = view.findViewById(R.id.refreshGiftExchange);
        btnAll = view.findViewById(R.id.btnAllGiftExchange);
        btnNewArrival = view.findViewById(R.id.btnExchangeGiftExchange);
        btnPopularity = view.findViewById(R.id.btnPopularityGiftExchange);
        rcGiftExchange = view.findViewById(R.id.rcViewGiftExchange);
    }

    private void addInfoGift(final String filter){
        final Call<AllGiftExchange> exchangeCall = apiClient.ALL_GIFT_EXCHANGE_CALL("Bearer " + token);
        exchangeCall.enqueue(new Callback<AllGiftExchange>() {
            @Override
            public void onResponse(Call<AllGiftExchange> call, Response<AllGiftExchange> response) {

                AllGiftExchange giftExchange = response.body();
                if (giftExchange != null) {
                    listAllGift = new ArrayList<>();
                    listNewGift = new ArrayList<>();
                    listRecommendGift = new ArrayList<>();
                    switch (filter) {
                        case FILTER_ALL:
                            List<AllGiftExchange.AllGift> allGiftExchangeList = giftExchange.getAllGifts();
                            for (AllGiftExchange.AllGift infoAllGift : allGiftExchangeList) {
                                GiftExchange allGift = new BuildGiftExchange()
                                        .id(infoAllGift.getId())
                                        .giftName(infoAllGift.getName())
                                        .introGift(infoAllGift.getName())
                                        .imageGift(infoAllGift.getImage_path())
                                        .priceGift(infoAllGift.getPrice())
                                        .quantity(infoAllGift.getQuantity())
                                        .isRecommend(infoAllGift.isRecommend())
                                        .isNew(infoAllGift.isNews())
                                        .build();
                                listAllGift.add(allGift);
                            }
                            giftExchangeAdapter = new GiftExchangeAdapter(getActivity(), listAllGift);
                            giftExchangeAdapter.notifyDataSetChanged();
                            rcGiftExchange.setAdapter(giftExchangeAdapter);
                            rcGiftExchange.setLayoutManager(layoutManager);
                            giftExchangeAdapter.setItemClickListener(new GiftExchangeAdapter.ItemClickListener() {
                                @Override
                                public void onClick(GiftExchange gift) {
                                    Intent intent = new Intent(getActivity(), InfoGiftExchangeActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(InfoGiftExchangeActivity.ID_GIFT, gift.getId());
                                    bundle.putString(InfoGiftExchangeActivity.IMAGE_GIFT, ApiService.url_path + gift.getImageGift());
                                    bundle.putString(InfoGiftExchangeActivity.NAME_GIFT, gift.getGiftName());
                                    bundle.putString(InfoGiftExchangeActivity.INTRO_GIFT, gift.getIntroGift());
                                    bundle.putInt(InfoGiftExchangeActivity.PRICE_GIFT, gift.getPriceGift());
                                    bundle.putInt(InfoGiftExchangeActivity.QUANTITY, gift.getQuantity());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });
                            break;
                        case FILTER_NEW:
                            List<AllGiftExchange.AllGift> newGiftExchangeList = giftExchange.getNewGifts();
                            for (AllGiftExchange.AllGift infoNewGift : newGiftExchangeList) {
                                GiftExchange newGift = new BuildGiftExchange()
                                        .id(infoNewGift.getId())
                                        .giftName(infoNewGift.getName())
                                        .introGift(infoNewGift.getName())
                                        .imageGift(infoNewGift.getImage_path())
                                        .priceGift(infoNewGift.getPrice())
                                        .quantity(infoNewGift.getQuantity())
                                        .isRecommend(infoNewGift.isRecommend())
                                        .isNew(infoNewGift.isNews())
                                        .build();
                                listNewGift.add(newGift);

                            }
                            giftExchangeAdapter = new GiftExchangeAdapter(getActivity(), listNewGift);
                            giftExchangeAdapter.notifyDataSetChanged();
                            rcGiftExchange.setAdapter(giftExchangeAdapter);
                            rcGiftExchange.setLayoutManager(layoutManager);
                            giftExchangeAdapter.setItemClickListener(new GiftExchangeAdapter.ItemClickListener() {
                                @Override
                                public void onClick(GiftExchange gift) {
                                    Intent intent = new Intent(getActivity(), InfoGiftExchangeActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(InfoGiftExchangeActivity.ID_GIFT, gift.getId());
                                    bundle.putString(InfoGiftExchangeActivity.IMAGE_GIFT, ApiService.url_path + gift.getImageGift());
                                    bundle.putString(InfoGiftExchangeActivity.NAME_GIFT, gift.getGiftName());
                                    bundle.putString(InfoGiftExchangeActivity.INTRO_GIFT, gift.getIntroGift());
                                    bundle.putInt(InfoGiftExchangeActivity.PRICE_GIFT, gift.getPriceGift());
                                    bundle.putInt(InfoGiftExchangeActivity.QUANTITY, gift.getQuantity());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });
                            break;

                        case FILTER_RECOMMEND:
                            List<AllGiftExchange.AllGift> recommendGiftExchangeList = giftExchange.getRecommendGifts();
                            for (AllGiftExchange.AllGift infoRecommendGift : recommendGiftExchangeList) {
                                GiftExchange recommendGift = new BuildGiftExchange()
                                        .id(infoRecommendGift.getId())
                                        .giftName(infoRecommendGift.getName())
                                        .introGift(infoRecommendGift.getName())
                                        .imageGift(infoRecommendGift.getImage_path())
                                        .priceGift(infoRecommendGift.getPrice())
                                        .quantity(infoRecommendGift.getQuantity())
                                        .isRecommend(infoRecommendGift.isRecommend())
                                        .isNew(infoRecommendGift.isNews())
                                        .build();
                                listRecommendGift.add(recommendGift);

                            }
                            giftExchangeAdapter = new GiftExchangeAdapter(getActivity(), listRecommendGift);
                            giftExchangeAdapter.notifyDataSetChanged();
                            rcGiftExchange.setAdapter(giftExchangeAdapter);
                            rcGiftExchange.setLayoutManager(layoutManager);
                            giftExchangeAdapter.setItemClickListener(new GiftExchangeAdapter.ItemClickListener() {
                                @Override
                                public void onClick(GiftExchange gift) {
                                    Intent intent = new Intent(getActivity(), InfoGiftExchangeActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(InfoGiftExchangeActivity.ID_GIFT, gift.getId());
                                    bundle.putString(InfoGiftExchangeActivity.IMAGE_GIFT, ApiService.url_path + gift.getImageGift());
                                    bundle.putString(InfoGiftExchangeActivity.NAME_GIFT, gift.getGiftName());
                                    bundle.putString(InfoGiftExchangeActivity.INTRO_GIFT, gift.getIntroGift());
                                    bundle.putInt(InfoGiftExchangeActivity.PRICE_GIFT, gift.getPriceGift());
                                    bundle.putInt(InfoGiftExchangeActivity.QUANTITY, gift.getQuantity());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });
                            break;

                    }
                    refreshGiftExchange.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(Call<AllGiftExchange> call, Throwable t) {

                refreshGiftExchange.setRefreshing(false);
                call.cancel();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnAllGiftExchange:
                btnAll.setTextColor(getResources().getColor(R.color.white));
                btnNewArrival.setTextColor(getResources().getColor(R.color.gray_dark));
                btnPopularity.setTextColor(getResources().getColor(R.color.gray_dark));
                btnAll.setBackground(getResources().getDrawable(R.drawable.custom_button_all_gitfexchange_grey));
                btnNewArrival.setBackground(getResources().getDrawable(R.drawable.custom_button_new_arrial_giftexchange_white));
                btnPopularity.setBackground(getResources().getDrawable(R.drawable.custom_button_popularity_gitfexchange_white));
                addInfoGift(FILTER_ALL);
                giftExchangeAdapter.notifyDataSetChanged();
                break;
            case R.id.btnExchangeGiftExchange:
                btnAll.setTextColor(getResources().getColor(R.color.gray_dark));

                btnNewArrival.setTextColor(getResources().getColor(R.color.white));
                btnPopularity.setTextColor(getResources().getColor(R.color.gray_dark));
                btnAll.setBackground(getResources().getDrawable(R.drawable.custom_button_all_gitfexchange_white));
                btnNewArrival.setBackground(getResources().getDrawable(R.drawable.custom_button_new_arrial_giftexchange_gray));
                btnPopularity.setBackground(getResources().getDrawable(R.drawable.custom_button_popularity_gitfexchange_white));
                addInfoGift(FILTER_NEW);
                giftExchangeAdapter.notifyDataSetChanged();
                break;
            case R.id.btnPopularityGiftExchange:
                btnAll.setTextColor(getResources().getColor(R.color.gray_dark));
                btnNewArrival.setTextColor(getResources().getColor(R.color.gray_dark));
                btnPopularity.setTextColor(getResources().getColor(R.color.white));
                btnAll.setBackground(getResources().getDrawable(R.drawable.custom_button_all_gitfexchange_white));
                btnNewArrival.setBackground(getResources().getDrawable(R.drawable.custom_button_new_arrial_giftexchange_white));
                btnPopularity.setBackground(getResources().getDrawable(R.drawable.custom_button_popularity_gitfexchange_grey));
                addInfoGift(FILTER_RECOMMEND);
                giftExchangeAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onRefresh() {
        addInfoGift("all");
    }
}
