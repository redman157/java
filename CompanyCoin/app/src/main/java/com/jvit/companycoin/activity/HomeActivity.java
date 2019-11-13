package com.jvit.companycoin.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jvit.companycoin.adapter.PageAdapter;

import com.jvit.companycoin.CoinCount;
import com.jvit.companycoin.api.ApiClient;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.api.CardInfo;
import com.jvit.companycoin.api.CheckInUser;
import com.jvit.companycoin.fragment.GiftExchangeFragment;
import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.fragment.NotificationsFragment;
import com.jvit.companycoin.fragment.FeedBackFragment;
import com.jvit.companycoin.fragment.SendCoinsFragment;
import com.jvit.companycoin.R;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class  HomeActivity extends AppCompatActivity{
    private ViewPager viewTab;
    private TabLayout tabLayout;

    private PageAdapter pageAdapter;

    private final String SAVE_COIN_USER = "SAVE_COIN_USER";
    private final String DATE_CHECK_IN = "DATE_CHECK_IN";
    private SharedPreferences preferencesDate,preferencesCoin , preferencesToken;
    private SharedPreferences.Editor editorCoin;
    private LinearLayout linearCheck, homeActivity;
    private TextView title;
    private ImageView icon;
    private CoinCount coinCount;
    private HomeFragment homeFragment;
    private String token, code, checkin_opened_at, checkin_closed_at;
    private int coinHistory, type, token_amount, user_id, target_user_id, token_amount_user;
    private boolean has_checked_in;
    public static final String SWITCH_SEND_COIN_FRAGMENT = "send_coin";
    public static final String SWITCH_IDEA_FRAGMENT = "comment";
    public static final String SWITCH_GIFT_FRAGMENT = "gift";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        homeFragment = new HomeFragment();
        coinCount = homeFragment;

        preferencesCoin = getSharedPreferences(SAVE_COIN_USER, MODE_PRIVATE);
        preferencesDate = getSharedPreferences(DATE_CHECK_IN, MODE_PRIVATE);
        preferencesToken = getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN,HomeFragment.TOKEN_NULL);
        initView();

        tabLayout.setupWithViewPager(viewTab);

        linearCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MyPageActivity.class));
            }
        });
        setupViewPager();


        setupTabLayout();
        onClickItemChange(tabLayout);

        hiddenKeyboard();

    }

    private void onClickItemChange(final TabLayout tabLayout){
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    View view = tabLayout.getTabAt(i).getCustomView();
                    TextView title = view.findViewById(R.id.textTlTitleCompanyCoin);
                    int textColor = (i == tab.getPosition()) ? getResources().getColor(R.color.tablayout_orange) : getResources().getColor(R.color.tablayout_grey);
                    title.setTextColor(textColor);
                    ImageView icon = view.findViewById(R.id.imgTlIconCompanyCoin);
                    icon.setColorFilter(textColor);
                }
                // dieu kiện chuẩn để update icon

//                if (tab.getPosition() == 0){
//                    View view0 = tabLayout.getTabAt(0).getCustomView();
//                    ImageView icon0 = view0.findViewById(R.id.imgTlIconCompanyCoin);
//                    icon0.setImageResource(R.drawable.menu_home_orange);
//
//                    View view1 = tabLayout.getTabAt(1).getCustomView();
//                    ImageView icon1 = view1.findViewById(R.id.imgTlIconCompanyCoin);
//                    icon1.setImageResource(R.drawable.menu_gift_gray);
//
//                    View view2 = tabLayout.getTabAt(2).getCustomView();
//                    ImageView icon2 = view2.findViewById(R.id.imgTlIconCompanyCoin);
//                    icon2.setImageResource(R.drawable.menu_idea_gray);
//
//                    View view3 = tabLayout.getTabAt(3).getCustomView();
//                    ImageView icon3 = view3.findViewById(R.id.imgTlIconCompanyCoin);
//                    icon3.setImageResource(R.drawable.menu_sendcoin_gray);
//
//                    View view4 = tabLayout.getTabAt(4).getCustomView();
//                    ImageView icon4 = view4.findViewById(R.id.imgTlIconCompanyCoin);
//                    icon4.setImageResource(R.drawable.menu_notification_gray);
//                }
//
//                if (tab.getPosition() == 1){
//                    View view0 = tabLayout.getTabAt(0).getCustomView();
//                    ImageView icon0 = view0.findViewById(R.id.imgTlIconCompanyCoin);
//                    icon0.setImageResource(R.drawable.ic_menu_home_gray);
//
//                    View view1 = tabLayout.getTabAt(1).getCustomView();
//                    ImageView icon1 = view1.findViewById(R.id.imgTlIconCompanyCoin);
//                    icon1.setImageResource(R.drawable.ic_menu_gift_orange);
//
//                    View view2 = tabLayout.getTabAt(2).getCustomView();
//                    ImageView icon2 = view2.findViewById(R.id.imgTlIconCompanyCoin);
//                    icon2.setImageResource(R.drawable.menu_idea_gray);
//
//                    View view3 = tabLayout.getTabAt(3).getCustomView();
//                    ImageView icon3 = view3.findViewById(R.id.imgTlIconCompanyCoin);
//                    icon3.setImageResource(R.drawable.menu_sendcoin_gray);
//
//                    View view4 = tabLayout.getTabAt(4).getCustomView();
//                    ImageView icon4 = view4.findViewById(R.id.imgTlIconCompanyCoin);
//                    icon4.setImageResource(R.drawable.menu_notification_gray);
//                }


//                if (tab.getPosition() == 0) {
//                    view = getTabView(0);
//                    TextView title = view.findViewById(R.id.textTlTitleCompanyCoin);
//                    ImageView icon = view.findViewById(R.id.imgTlIconCompanyCoin);
//
//                    title.setTextColor(getResources().getColor(R.color.tablayout_grey));
//                    icon.setImageResource(R.drawable.ic_menu_home_gray);
//
//                    tab.setCustomView(view);
//                } else if (tab.getPosition() == 1) {
//                    view = getTabView(1);
//                    TextView title = view.findViewById(R.id.textTlTitleCompanyCoin);
//                    ImageView icon = view.findViewById(R.id.imgTlIconCompanyCoin);
//
//                    title.setTextColor(getResources().getColor(R.color.tablayout_orange));
//                    icon.setImageResource(R.drawable.ic_menu_gift_orange);
//                    tab.setCustomView(view);
//                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void hiddenKeyboard(){
        KeyboardVisibilityEvent.setEventListener(HomeActivity.this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if (isOpen){
                    tabLayout.setVisibility(View.GONE);
                }else {
                    tabLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    private void initView(){
        homeActivity = findViewById(R.id.homeActivity);
        linearCheck = findViewById(R.id.linearMyPage);

        viewTab = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
    }



    private void setupViewPager(){
        pageAdapter = new PageAdapter(getSupportFragmentManager(),0);
        pageAdapter.addFragment(homeFragment, getResources().getString(R.string.home));
        pageAdapter.addFragment(new GiftExchangeFragment(),getResources().getString(R.string.gift_exchange));
        pageAdapter.addFragment(new FeedBackFragment(),getResources().getString(R.string.feedback));
        pageAdapter.addFragment(new SendCoinsFragment(), getResources().getString(R.string.send_coins));
        pageAdapter.addFragment(new NotificationsFragment(), getResources().getString(R.string.notifications));
        viewTab.setAdapter(pageAdapter);
    }


    private void setupTabLayout(){
        tabLayout.setupWithViewPager(viewTab);
        int length = tabLayout.getTabCount();

        for (int i = 0; i <length;i++){
            tabLayout.getTabAt(i).setCustomView(getTabView(i));
        }
    }

    public View getTabView(int position){
        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.custom_tablayout_company, null);
        title = view.findViewById(R.id.textTlTitleCompanyCoin);
        icon = view.findViewById(R.id.imgTlIconCompanyCoin);

        ArrayList<Integer> listIcon = iconTabLayout();
        ArrayList<String> listTitle = titleTabLayout();
        if (position == 0){
            title.setTextColor(getResources().getColor(R.color.tablayout_orange));
        }
        icon.setImageResource(listIcon.get(position));
        title.setText(listTitle.get(position));

        return view;

    }
    private ArrayList<Integer> iconTabLayout(){
        ArrayList<Integer> listIcon = new ArrayList<>();
        listIcon.add(R.drawable.menu_home_orange);
        listIcon.add(R.drawable.menu_gift_gray);
        listIcon.add(R.drawable.menu_idea_gray);
        listIcon.add(R.drawable.menu_sendcoin_gray);
        listIcon.add(R.drawable.menu_notification_gray);
        return listIcon;
    }

    private ArrayList<String> titleTabLayout(){
        ArrayList<String> listTitle = new ArrayList<>();
        listTitle.add(getResources().getString(R.string.home));
        listTitle.add(getResources().getString(R.string.redeem_gift));
        listTitle.add(getResources().getString(R.string.feedback));
        listTitle.add(getResources().getString(R.string.send_coins));
        listTitle.add(getResources().getString(R.string.notifications));

        return listTitle;
    }

    public static int switchAt(String value){
        if (value.equals(HomeActivity.SWITCH_GIFT_FRAGMENT)) {
            return 1;
        }else if (value.equals(HomeActivity.SWITCH_IDEA_FRAGMENT)){
            return 2;
        }else if (value.equals(HomeActivity.SWITCH_SEND_COIN_FRAGMENT)){
            return 3;
        }
        return -1;
    }
    private void showDialog() {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exit);
        TextView textTitle = dialog.findViewById(R.id.textTitleDiaglogExit);
        TextView btnYes = dialog.findViewById(R.id.btnYesDiaglogExit);
        TextView btnNo = dialog.findViewById(R.id.btnNoDiaglogExit);

        textTitle.setText(getResources().getString(R.string.exit_application));
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(a);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    @Override
    public void onBackPressed() {
        showDialog();
    }


}

