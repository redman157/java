package com.droidheat.musicplayer.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.adapters.EqualizerAdapter;
import com.droidheat.musicplayer.adapters.EqualizerAdapter.OnClickItemListener;
import com.droidheat.musicplayer.manager.SharedPrefsManager;

import java.util.ArrayList;

public class EqualizerActivity extends AppCompatActivity implements OnClickListener, OnClickItemListener {
    private ImageButton mImbBackMusic;
    private SharedPrefsManager mSharedPrefsManager;
    private SeekBar[] sliders = new SeekBar[Constants.VALUE.MAX_SLIDERS];
    private SeekBar mBassBoost = null;
    private SeekBar mVirtualizerSeekBar = null;
    private int mCurrentEqProfile = 0;
    private EqualizerAdapter mEqualizerAdapter;
    private ArrayList<String> mEqList;
    private ImageView img_selection;
    private RecyclerView mRcEqualizer;
    private TextView text_title_equalizer;
    private LinearLayout ll_main_equalizer;
    private boolean isClick = false;
    private String item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);
        initView();
        initData();
        assignView();
    }
    private void initView(){
        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);
        ll_main_equalizer = findViewById(R.id.ll_main_equalizer);
        text_title_equalizer = findViewById(R.id.text_title_equalizer);
        img_selection = findViewById(R.id.img_selection);
        mRcEqualizer = findViewById(R.id.rc_Equalizer);
        mImbBackMusic = findViewById(R.id.imb_BackMusic);
    }

    private void initData(){
        mEqList = new ArrayList<>();
        mEqList.add("Profile 1");
        mEqList.add("Profile 2");
        mEqList.add("Profile 3");
        mEqList.add("Profile 4");
        mEqList.add("Profile 5");
    }
    private void assignView(){
        mImbBackMusic.setOnClickListener(this);
        img_selection.setOnClickListener(this);
        mEqualizerAdapter = new EqualizerAdapter(this, mEqList);
        mEqualizerAdapter.setOnClickItemListener(this);
        mRcEqualizer.setAdapter(mEqualizerAdapter);
        mRcEqualizer.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imb_BackMusic:
                this.finish();
//                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.img_selection:
                if (isClick){
                    Animation fadeOut = AnimationUtils.loadAnimation(EqualizerActivity.this,
                            R.anim.fadeout);
                    mRcEqualizer.setAnimation(fadeOut);
                    mRcEqualizer.setVisibility(View.GONE);

                    ll_main_equalizer.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            text_title_equalizer.setVisibility(View.VISIBLE);
                            text_title_equalizer.setBackgroundResource(R.color.black);
                        }
                    });

                    Animation fadeIn = AnimationUtils.loadAnimation(EqualizerActivity.this,
                            R.anim.fadein);
                    ll_main_equalizer.setAlpha(1);
                    ll_main_equalizer.setAnimation(fadeIn);
                    ll_main_equalizer.setVisibility(View.VISIBLE);

                    img_selection.setImageResource(R.drawable.ic_up_arrow_white);
                    isClick = false;
                }else {
                    Animation fadeIn = AnimationUtils.loadAnimation(EqualizerActivity.this,
                            R.anim.fadein);
                    mRcEqualizer.setAnimation(fadeIn);

                    text_title_equalizer.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            text_title_equalizer.setVisibility(View.GONE);
                            text_title_equalizer.setBackgroundResource(R.color.black);
                        }
                    });


                    ll_main_equalizer.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            ll_main_equalizer.setVisibility(View.GONE);
                        }
                    });

                    mRcEqualizer.setVisibility(View.VISIBLE);

                    img_selection.setImageResource(R.drawable.ic_down_arrow_white);
                    isClick = true;
                }
                break;
        }
    }

    @Override
    public void onClickItem(String item) {
        this.item = item;

        text_title_equalizer.setAlpha(1);
        Animation fadeOut = AnimationUtils.loadAnimation(EqualizerActivity.this,
                R.anim.fadeout);
        mRcEqualizer.setAnimation(fadeOut);
        mRcEqualizer.setVisibility(View.GONE);

        Animation fadeIn = AnimationUtils.loadAnimation(EqualizerActivity.this,
                R.anim.fadein);

        text_title_equalizer.setAnimation(fadeIn);
        text_title_equalizer.setText(item);
        text_title_equalizer.setBackgroundResource(R.color.whiteOpaqueX4);
        text_title_equalizer.setVisibility(View.VISIBLE);

        ll_main_equalizer.setAlpha(1);
        ll_main_equalizer.setAnimation(fadeIn);
        ll_main_equalizer.setVisibility(View.VISIBLE);
        
        img_selection.setImageResource(R.drawable.ic_up_arrow_white);
        isClick = false;
    }
}
