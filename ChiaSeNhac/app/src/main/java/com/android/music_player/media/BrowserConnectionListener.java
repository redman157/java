package com.android.music_player.media;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.media.MediaBrowserServiceCompat;

import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.services.MediaService;
import com.android.music_player.utils.BundleHelper;
import com.android.music_player.utils.Constants;

import java.util.List;

/**
 * Customize the connection to our {@link MediaBrowserServiceCompat}
 * and implement our app specific desires.
 */
public class BrowserConnectionListener extends BrowserHelper {
    private MediaSeekBar mSeekBarAudio;
    private String TAG = "JJJ";
    private String mediaId;
    public MediaControllerCompat mMediaController;
    private Context context;
    private TextView mTextLeftTime,mTextRightTime;
    private boolean isPlay;
    public MediaBrowserSubscriptionCallback mMediaBrowserSubscriptionCallback;
    private MediaManager mMediaManager = MediaManager.getInstance();

    public OnServiceConnect onServiceConnect;
    public interface OnServiceConnect {
        void onConnect(MediaBrowserCompat mediaBrowserCompat, MediaControllerCompat mediaController);
    }

    public void setOnServiceConnectListener(OnServiceConnect onServiceConnect) {
        this.onServiceConnect = onServiceConnect;

    }

    public BrowserConnectionListener(Context context) {
        super(context, MediaService.class);
        this.context = context;
        mMediaBrowserSubscriptionCallback = new MediaBrowserSubscriptionCallback();
        mMediaManager.setContext(context);
    }

    @Override
    protected void onConnected(@NonNull MediaControllerCompat mediaController,
                               MediaBrowserCompat mediaBrowserCompat) {
        if (onServiceConnect != null) {
            onServiceConnect.onConnect(mediaBrowserCompat ,mediaController);
        }
        Log.d(TAG, "onConnected: "+mediaController.getPlaybackInfo().getPlaybackType());
        // khi connect mình sẽ set bài hát ở đây
        Log.d("MMM","onConnected: "+mediaBrowserCompat.getRoot() );

//        mediaBrowserCompat.subscribe(MusicLibrary.MEDIA_ID_EMPTY_ROOT,
//                mMediaBrowserSubscriptionCallback);
    }

    public MediaBrowserSubscriptionCallback getCallback() {
        return mMediaBrowserSubscriptionCallback;
    }


    @Override
    public void setSubscribe(String parentID, MediaBrowserSubscriptionCallback mediaBrowserSubscriptionCallback) {
        super.setSubscribe(parentID, mediaBrowserSubscriptionCallback);
    }

    @Override
    protected void onChildrenLoaded(@NonNull String parentId,
                                    @NonNull List<MediaBrowserCompat.MediaItem> children) {
        super.onChildrenLoaded(parentId, children);

        mMediaController = getMediaController();
        if (children.size()> 0) {
            Log.d("ZZZ", "enter if");
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                mMediaController.addQueueItem(mediaItem.getDescription());

            }
            for ( MediaBrowserCompat.MediaItem mediaItem : children) {
                Log.d("ZZZ", mediaItem.getMediaId() + "");
                mMediaController.removeQueueItem(mediaItem.getDescription());
            }
        }else if(children.size() == 0) {
            mMediaManager.getStateViewModel().getNamePlayList().observe((LifecycleOwner) context, new Observer<String>() {
                @Override
                public void onChanged(String titlePlayList) {
                    Log.d("ZZZ",
                            "enter else if: " + titlePlayList);
                    List<MediaBrowserCompat.MediaItem> mediaItems = null;
                    if (mMediaManager.getAllMusicOfPlayList(titlePlayList) != null) {
                        mediaItems = MusicLibrary.getAlbumItems(mMediaManager.getAllMusicOfPlayList(titlePlayList));
                        Log.d("ZZZ", "enter else if: " + mediaItems.size());
                    }
                    if (mediaItems != null) {
                        for ( MediaBrowserCompat.MediaItem mediaItem : mediaItems) {
                            Log.d("ZZZ", mediaItem.getMediaId() + "");
                            mMediaController.addQueueItem(mediaItem.getDescription());
                        }
                        for ( MediaBrowserCompat.MediaItem mediaItem : mediaItems) {
                            Log.d("ZZZ", mediaItem.getMediaId() + "");
                            mMediaController.removeQueueItem(mediaItem.getDescription());
                        }
                    }
                }
            });
        }
       /* try {
            for (int i = 0; i < mMediaController.getQueue().size(); i++) {
                Log.d("ZZZ", "enter remove");
                mMediaController.removeQueueItem(mMediaController.getQueue().get(i).getDescription());
            }
        }catch (Exception e){
            Log.d("ZZZ", this.getClass().getSimpleName() + " --- onChildrenLoaded: "+e.getMessage());
        }finally {
            // Queue up all media items for this simple sample.

            }
        }*/


    }

    public void setAutoPlay(String mediaID, boolean autoPlay){
        if (mMediaController != null) {
            BundleHelper.Builder builder = new BundleHelper.Builder();
            builder.putBoolean(Constants.INTENT.AUTO_PLAY, autoPlay);
            if (mediaID.equals(mMediaManager.getCurrentMusic())) {
                mMediaController.getTransportControls().stop();
            }
            mMediaController.getTransportControls().prepareFromMediaId(mediaID,
                    builder.generate().getBundle());
        }
    }
}