package com.android.music_player.activities;

import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.utils.Constants;
import com.android.music_player.R;
import com.android.music_player.adapters.EqualizerAdapter;
import com.android.music_player.adapters.EqualizerAdapter.OnClickItemListener;
import com.android.music_player.utils.SharedPrefsUtils;

import java.util.ArrayList;

public class EqualizerActivity extends AppCompatActivity implements OnClickListener,
        OnClickItemListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
    private ImageButton mImbBackMusic;
    private SharedPrefsUtils mSharedPrefsUtils;
    private int mCurrentEqProfile = 0;
    private EqualizerAdapter mEqualizerAdapter;
    private ArrayList<String> mEqList;
    private ImageView mImgSelection;
    private RecyclerView mRecycleEqualizer;
    private TextView mTextOptionEqualizer;
    private LinearLayout mLinearEqualizer;
    private boolean isClick = false;
    private String item;
    private Button mBtnFlat;
    private SeekBar mBassBoost, mVirtualizer;
    private SeekBar[] sliders = new SeekBar[Constants.VALUE.MAX_SLIDERS];
    private Switch mSwEnabled;
    private int num_sliders = 0;
    private int minEQLevel = 0;
    private int maxEQLevel = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);
        mSharedPrefsUtils = new SharedPrefsUtils(this);
        initView();
        initData();
        mCurrentEqProfile = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.currentEqProfile
                , 0);
        assignView();

    }
    private void initView(){
        sliders[0] = findViewById(R.id.slider_1);
        sliders[1] = findViewById(R.id.slider_2);
        sliders[2] = findViewById(R.id.slider_3);
        sliders[3] = findViewById(R.id.slider_4);
        sliders[4] = findViewById(R.id.slider_5);
        mBassBoost = findViewById(R.id.sb_bass_boost);
        mVirtualizer = findViewById(R.id.sb_virtualizer);
        mBtnFlat = findViewById(R.id.btnFlat);

        mLinearEqualizer = findViewById(R.id.ll_main_equalizer);
        mTextOptionEqualizer = findViewById(R.id.text_title_equalizer);
        mImgSelection = findViewById(R.id.img_selection);
        mRecycleEqualizer = findViewById(R.id.rc_Equalizer);
        mImbBackMusic = findViewById(R.id.imb_BackMusic);
        mSwEnabled = findViewById(R.id.sw_enabled);
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
        mBtnFlat.setOnClickListener(this);
        mSwEnabled.setOnCheckedChangeListener(this);
        mImbBackMusic.setOnClickListener(this);
        mImgSelection.setOnClickListener(this);
        mEqualizerAdapter = new EqualizerAdapter(this, mEqList);
        mEqualizerAdapter.setOnClickItemListener(this);
        mRecycleEqualizer.setAdapter(mEqualizerAdapter);
        mRecycleEqualizer.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mBassBoost.setOnSeekBarChangeListener(this);
        mVirtualizer.setOnSeekBarChangeListener(this);

        setupEqualizerFxAndUI(true);
    }
    private void setupEqualizerFxAndUI(boolean isCheck){
        try {
            Equalizer equalizer = new Equalizer(0,
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0));
            equalizer.setEnabled(isCheck);
            num_sliders = (int) equalizer.getNumberOfBands();
            short[] bands  = equalizer.getBandLevelRange();
            minEQLevel = bands [0];
            maxEQLevel = bands [1];
            for (int i = 0; i < num_sliders && i < Constants.VALUE.MAX_SLIDERS; i++) {
                sliders[i].setOnSeekBarChangeListener(this);
                //slider_labels[i].setText(formatBandLabel(freq_range));
            }
            equalizer.release();
        } catch (Exception ignored) {
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnFlat:
                setFlat();
                break;
            case R.id.imb_BackMusic:
                this.finish();
//                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.img_selection:
                if (isClick){
                    // ẩn list
                    Animation fadeOut = AnimationUtils.loadAnimation(EqualizerActivity.this,
                            R.anim.fadeout);
                    mRecycleEqualizer.setAnimation(fadeOut);
                    mRecycleEqualizer.setVisibility(View.GONE);
                    // hiện thanh tiêu đề
                    mTextOptionEqualizer.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mTextOptionEqualizer.setVisibility(View.VISIBLE);
                            mTextOptionEqualizer.setBackgroundResource(R.color.black);
                        }
                    });
                    // hiện thanh công cụ
                    mLinearEqualizer.setAlpha(1);
                    Animation fadeIn = AnimationUtils.loadAnimation(EqualizerActivity.this,
                            R.anim.fadein);
                    mLinearEqualizer.setAnimation(fadeIn);
                    mLinearEqualizer.setVisibility(View.VISIBLE);

                    mImgSelection.setImageResource(R.drawable.ic_up_arrow_white);
                    isClick = false;
                }else {
                    // hiện list
                    Animation fadeIn = AnimationUtils.loadAnimation(EqualizerActivity.this,
                            R.anim.fadein);
                    mRecycleEqualizer.setAnimation(fadeIn);
                    mRecycleEqualizer.setVisibility(View.VISIBLE);
                    // ẩn thanh tiêu đề
                    mTextOptionEqualizer.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mTextOptionEqualizer.setVisibility(View.GONE);
                            mTextOptionEqualizer.setBackgroundResource(R.color.black);
                        }
                    });

                    // ẩn thanh công cụ
                    mLinearEqualizer.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mLinearEqualizer.setVisibility(View.GONE);
                        }
                    });

                    mImgSelection.setImageResource(R.drawable.ic_down_arrow_white);
                    isClick = true;
                }
                break;
        }
    }

    @Override
    public void onClickItem(String item, int pos) {
        this.item = item;

        // ẩn list
        Animation fadeOut = AnimationUtils.loadAnimation(EqualizerActivity.this,
                R.anim.fadeout);
        mRecycleEqualizer.setAnimation(fadeOut);
        mRecycleEqualizer.setVisibility(View.GONE);
        // NAME_PLAYLIST list sẽ hiện
        mTextOptionEqualizer.setAlpha(1);
        Animation fadeIn = AnimationUtils.loadAnimation(EqualizerActivity.this,
                R.anim.fadein);
        mTextOptionEqualizer.setAnimation(fadeIn);
        mTextOptionEqualizer.setText(item);
        mTextOptionEqualizer.setBackgroundResource(R.color.whiteOpaqueX4);
        mTextOptionEqualizer.setVisibility(View.VISIBLE);
        // chỉnh equalizer sẽ hiện
        mLinearEqualizer.setAlpha(1);
        mLinearEqualizer.setAnimation(fadeIn);
        mLinearEqualizer.setVisibility(View.VISIBLE);

        mImgSelection.setImageResource(R.drawable.ic_up_arrow_white);
        isClick = false;

        if (mCurrentEqProfile != pos) {
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.currentEqProfile, pos);
            mCurrentEqProfile = pos;
        }
        Log.d("Equalizer", mCurrentEqProfile + "profile");
        updateUI();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d("CCC",
                "Equalizer --- onCheckedChanged: "+ mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0) );
        mSharedPrefsUtils.setBoolean(Constants.PREFERENCES.turnEqualizer, isChecked);
        Equalizer equalizer = new Equalizer(0,
                mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0));
        BassBoost bassBoost = new BassBoost(0,
                mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0));
        Virtualizer virtualizer = new Virtualizer(0,
                mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0));

        equalizer.setEnabled(isChecked);
        bassBoost.setEnabled(isChecked);
        virtualizer.setEnabled(isChecked);
        // saving setting for equalizer
        if (isChecked){
            for (int index = 0; index < Constants.VALUE.MAX_SLIDERS; index++){
                equalizer.setBandLevel((short) index,
                        (short) mSharedPrefsUtils.getInteger("profile"+mCurrentEqProfile+"Band"+ index,0));
            }
            bassBoost.setStrength((short) mSharedPrefsUtils.
                    getInteger("bassLevel" + mCurrentEqProfile, 0));
            virtualizer.setStrength((short) mSharedPrefsUtils.
                    getInteger("vzLevel" + mCurrentEqProfile, 0));
        }
        equalizer.release();
        bassBoost.release();
        virtualizer.release();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Equalizer eq = new Equalizer(0, mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0));
        BassBoost bassBoost = new BassBoost(0, mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0));
        Virtualizer virtualizer = new Virtualizer(0, mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0));
        if (seekBar == mBassBoost){
            if (eq.getEnabled()){
                bassBoost.setEnabled(progress > 0);
                bassBoost.setStrength((short) progress);// Already in the right range 0-1000
            }
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.bassLevel + mCurrentEqProfile, progress);
        }else if (seekBar == mVirtualizer){
            if (eq.getEnabled()){
                virtualizer.setEnabled(progress > 0);
                virtualizer.setStrength((short) progress);// Already in the right range 0-1000
            }
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.vzLevel + mCurrentEqProfile, progress);
        }else {
            int new_progress = minEQLevel + (maxEQLevel - minEQLevel ) * progress / 100;
            Log.d("CCC", "Equalizer --- onProgressChanged: "+ new_progress);
            for (int i = 0; i < num_sliders; i++) {
                if (sliders[i] == seekBar) {
                    if (eq.getEnabled()) {
                        eq.setBandLevel((short) i, (short) new_progress);
                    }
                    mSharedPrefsUtils.setInteger("profile" + mCurrentEqProfile + "Band" + i, new_progress);
                    break;
                }
            }
        }
        eq.release();
        bassBoost.release();
        virtualizer.release();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void updateSliders() {
        try {
            for (int i = 0; i < num_sliders; i++) {
                int level =
                        (short) mSharedPrefsUtils.getInteger("profile" + mCurrentEqProfile + "Band" + i,
                                0);
                int pos = 100 * level / (maxEQLevel - minEQLevel) + 50;
                sliders[i].setProgress(pos);
            }
        } catch (Exception ignored) {
        }
    }

    public void updateBassBoost() {
        try {
            mBassBoost.setProgress((short)
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.bassLevel + mCurrentEqProfile,
                            0));
        } catch (Exception ignored) {
        }
    }

    public void updateVirtualizer() {
        try {
            mVirtualizer.setProgress((short)
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.vzLevel + mCurrentEqProfile, 0));
        } catch (Exception ignored) {
        }
    }


    public void updateUI() {
        updateSliders();
        updateBassBoost();
        updateVirtualizer();
        mSwEnabled.setChecked(mSharedPrefsUtils.getBoolean(Constants.PREFERENCES.turnEqualizer, false));
    }

    public void setFlat() {
        try {
            Equalizer eq = new Equalizer(0,
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0));
            BassBoost bassBoost = new BassBoost(0,
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0));
            Virtualizer virtualizer = new Virtualizer(0,
                    mSharedPrefsUtils.getInteger(Constants.PREFERENCES.audio_session_id, 0));
            bassBoost.setEnabled(false);
            bassBoost.setStrength((short) 0);
            virtualizer.setEnabled(false);
            virtualizer.setStrength((short) 0);
            for (int i = 0; i < num_sliders; i++) {
                eq.setBandLevel((short) i, (short) 0);
                mSharedPrefsUtils.setInteger("profile" + mCurrentEqProfile + "Band" + i, 0);
            }
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.bassLevel + mCurrentEqProfile, 0);
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.vzLevel + mCurrentEqProfile, 0);
            updateUI();
            eq.release();
            bassBoost.release();
            virtualizer.release();
        } catch (Exception ignored) {
        }
    }
}
