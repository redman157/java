package com.android.music_player.media;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.media.MediaBrowserServiceCompat;

import com.android.music_player.activities.PlayActivity;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.Utils;

import java.util.List;

/**
 * Customize the connection to our {@link MediaBrowserServiceCompat}
 * and implement our app specific desires.
 */
public class MediaBrowserConnection extends MediaBrowserHelper {
    private MediaSeekBar mSeekBarAudio;
    private String TAG = "JJJ";
    private String mediaId;
    private MediaControllerCompat mediaController;
    private Context context;
    private TextView mTextLeftTime,mTextRightTime;
    public MediaSeekBar getSeekBarAudio() {
        return mSeekBarAudio;
    }
    public void setSeekBarAudio(MediaSeekBar mSeekBarAudio, TextView mTextLeftTime, TextView mTextRightTime) {
        this.mSeekBarAudio = mSeekBarAudio;
        this.mTextLeftTime = mTextLeftTime;
        this.mTextRightTime = mTextRightTime;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public MediaBrowserConnection(Context context) {
        super(context, MediaService.class);
        this.context = context;
    }

    @Override
    protected void onConnected(@NonNull MediaControllerCompat mediaController) {
        if (context instanceof PlayActivity) {
            Log.d(TAG, mediaController.getPlaybackInfo().getPlaybackType()+"");
            ((PlayActivity)context).mSeekBarAudio.setMediaController(mediaController,
                    mTextLeftTime, mTextRightTime);
        }
    }

    @Override
    protected void onChildrenLoaded(@NonNull String parentId,
                                    @NonNull List<MediaBrowserCompat.MediaItem> children) {
        super.onChildrenLoaded(parentId, children);

        mediaController = getMediaController();

        // Queue up all media items for this simple sample.
        for (final MediaBrowserCompat.MediaItem mediaItem : children) {
            mediaController.addQueueItem(mediaItem.getDescription());
        }
        Utils.Builder builder = new Utils.Builder();
        builder.putBoolean(Constants.INTENT.AUTO_PLAY, true);

        // Call prepare now so pressing play just works.
        if (mediaId!= null) {
            MusicManager.getInstance().setContext(context);
            Log.d("VVV", "MediaBrowserConnection --- mediaId: "+mediaId);
//            Log.d("VVV", "MediaBrowserConnection --- CurrentSong: "+MusicManager.getInstance()
//                    .getCurrentMusic().getSongName());

            mediaController.getTransportControls().prepareFromMediaId(mediaId, null);
        }
    }
}