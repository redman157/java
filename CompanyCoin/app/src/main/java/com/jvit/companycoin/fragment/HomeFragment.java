package com.jvit.companycoin.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jvit.companycoin.activity.RankActivity;

import com.jvit.companycoin.activity.HomeActivity;
import com.jvit.companycoin.CoinCount;
import com.jvit.companycoin.api.CardInfo;
import com.jvit.companycoin.api.CheckInUser;
import com.jvit.companycoin.object.ItemUser;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.ItemUserRank;
import com.jvit.companycoin.api.TopRank;
import com.jvit.companycoin.api.UserLogin;
import com.jvit.companycoin.fragment.sliderItemFragment.SliderGiftAdapter;
import com.jvit.companycoin.fragment.sliderItemFragment.SliderFeedBackAdapter;
import com.jvit.companycoin.adapter.UserRankAdapter;
import com.jvit.companycoin.fragment.animationSlider.ZoomOutPageTransformer;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements CoinCount, View.OnClickListener{
    public HomeFragment(){}
    private Dialog checkInCoin;
    private LinearLayout btnCheckIn;
    private Timer timer;
    private TextView textDay;

    private SharedPreferences  preferencesToken;
    private ViewPager viewPagerGiftHome, viewPagerPostHome;
    private LinearLayout dotsLayoutGiftHome, dotsLayoutPostHome;
    private TextView textInfo;
    public static TextView textCoin,textHistory, textNameCompany;;

    static long timestamp;

    private String timeLogin;
    public static ImageView imgAvatar;
    private RecyclerView rcUserRank;
    private UserRankAdapter userRankAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ItemUser> listItemUser;
    private View view;
    private Button btnGiftExchange, btnAllComment, btnSendCoinHome, btnAllRankHome;
    private TabLayout tabLayout;
    private SliderGiftAdapter sliderGiftAdapter;
    public static SliderFeedBackAdapter sliderFeedBackAdapter;
    private final String LIST_USER = "listuser";
    private final String HISTORY_SEND_COIN = "HISTORY_SEND_COIN";
    private final String SAVE_COIN_HISTORY = "SAVE_COIN_HISTORY";
    private SharedPreferences preferencesHistory;
    private SharedPreferences.Editor editHistory;
    private String token, code, checkin_opened_at, checkin_closed_at;
    private int id,coinHistory, type, token_amount, user_id, target_user_id, token_amount_user;
    private boolean has_checked_in;

    public static ApiClient apiClient;
    public final static String TOKEN = "token";
    public final static String TOKEN_NULL = "token_null";
    public final static String SAVE_TOKEN = "SAVE_TOKEN";
    public static String name, avatar, coinUser, email;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (view != null) {
            return view;
        }

        view = inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        preferencesToken = getActivity().getSharedPreferences(SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN,HomeFragment.TOKEN_NULL);
        apiClient = ApiService.getRetrofit().create(ApiClient.class);
        preferencesHistory = getActivity().getSharedPreferences(HISTORY_SEND_COIN, Context.MODE_PRIVATE);
        editHistory = preferencesHistory.edit();
        if (SendCoinsFragment.tradeHistory){
            textHistory.setText("-"+(Integer.valueOf(HomeFragment.coinUser) - SendCoinsFragment.coinUser));
        }
        topRankUser();
        cardInfoUser();
        infoUserLogin();
        if (sliderFeedBackAdapter == null ){
            sliderFeedBackAdapter = new SliderFeedBackAdapter(getChildFragmentManager(), getActivity());
            sliderFeedBackAdapter.onRendering(new SliderFeedBackAdapter.RendererPost() {
                @Override
                public void Refresh() {
                    viewPagerPostHome.setAdapter(sliderFeedBackAdapter);
                }

                @Override
                public void Render() {
                    viewPagerPostHome.invalidate();
                    dotsLayoutPostHome.removeAllViews();

                    for (TextView dot : sliderFeedBackAdapter.getDots()){
                        dotsLayoutPostHome.addView(dot);
                    }
                }

                @Override
                public void Switch() {
                    if (viewPagerPostHome.getCurrentItem() < sliderFeedBackAdapter.getCount() - 1){
                        viewPagerPostHome.setCurrentItem(viewPagerPostHome.getCurrentItem() + 1,
                                true);
                        viewPagerPostHome.setPageTransformer(true ,
                                new ZoomOutPageTransformer());
                    } else {
                        viewPagerPostHome.setCurrentItem(0, true);
                        viewPagerPostHome.setPageTransformer(true ,
                                new ZoomOutPageTransformer());
                    }

                    for (int i = 0; i < sliderFeedBackAdapter.getCount(); ++i){
                        if (i == viewPagerPostHome.getCurrentItem()) {
                            sliderFeedBackAdapter.getDots().get(i).setTextColor(getActivity().getResources().getIntArray(R.array.array_dot_active)[0]);
                        } else {
                            sliderFeedBackAdapter.getDots().get(i).setTextColor(getActivity().getResources().getIntArray(R.array.array_dot_inactive)[0]); // set dùm cái invalid color
                        }
                    }
                }
            });
            
            apiClient.IDEA_NEW_CALL("Bearer " + token).enqueue(sliderFeedBackAdapter);
            viewPagerPostHome.addOnPageChangeListener(sliderFeedBackAdapter);
        }

        if (sliderGiftAdapter == null){
            sliderGiftAdapter = new SliderGiftAdapter(getChildFragmentManager(),
                                                      getActivity());

            sliderGiftAdapter.onRendering(new SliderGiftAdapter.RendererGift() {
                public void Refresh() {
                    viewPagerGiftHome.setAdapter(sliderGiftAdapter);
                    dotsLayoutGiftHome.removeAllViews();

                    for (TextView dot : sliderGiftAdapter.getDots()) {
                        dotsLayoutGiftHome.addView(dot);
                    }
                }

                public void Render() {
                    viewPagerGiftHome.invalidate();
                    dotsLayoutGiftHome.removeAllViews();

                    for (TextView dot : sliderGiftAdapter.getDots()) {
                        dotsLayoutGiftHome.addView(dot);
                    }
                }

                public void Switch() {
                    if (viewPagerGiftHome.getCurrentItem() < sliderGiftAdapter.getCount() - 1){
                        viewPagerGiftHome.setCurrentItem(viewPagerGiftHome.getCurrentItem() + 1,
                                             true);
                        viewPagerGiftHome.setPageTransformer(true ,
                                                             new ZoomOutPageTransformer());
                    } else {
                        viewPagerGiftHome.setCurrentItem(0, true);
                        viewPagerGiftHome.setPageTransformer(true ,
                                                             new ZoomOutPageTransformer());
                    }

                    for (int i = 0; i < sliderGiftAdapter.getCount(); ++i){
                        if (i == viewPagerGiftHome.getCurrentItem()) {
                            sliderGiftAdapter.getDots().get(i).setTextColor(getActivity().getResources().getIntArray(R.array.array_dot_active)[0]);
                        } else {
                            sliderGiftAdapter.getDots().get(i).setTextColor(getActivity().getResources().getIntArray(R.array.array_dot_inactive)[0]); // set dùm cái invalid color
                        }
                    }
                }
            });

            apiClient.NEW_GIFT_EXCHANGE_CALL("Bearer " + token).enqueue(sliderGiftAdapter);
            viewPagerGiftHome.addOnPageChangeListener(sliderGiftAdapter);
        }

        viewPagerGiftHome.setAdapter(sliderGiftAdapter);
        viewPagerPostHome.setAdapter(sliderFeedBackAdapter);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);



        if (sliderGiftAdapter != null) sliderGiftAdapter.enable();
        if (sliderFeedBackAdapter != null) sliderFeedBackAdapter.enable();


        btnAllRankHome.setOnClickListener(this);
        btnSendCoinHome.setOnClickListener(this);
        btnGiftExchange.setOnClickListener(this);
        btnAllComment.setOnClickListener(this);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderGiftAdapter.disable();
        sliderFeedBackAdapter.disable();
    }

    @Override
    public void onStop() {
        super.onStop();
        sliderGiftAdapter.disable();
        sliderFeedBackAdapter.disable();
    }

    @Override
    public void onStart() {
        super.onStart();
        cardInfoUser();
        infoUserLogin();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewPagerGiftHome.postDelayed(new Runnable() {
            @Override
            public void run() {
                sliderGiftAdapter.enable();
            }
        },100);
        viewPagerPostHome.postDelayed(new Runnable() {
            @Override
            public void run() {
                sliderFeedBackAdapter.enable();
            }
        },100);
    }
    private void initView(){ // mappingView - assignView - initView
        textNameCompany = view.findViewById(R.id.textNameCompanyHomeFragment);
        textHistory = view.findViewById(R.id.textHistoryHomeFragment);
        textInfo = view.findViewById(R.id.textInfo);
        imgAvatar = view.findViewById(R.id.imgAvataCardHomeFragment);
        btnAllRankHome = view.findViewById(R.id.btnAllRankHome);
        btnSendCoinHome = view.findViewById(R.id.btnSendCoinHome);
        btnAllComment = view.findViewById(R.id.btnAllCommentHome);
        tabLayout = getActivity().findViewById(R.id.tabLayout);
        textCoin = view.findViewById(R.id.textNumCoin);
        viewPagerGiftHome = view.findViewById(R.id.view_pager);
        viewPagerPostHome = view.findViewById(R.id.view_pager_post);
        dotsLayoutPostHome = view.findViewById(R.id.layoutDotsPost);
        dotsLayoutGiftHome = view.findViewById(R.id.layoutDots);
        btnGiftExchange = view.findViewById(R.id.btnGiftExchangeHome);
        rcUserRank = view.findViewById(R.id.rcView);
    }
    private void cardInfoUser(){
        final Call<CardInfo> cardInfoCall = apiClient.CARD_INFO_CALL("Bearer " + token);
        cardInfoCall.enqueue(new Callback<CardInfo>() {
            @Override
            public void onResponse(Call<CardInfo> call, Response<CardInfo> response) {
                CardInfo cardInfo = response.body();

                timeLogin = response.headers().get("Date");
                timestamp = timeStamp(timeLogin);
                if (cardInfo == null){
                    return;
                }
                CardInfo.Data data = cardInfo.getData();
                if (data != null){
                    CardInfo.LastTransaction lastTransaction = data.getLastTransaction();
                    type = lastTransaction.getType();
                    token_amount = lastTransaction.getToken_amount();
                    user_id = lastTransaction.getUser_id();
                    target_user_id = lastTransaction.getTarget_user_id();

                    CardInfo.Company company = data.getCompany();
                    code = company.getCode();

                    CardInfo.User user = data.getUser();
                    checkin_opened_at = user.getCheckin_opened_at();
                    checkin_closed_at = user.getCheckin_closed_at();
                    has_checked_in = user.isHas_checked_in();
                    token_amount_user = user.getToken_amount();

                    infoCoinUser(id, user_id, token_amount, code);
                    checkInUser(has_checked_in, token_amount_user);
                }
            }

            @Override
            public void onFailure(Call<CardInfo> call, Throwable t) {

            }
        });
    }

    private void infoCoinUser(int id, int user_id, int token, String nameCompany){

        if (id == user_id){
            HomeFragment.textHistory.setText("-"+token);
        }else {
            HomeFragment.textHistory.setText("+"+token);
        }
        HomeFragment.textNameCompany.setText(nameCompany);
    }

    private void checkInUser(boolean checkIn, int token){
        if (!checkIn){
            displayDialog();
            HomeFragment.textCoin.setText(""+token);
        }
    }



    private long timeStamp(String currentTime){
        final String NEW_FORMAT = "EEE, dd MMM yyyy hh:mm:ss z";

        try {
            DateFormat format = new SimpleDateFormat(NEW_FORMAT);
            Date currTime  = format.parse(currentTime);
            long sec = TimeUnit.MILLISECONDS.toSeconds(currTime.getTime());
            return sec;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }



    private String getCurrentDate(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return sdf.format(date);
    }

    private int getCurrentHour(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("HH"); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return Integer.valueOf(sdf.format(date));
    }

    private Dialog contentDialog(){
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_custom);
        dialog.setCancelable(false);

        btnCheckIn = dialog.findViewById(R.id.linearCheck);
        textDay = dialog.findViewById(R.id.textTime);
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                timestamp = timestamp+1;
                Log.d("AAA", timestamp +"");
                textDay.setText(getCurrentDate(timestamp));
            }
        };
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, 1000, 1000);
        }


        btnCheckIn = dialog.findViewById(R.id.linearCheck);
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInUser();
                dialog.dismiss();

            }
        });

        return dialog;
    }

    private void displayDialog(){

        if (getCurrentHour(timestamp) > 6 && getCurrentHour(timestamp) < 8 ){
            if (checkInCoin == null){
                checkInCoin = contentDialog();
            }
            checkInCoin.show();
        }

    }

    private void infoUserLogin(){
        final Call<UserLogin> userLoginCall = apiClient.USER_LOGIN_CALL("Bearer " + token);
        userLoginCall.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, Response<UserLogin> response) {
                UserLogin infoUser = response.body();
                if (infoUser!= null) {
                    id = infoUser.getData().getId();
                    name = infoUser.getData().getName();
                    coinUser = infoUser.getData().getCoin();
                    avatar = infoUser.getData().getAvatar();
                    email = infoUser.getData().getEmail();
                }

                if (avatar == null){
                    avatar = ApiService.url+"_nuxt/img/a000933.png";
                    Picasso.get().load(avatar).into(imgAvatar);
                }else {
                    avatar = ApiService.url_path+infoUser.getData().getAvatar();
                    Picasso.get().load( avatar).into(imgAvatar);
                }
                if (coinUser != null){
                    editHistory.putInt(SAVE_COIN_HISTORY, Integer.valueOf(coinUser));
                    editHistory.apply();
                }



                textInfo.setText(name);
                textCoin.setText(coinUser);
            }
            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void checkInUser(){
        Call<CheckInUser> checkInUserCall = apiClient.CHECK_IN_USER_CALL("Bearer " + token);
        checkInUserCall.enqueue(new Callback<CheckInUser>() {
            @Override
            public void onResponse(Call<CheckInUser> call, Response<CheckInUser> response) {
                CheckInUser checkInUser = response.body();
                if (checkInUser!= null){
                    CheckInUser.Data data = checkInUser.getData();
                    if (data!= null){
                        textCoin.setText(""+ data.getToken_amount());
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckInUser> call, Throwable t) {

            }
        });
    }

    private void topRankUser(){
        final Call<TopRank> topRankCall = apiClient.TOP_RANK_CALL("Bearer " + token);
        topRankCall.enqueue(new Callback<TopRank>() {
            @Override
            public void onResponse(Call<TopRank> call, Response<TopRank> response) {
                listItemUser = new ArrayList<>();

                TopRank allRankUser = response.body();

                if (allRankUser!= null) {
                    List<ItemUserRank> dataRank = allRankUser.dataTopRank;

                    for (ItemUserRank itemUserRank : dataRank) {
                        listItemUser.add(new ItemUser(itemUserRank.rank_no,
                                itemUserRank.avatar_path,
                                itemUserRank.name,
                                itemUserRank.token_amount,
                                itemUserRank.is_token_increase));
                    }
                }
                userRankAdapter = new UserRankAdapter(getActivity(), listItemUser);
                rcUserRank.setAdapter(userRankAdapter);
                rcUserRank.setLayoutManager(layoutManager);
                userRankAdapter.notifyDataSetChanged();
            }


            @Override
            public void onFailure(Call<TopRank> call, Throwable t) {
                call.cancel();

            }
        });

    }

    @Override
    public void setCoin(int count) {
        textCoin.setText(String.valueOf(count));
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAllRankHome:
                Intent intent = new Intent(getActivity(), RankActivity.class);
                intent.putExtra(LIST_USER, listItemUser);
                startActivity(intent);
                sliderGiftAdapter.disable();
                sliderFeedBackAdapter.disable();
                break;
            case R.id.btnSendCoinHome:
                FragmentTransaction ftSendCoin = getFragmentManager().beginTransaction();
                ftSendCoin.replace(R.id.view_pager, new SendCoinsFragment());
                ftSendCoin.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftSendCoin.addToBackStack(null);
                ftSendCoin.commit();
                sliderGiftAdapter.disable();
                sliderFeedBackAdapter.disable();
                tabLayout.getTabAt(HomeActivity.switchAt(HomeActivity.SWITCH_SEND_COIN_FRAGMENT)).select();
                break;
            case R.id.btnAllCommentHome:
                FragmentTransaction ftAllComment = getFragmentManager().beginTransaction();
                ftAllComment.replace(R.id.view_pager,new FeedBackFragment());
                ftAllComment.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftAllComment.addToBackStack(null);
                ftAllComment.commit();
                sliderGiftAdapter.disable();
                sliderFeedBackAdapter.disable();
                tabLayout.getTabAt(HomeActivity.switchAt(HomeActivity.SWITCH_IDEA_FRAGMENT)).select();
                break;
            case R.id.btnGiftExchangeHome:
                FragmentTransaction ftGiftExchangeHome = getFragmentManager().beginTransaction();
                ftGiftExchangeHome.replace(R.id.view_pager,new GiftExchangeFragment());
                ftGiftExchangeHome.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ftGiftExchangeHome.addToBackStack(null);
                ftGiftExchangeHome.commit();
                sliderGiftAdapter.disable();
                sliderFeedBackAdapter.disable();
                tabLayout.getTabAt(HomeActivity.switchAt(HomeActivity.SWITCH_GIFT_FRAGMENT)).select();
                break;
        }
    }
}
