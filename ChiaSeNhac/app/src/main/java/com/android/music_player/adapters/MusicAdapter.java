package com.android.music_player.adapters;

import android.app.Activity;
import android.support.v4.media.MediaMetadataCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.interfaces.OnConnectMediaId;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.DialogHelper;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.view.MediaItemViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MusicAdapter extends RecyclerView.Adapter<MediaItemViewHolder> {
    private Activity mActivity;
    private Map<String, MediaMetadataCompat> mMusics;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaManager mMediaManager;
    private int limit = 10;
    private boolean isSort;
    public void setLimit(boolean isLimit){
        this.isSort = isLimit;
    }
    public static List<String> keys;
    public MusicAdapter(Activity activity, Map<String, MediaMetadataCompat> musics,
                        boolean isSort) {
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(activity);
        this.mMusics = musics;
        mActivity = activity;
        this.isSort = isSort;
        if (isSort) {
            keys = new ArrayList<>(musics.keySet());
            Collections.sort(keys);
            setMusicList(keys);
        }else {
            keys = new ArrayList<>(musics.keySet());
            Collections.reverse(keys);
            setMusicList(keys);
        }
        mSharedPrefsUtils = new SharedPrefsUtils(mActivity);
    }

    public MusicAdapter(){

    }

    public void setMusicList(List<String> keys) {
        this.keys = keys;
    }

    public List<String> getMusicList(){
        return keys;
    }
    private OnConnectMediaId onConnectMediaId;

    public void setOnConnectMediaIdListener(OnConnectMediaId onConnectMediaId) {
        this.onConnectMediaId = onConnectMediaId;
    }


    public interface OnClickListener {
        void onClick(String type, int position);
    }

    @Override
    public MediaItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music_line, parent, false);
        // If the state of convertView is different, we need to adapt the view to the
        // new state.
        return new MediaItemViewHolder(view, mActivity);
    }

    @Override
    public void onBindViewHolder(MediaItemViewHolder holder, final int position) {
        final MediaMetadataCompat metadataCompat = mMediaManager.getMetadata(mActivity,keys.get(position));
        holder.assignData(metadataCompat);
        holder.mLinearMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* bắt sự kiện click để bật nhạc*/
                onConnectMediaId.onChangeMediaId(metadataCompat.getDescription().getMediaId());
                mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID,
                        metadataCompat.getString(Constants.METADATA.AlbumID));
            }
        });

        holder.mBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogHelper.showDialogChangeMusic(mActivity,metadataCompat);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (!isSort){
            if (mMusics.size() < limit){
                return mMusics.size();
            }else {
                return limit;
            }
        }else {
            return mMusics.size();
        }
    }

}