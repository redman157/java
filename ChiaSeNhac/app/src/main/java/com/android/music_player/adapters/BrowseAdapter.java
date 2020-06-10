package com.android.music_player.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.DialogHelper;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.viewholder.MediaItemViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BrowseAdapter extends RecyclerView.Adapter<MediaItemViewHolder> {
    private final Activity mActivity;
    private Map<String, MediaMetadataCompat> mMusics;
    private SharedPrefsUtils mSharedPrefsUtils;

    private int limit = 10;
    private boolean isSort;
    public void setLimit(boolean isLimit){
        this.isSort = isLimit;
    }
    public static final int STATE_INVALID = -1;
    public static final int STATE_NONE = 0;
    public static final int STATE_PLAYABLE = 1;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_PLAYING = 3;

    private ColorStateList sColorStatePlaying;
    private ColorStateList sColorStateNotPlaying;
    private List<String> keys;
    public BrowseAdapter(Activity activity, Map<String, MediaMetadataCompat> musics,
                         boolean isSort) {
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
    private OnClickItemListener onClickItemListener;
    public void setOnClickItemListener(OnClickItemListener onClickItemListener) {
        this.onClickItemListener = onClickItemListener;
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
        Integer cachedState = STATE_INVALID;

//        final MediaBrowserCompat.MediaItem item = mMusics.get(position);
//        final SongModel songModel = MusicLibrary.getSongModel(mMusics.get(position).getMediaId());
        final MediaMetadataCompat description = MusicLibrary.getMetadata(mActivity,keys.get(position));
        holder.assignData(description);
        /*int state = getMediaItemState(mActivity, item);
        if (cachedState == null || cachedState != state) {
            Drawable drawable = getDrawableByState(mActivity, state);
            if (drawable != null) {
                holder.mImageView.setImageDrawable(drawable);
                holder.mImageView.setVisibility(View.VISIBLE);
            }
        }*/

        holder.mLinearMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItemListener.onClickMusic(keys.get(position));
                mSharedPrefsUtils.setString(Constants.PREFERENCES.SAVE_ALBUM_ID,
                        description.getString(Constants.METADATA.AlbumID));
            }
        });

        holder.mBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showSetMusic(mActivity,keys.get(position));
            }
        });

    }
    public int getMediaItemState(Activity context, MediaBrowserCompat.MediaItem mediaItem) {
        int state = STATE_NONE;
        // Set state to playable first, then override to playing or paused state if needed
        if (mediaItem.isPlayable()) {
            state = STATE_PLAYABLE;
           /* if (MediaIDHelper.isMediaItemPlaying(context, mediaItem)) {
                state = getStateFromController(context);
            }*/
        }

        return state;
    }

    public int getStateFromController(Activity context) {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(context);
        PlaybackStateCompat pbState = controller.getPlaybackState();
        if (pbState == null ||
                pbState.getState() == PlaybackStateCompat.STATE_ERROR) {
            return STATE_NONE;
        } else if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            return  STATE_PLAYING;
        } else {
            return STATE_PAUSED;
        }
    }

    public  Drawable getDrawableByState(Context context, int state) {
        if (sColorStateNotPlaying == null || sColorStatePlaying == null) {
//            initializeColorStateLists(context);
        }

        switch (state) {
            case STATE_PLAYABLE:
                Drawable pauseDrawable = ContextCompat.getDrawable(context,
                        R.drawable.ic_play_arrow_black_36dp);
                DrawableCompat.setTintList(pauseDrawable, sColorStateNotPlaying);
                return pauseDrawable;
            case STATE_PLAYING:
                AnimationDrawable animation = (AnimationDrawable)
                        ContextCompat.getDrawable(context, R.drawable.ic_equalizer_white_36dp);
                DrawableCompat.setTintList(animation, sColorStatePlaying);
                animation.start();
                return animation;
            case STATE_PAUSED:
                Drawable playDrawable = ContextCompat.getDrawable(context,
                        R.drawable.ic_equalizer1_white_36dp);
                DrawableCompat.setTintList(playDrawable, sColorStatePlaying);
                return playDrawable;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        if (!isSort){
            return limit;
        }else {
            return mMusics.size();
        }
    }

}