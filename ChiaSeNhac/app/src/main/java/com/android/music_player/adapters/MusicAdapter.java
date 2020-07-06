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
    private final Activity mActivity;
    private Map<String, MediaMetadataCompat> mMusics;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaManager mMediaManager;
    private int limit = 10;
    private boolean isSort;
    public void setLimit(boolean isLimit){
        this.isSort = isLimit;
    }
    private List<String> keys;
    public MusicAdapter(Activity activity, Map<String, MediaMetadataCompat> musics,
                        boolean isSort) {
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(activity);
        this.mMusics = musics;
        mActivity = activity;
        this.isSort = isSort;
        keys = new ArrayList<>(musics.keySet());
        if (isSort) {
            Collections.sort(keys);
        }else {
            Collections.reverse(keys);
        }
        mSharedPrefsUtils = new SharedPrefsUtils(mActivity);
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
        final MediaMetadataCompat description = mMediaManager.getMetadata(mActivity,keys.get(position));
        holder.assignData(description);
        holder.mLinearMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* bắt sự kiện click để bật nhạc*/
                onConnectMediaId.onChangeMediaId(description.getDescription().getMediaId());
                mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID,
                        description.getString(Constants.METADATA.AlbumID));
            }
        });

        holder.mBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showDialogChangeMusic(mActivity,description.getDescription().getMediaId());
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