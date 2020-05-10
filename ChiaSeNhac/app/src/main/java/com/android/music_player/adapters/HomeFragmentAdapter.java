package com.android.music_player.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.managers.SongManager;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.viewholder.HomeHolder;

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeHolder> {
    private Activity activity;
    private SongManager mSongManager;
    private SharedPrefsUtils mSharedPrefsUtils;

    public HomeFragmentAdapter(Activity activity){
        this.activity = activity;
        mSharedPrefsUtils = new SharedPrefsUtils(activity);
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(activity);
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
        holder.assignView();
    }

    @Override
    public int getItemCount() {
        return 1;
    }

}
