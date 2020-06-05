package com.android.music_player.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.viewholder.HomeHolder;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeHolder>  {
    private Activity mActivity;
    private MusicManager mMusicManager;
    private SharedPrefsUtils mSharedPrefsUtils;
    private SongAdapter mSongAdapter;


    public HomeFragmentAdapter(Activity activity, SongAdapter songAdapter){
        mActivity = activity;
        mSongAdapter = songAdapter;
        mSharedPrefsUtils = new SharedPrefsUtils(activity);
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(activity);
    }
    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_home, null);
        return new HomeHolder(view,mActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        holder.initView();
        mSongAdapter.notifyDataSetChanged();
        holder.assignView(mSongAdapter);
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
