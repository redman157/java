package com.android.music_player.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.adapters.MusicAdapter;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.utils.SharedPrefsUtils;

public class ListMusicFragment extends Fragment  {
    private View view;
    private RecyclerView mRcMusic;
    private MediaManager mMediaManager;
    private HomeActivity mHomeActivity;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MusicAdapter mMusicAdapter;
    public ListMusicFragment(MusicAdapter musicAdapter){
        mMusicAdapter = musicAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(getContext());
        mHomeActivity = (HomeActivity) getContext();
        mSharedPrefsUtils = new SharedPrefsUtils(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_list_music, container, false);
            initView(view);
        }
        initData();
        return view;
    }

    private void initView(View view){
        mRcMusic = view.findViewById(R.id.rc_music_add);
    }

    private void initData(){
        mRcMusic.setHasFixedSize(true);
        mRcMusic.setItemViewCacheSize(20);
        mRcMusic.setDrawingCacheEnabled(true);
        mRcMusic.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mRcMusic.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL, false));
        mRcMusic.setAdapter(mMusicAdapter);
    }


}
