package com.jvit.companycoin.activity;

import androidx.appcompat.app.AppCompatActivity;

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

import com.jvit.companycoin.api.BuyGift;
import com.jvit.companycoin.fragment.HomeFragment;
import com.jvit.companycoin.fragment.SendCoinsFragment;
import com.jvit.companycoin.fragment.sliderItemFragment.ItemGiftFragment;
import com.jvit.companycoin.object.GiftSlider;
import com.jvit.companycoin.api.ApiService;
import com.jvit.companycoin.R;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoGiftSliderActivity extends AppCompatActivity implements View.OnClickListener{
    private GiftSlider giftSlider;
    private ImageView imageView, imgBack;
    private LinearLayout btnBuyGift;
    private TextView textGiftName, textCoin, textRemain, textIntro, textCoinExchange, textAfterExchange;
    private Bundle bundle;
    private Button btnNotBuyGift;
    public static int coinUser;
    private SharedPreferences preferencesToken;
    private String token;
    private String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_gift_slider);
        initView();

        preferencesToken = getSharedPreferences(HomeFragment.SAVE_TOKEN, Context.MODE_PRIVATE);
        token = preferencesToken.getString(HomeFragment.TOKEN, HomeFragment.TOKEN_NULL);

        Intent intent = getIntent();
        bundle = intent.getExtras();
        if (bundle != null) {
            giftSlider = (GiftSlider) bundle.getSerializable(ItemGiftFragment.SLIDER_DATA);
            setDataSlider();
        }
        btnBuyGift.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        btnNotBuyGift.setOnClickListener(this);
    }
    private void setDataSlider(){
        giftSlider = (GiftSlider) bundle.getSerializable(ItemGiftFragment.SLIDER_DATA);
        Picasso.get().load(ApiService.url_path+ giftSlider.getImage()).into(imageView);
        textGiftName.setText(giftSlider.getTitle());
        textIntro.setText(giftSlider.getDescription());
        textCoinExchange.setText(giftSlider.getExchangeCoin()+" Coin");
        textAfterExchange.setText(giftSlider.getRemain()+" Items");
        textRemain.setText(getResources().getString(R.string.remaining)+ " "+ giftSlider.getRemain()+" Items");
        textCoin.setText(""+ giftSlider.getExchangeCoin()+" Coin");
    }
    private void initView(){
        btnBuyGift = findViewById(R.id.linearBuyGiftSlider);
        btnNotBuyGift = findViewById(R.id.btnGiftExchangeSliderDoNotBuy);
        imgBack = findViewById(R.id.img_backWorkSlider);
        textGiftName = findViewById(R.id.textInfoGiftNameSlider);
        textCoin = findViewById(R.id.textInfoGiftCoinSlider);
        textRemain = findViewById(R.id.textInfoGiftRemainSlider);
        textAfterExchange = findViewById(R.id.textAfterCoinSlider);
        textCoinExchange = findViewById(R.id.textInfoGiftExchangeWithCoinSlider);
        textIntro = findViewById(R.id.textInfoGiftIntroSlider);
        imageView = findViewById(R.id.imgInfoGiftSlider);
    }

    private void buyGift(){
        Call<BuyGift> buyGiftCall = HomeFragment.apiClient.BUY_GIFT_CALL(
                "Bearer "+ token,
                giftSlider.getId());
        buyGiftCall.enqueue(new Callback<BuyGift>() {
            @Override
            public void onResponse(Call<BuyGift> call, Response<BuyGift> response) {
                BuyGift buyGift = response.body();

                if (buyGift != null && Integer.valueOf(HomeFragment.coinUser) > giftSlider.getExchangeCoin()){
                    coinUser = buyGift.getData().getToken_amount();
                    HomeFragment.textCoin.setText(""+coinUser);
                    SendCoinsFragment.textCoinLimit.setText("/" + coinUser + " " +getResources().getString(R.string.coin));
                    Toast.makeText(InfoGiftSliderActivity.this, "Bạn Đã Mua: "+ giftSlider.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }else {
                    message = "Bạn vui lòng" +
                            " kiểm lại số coin: "+ " Coin " +
                            "hiện" +
                            " " +
                            "tại là: "+ HomeFragment.coinUser + " và "+"Coin mua sản phẩm: "+ giftSlider.getTitle()+" là: "+ giftSlider.getExchangeCoin();
                    InfoGiftExchangeActivity.showToast(InfoGiftSliderActivity.this, message);
                }
            }

            @Override
            public void onFailure(Call<BuyGift> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGiftExchangeSliderDoNotBuy:
            case R.id.img_backWorkSlider:
                finish();
                break;
            case R.id.linearBuyGiftSlider:
                buyGift();
                break;
        }
    }
}
