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
import com.android.music_player.adapters.AlbumAdapter;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.utils.SharedPrefsUtils;

public class ListAlbumFragment extends Fragment {
    private View view;
    private RecyclerView mFastScrollRecyclerView;
    private MediaManager mMediaManager;
    private HomeActivity mHomeActivity;
    private SharedPrefsUtils mSharedPrefsUtils;
    private AlbumAdapter mAlbumAdapter;
    public ListAlbumFragment(AlbumAdapter albumAdapter){
        this.mAlbumAdapter = albumAdapter;
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
            view = inflater.inflate(R.layout.fragment_list_album, container, false);
            initView(view);
        }
        initData();
        return view;
    }

    private void initView(View view){
        mFastScrollRecyclerView = view.findViewById(R.id.rc_album_add);
    }

    private void initData(){
        mFastScrollRecyclerView.setHasFixedSize(true);
        mFastScrollRecyclerView.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.VERTICAL, false));
        mFastScrollRecyclerView.setAdapter(mAlbumAdapter);
    }

}
