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

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeHolder> {
    private Activity activity;
    private MusicManager mMusicManager;
    private SharedPrefsUtils mSharedPrefsUtils;
    private SongAdapter songAdapter;
    public HomeFragmentAdapter(Activity activity, SongAdapter songAdapter){
        this.activity = activity;
        this.songAdapter = songAdapter;
        mSharedPrefsUtils = new SharedPrefsUtils(activity);
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(activity);
    }
    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_home, null);
        return new HomeHolder(view,activity);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {
        holder.initView();
        holder.assignView(songAdapter);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

}
