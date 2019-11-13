package com.jvit.companycoin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jvit.companycoin.api.BuyGift;
import com.jvit.companycoin.fragment.GiftExchangeFragment;
import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.fragment.SendCoinsFragment;
import com.jvit.companycoin.R;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoGiftExchangeActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView imageView, imgBack;
    private TextView textGiftName, textCoin, textRemain, textIntro, textCoinExchange, textAfterExchange;
    private String nameGift, introGift,imageGift;
    private Bundle bundle;
    private int id;
    private SharedPreferences preferencesToken;
    private String token;
    private String SAVE_TOKEN = "SAVE_TOKEN";
    private Button btnDoNotGiftExchange;
    private int numCoinExchange, remainItem;
    private LinearLayout btnGiftExchange;
    private String message;
    public static Toast mToast;
    public final static String ID_GIFT = "id_gift";
    public final static String IMAGE_GIFT = "image_gift";
    public final static String NAME_GIFT = "name_gift";
    public final static String INTRO_GIFT = "intro_gift";
    public final static String PRICE_GIFT = "price_gift";
    public final static String QUANTITY = "quantity";
    public static int coinUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_gift_exchange);
        initView();
        preferencesToken = getSharedPreferences(SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN, HomeFragment.TOKEN_NULL);
        getBundle();

        imgBack.setOnClickListener(this);
        btnDoNotGiftExchange.setOnClickListener(this);
        btnGiftExchange.setOnClickListener(this);
    }

    private void getBundle(){
        Intent intent = getIntent();
        bundle = intent.getExtras();
        if (bundle != null) {
            id = bundle.getInt(ID_GIFT);
            imageGift = bundle.getString(IMAGE_GIFT);
            nameGift = bundle.getString(NAME_GIFT);
            introGift = bundle.getString(INTRO_GIFT);
            numCoinExchange = bundle.getInt(PRICE_GIFT,0);
            remainItem = bundle.getInt(QUANTITY,0);
            setData();
        }
    }
    private void initView(){
        btnGiftExchange = findViewById(R.id.btnInfoGiftAcceptBuy);
        btnDoNotGiftExchange = findViewById(R.id.btnGiftExchangeDoNotBuy);
        imgBack = findViewById(R.id.img_backWork);
        textGiftName = findViewById(R.id.textInfoGiftName);
        textCoin = findViewById(R.id.textInfoGiftCoin);
        textRemain = findViewById(R.id.textInfoGiftRemain);
        textAfterExchange = findViewById(R.id.textAfterCoin);
        textCoinExchange = findViewById(R.id.textInfoGiftExchangeWithCoin);
        textIntro = findViewById(R.id.textInfoGiftIntro);
        imageView = findViewById(R.id.imgInfoGift);
    }
    private void setData(){
        Picasso.get().load(imageGift).into(imageView);
        textGiftName.setText(nameGift);
        textIntro.setText(introGift);
        textCoinExchange.setText(" "+ numCoinExchange +" Coin");
        textAfterExchange.setText(remainItem +" items");
        textRemain.setText("Remain "+ remainItem +" items");
        textCoin.setText(numCoinExchange +" Coin");
    }

    public static void showToast(Context cont, String message) {
        if (mToast == null) {
            mToast = Toast.makeText(cont, message, Toast.LENGTH_SHORT);
        }
        if (!mToast.getView().isShown()) {
            mToast.setText(message);
            mToast.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_backWork:
            case R.id.btnGiftExchangeDoNotBuy:
                finish();
                break;
            case R.id.btnInfoGiftAcceptBuy:
                apiGiftExchange();
                break;
        }
    }

    private void apiGiftExchange() {
        Call<BuyGift> buyGiftCall =
                GiftExchangeFragment.apiClient.BUY_GIFT_CALL(
                        "Bearer "+token,
                        id);
        buyGiftCall.enqueue(new Callback<BuyGift>() {

            @Override
            public void onResponse(Call<BuyGift> call, Response<BuyGift> response) {
                BuyGift buyGift = response.body();
                if (buyGift!= null && Integer.valueOf(HomeFragment.coinUser) > numCoinExchange ){
                    coinUser = buyGift.getData().getToken_amount();
                    HomeFragment.textCoin.setText(""+coinUser);
                    SendCoinsFragment.textCoinLimit.setText("/" + coinUser + " " +getResources().getString(R.string.coin));
                    Toast.makeText(InfoGiftExchangeActivity.this, "Bạn Đã Mua: "+ nameGift, Toast.LENGTH_SHORT).show();
                }else {
                    message = "Bạn vui lòng" +
                    " kiểm lại số coin: "+ " Coin " +
                            "hiện" +
                            " " +
                            "tại là: "+ HomeFragment.coinUser + " và "+
                            "Coin mua sản phẩm: "+nameGift+" " +
                            "là: "+numCoinExchange;

                    showToast(InfoGiftExchangeActivity.this,message );

                }
            }

            @Override
            public void onFailure(Call<BuyGift> call, Throwable t) {
                call.cancel();
                Toast.makeText(InfoGiftExchangeActivity.this, "Vui lòng kiểm tra đường truyền", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
