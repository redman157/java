package com.android.music_player.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.models.OptionItem;
import com.android.music_player.R;
import com.android.music_player.adapters.OptionAdapter;

import java.util.ArrayList;

public class SettingsFragment extends Fragment implements OptionAdapter.OnClickListener{
    private View view;
    private RecyclerView mRcLibrary, mRcSettings;
    private LinearLayout mLinearUser;
    private TextView mTextNameUser;
    private ImageView mImageUser;
    private ArrayList<OptionItem> mLibrary, mSettings;
    private OptionAdapter mLibraryAdapter, mSettingAdapter;
    private RecyclerView mRcSetting, mRcLirary;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_settings, container, false);
            initView(view);
        }
        assignView();
        return view;
    }

    public void initView(View view){
        mTextNameUser = view.findViewById(R.id.text_name_user);
        mImageUser = view.findViewById(R.id.img_user);
        mLinearUser = view.findViewById(R.id.line_info_login);
        mRcLibrary = view.findViewById(R.id.rc_info_library);
        mRcSettings = view.findViewById(R.id.rc_settings);
    }

    private void assignView(){
        initData();
        mLibraryAdapter = new OptionAdapter(getContext(), mLibrary);
        mLibraryAdapter.setOnClickListener(this);

        mSettingAdapter = new OptionAdapter(getContext(), mSettings);
        mSettingAdapter.setOnClickListener(this);

        mRcSettings.setAdapter(mSettingAdapter);
        mRcSettings.setNestedScrollingEnabled(false);
        mRcSettings.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        mRcLibrary.setAdapter(mLibraryAdapter);
        mRcLibrary.setNestedScrollingEnabled(false);
        mRcLibrary.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

    }
    private void initData(){
        mLibrary = new ArrayList<>();
        mLibrary.add(new OptionItem(R.drawable.ic_music_notes_padded, "All The Songs"));
        mLibrary.add(new OptionItem(R.drawable.ic_download_24px, "DownLoading"));
        mLibrary.add(new OptionItem(R.drawable.app_add_playlist, "All PlayList"));
        mLibrary.add(new OptionItem(R.drawable.ic_favorite_accent_24dp, "Favorites"));
        mLibrary.add(new OptionItem(R.drawable.ic_time_black_24dp, "Recently played"));
        mSettings = new ArrayList<>();
        mSettings.add(new OptionItem(R.drawable.ic_settings_icon_24px, "Settings"));
        mSettings.add(new OptionItem(R.drawable.ic_info_outline_accent_24dp, "Introduce"));
    }

    @Override
    public void onClick(int position) {
        switch (position){
            case 0:
                /*Intent iViewAll = new Intent(getContext(), SongActivity.class);
                iViewAll.putExtra(Constants.INTENT.TYPE_MUSIC, Constants.VALUE.ALL_SONGS);
                getActivity().finish();
                getActivity().startActivity(iViewAll);*/
                break;
        }
    }
}
