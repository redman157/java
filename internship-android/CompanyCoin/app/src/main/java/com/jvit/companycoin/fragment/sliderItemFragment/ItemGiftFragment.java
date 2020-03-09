package com.jvit.companycoin.fragment.sliderItemFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jvit.companycoin.object.GiftSlider;
import com.jvit.companycoin.activity.InfoGiftSliderActivity;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.ApiService;
import com.squareup.picasso.Picasso;

public class ItemGiftFragment extends Fragment  {
    public ItemGiftFragment(){}
    private View view;
    private TextView textTitle, textRemain, btnAccept;
    private ImageView imgGift;
    private String image,titleGift;
    private int remainGift, coinGift, id;
    private GiftSlider home;
    private final static String ID = "id";
    private final static String IMAGE = "image";
    private final static String TITLE = "title";
    private final static String REMAIN = "remain";
    private final static String COIN = "coin";
    private final static String DATA_GIFT = "data_gift";
    public final static String SLIDER_DATA = "slider_data";

    static ItemGiftFragment newInstance(GiftSlider giftSlider){
        ItemGiftFragment itemGiftFragment = new ItemGiftFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ID, giftSlider.getId());
        bundle.putString(IMAGE, giftSlider.getImage());
        bundle.putString(TITLE, giftSlider.getTitle());
        bundle.putInt(REMAIN, giftSlider.getRemain());
        bundle.putInt(COIN, giftSlider.getExchangeCoin());
        bundle.putSerializable(DATA_GIFT, giftSlider);
        itemGiftFragment.setArguments(bundle);
        return itemGiftFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getInt(ID);
        image = getArguments().getString(IMAGE);
        titleGift = getArguments().getString(TITLE);
        remainGift = getArguments().getInt(REMAIN);
        coinGift = getArguments().getInt(COIN);
        home = (GiftSlider) getArguments().getSerializable(DATA_GIFT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.slide_gift, container, false);
        initView();
        Picasso.get().load(ApiService.url_path+image).into(imgGift);
        textTitle.setText(""+titleGift);
        textRemain.setText( getResources().getString(R.string.remaining)+" "+remainGift+ " "+ getResources().getString(R.string.items));
        btnAccept.setText(coinGift+ getResources().getString(R.string.coin));

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), InfoGiftSliderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(SLIDER_DATA, home);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return view;
    }

    private void initView(){
        textTitle = view.findViewById(R.id.textItemTitleGiftHome);
        textRemain = view.findViewById(R.id.textItemRemainGiftHome);
        imgGift = view.findViewById(R.id.imgItemGiftHome);
        btnAccept = view.findViewById(R.id.btnItemAcceptGiftHome);
    }

}
