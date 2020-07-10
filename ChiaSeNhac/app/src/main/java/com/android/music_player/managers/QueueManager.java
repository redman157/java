package com.android.music_player.managers;

import android.app.Activity;
import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.android.music_player.models.StateViewModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class QueueManager {
    private static QueueManager instance;
    private StateViewModel mStateViewModel;
    private MediaManager mMediaManager;
    private Context mContext;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaMetadataCompat mediaMetadataCompat;
    private List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

    public static QueueManager getInstance(Context context){
        if (instance == null){
            instance = new QueueManager(context);
        }
        return instance;
    }
    private QueueManager(Context context){
        // TODO something code
        mContext = context;
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(mContext);
        mSharedPrefsUtils = new SharedPrefsUtils(mContext);
        // setup data ban đầu của khi mở app
        if (mStateViewModel == null){

            mStateViewModel = new StateViewModel(((Activity) mContext).getApplication());
            mStateViewModel.setParentId(MusicLibrary.MEDIA_ID_ROOT);
            mStateViewModel.setNamePlayList("");
            if (mSharedPrefsUtils.getString(Constants.PREFERENCES.CURRENT_MUSIC,"").equals("")){
                mStateViewModel.setMediaDataCurrent(MusicLibrary.music.get(MusicLibrary.music.keySet().toArray()[0]));
            }
        }
    }

    public StateViewModel getStateViewModel() {
        return mStateViewModel;
    }

    public void setStateViewModel(StateViewModel mStateViewModel) {
        this.mStateViewModel = mStateViewModel;
    }

    public void getParentId(){
        mStateViewModel.getParentId().observe((LifecycleOwner) mContext, new Observer<String>() {
            @Override
            public void onChanged(String parentId) {

                if (parentId.equals(MusicLibrary.MEDIA_ID_ROOT)){
                    // GỠ STATE VÀ SET STATE KHÁC
                    mMediaManager.getMediaBrowserConnection().unSetSubscribe(MusicLibrary.MEDIA_ID_EMPTY_ROOT,
                            mMediaManager.getMediaBrowserConnection().getCallback());

                    mMediaManager.getMediaBrowserConnection().setSubscribe(MusicLibrary.MEDIA_ID_ROOT,
                            mMediaManager.getMediaBrowserConnection().getCallback());
                    Log.d("ZZZ",
                            "kích thước: "+parentId);
                }else if (parentId.equals(MusicLibrary.MEDIA_ID_EMPTY_ROOT)){
                    mMediaManager.getMediaBrowserConnection().unSetSubscribe(MusicLibrary.MEDIA_ID_ROOT,
                            mMediaManager.getMediaBrowserConnection().getCallback());

                    mMediaManager.getMediaBrowserConnection().setSubscribe(MusicLibrary.MEDIA_ID_EMPTY_ROOT,
                            mMediaManager.getMediaBrowserConnection().getCallback());
                }
            }
        });
    }

    public List<MediaBrowserCompat.MediaItem> getNamePlayList(){
        mStateViewModel.getNamePlayList().observe((LifecycleOwner) mContext, new Observer<String>() {
            @Override
            public void onChanged(String namePlayList) {
                Log.d("ZZZ", "onChanged: " + namePlayList);
                if (namePlayList.equals("")) {
                    ArrayList<String> playLists = new ArrayList<>(MusicLibrary.music.keySet());
                    mediaItems = MusicLibrary.getAlbumService(playLists);
                    Log.d("ZZZ", "enter if");
                } else {
                    try {
                        if (mMediaManager.getAllMusicOfPlayList(namePlayList) != null) {
                            ArrayList<String> playLists = mMediaManager.getAllMusicOfPlayList(namePlayList);
                            mediaItems = MusicLibrary.getAlbumService(playLists);
                        }
                    } catch(NullPointerException e){
                        Utils.ToastShort(mContext, "Play List chưa có bài hát");
                    }
                }
            }
        });
        return mediaItems;
    }

    public MediaMetadataCompat getCurrentMediaMetadata(){

        getStateViewModel().getMediaDataCurrent().observe((LifecycleOwner) mContext, new Observer<MediaMetadataCompat>() {
            @Override
            public void onChanged(MediaMetadataCompat metadataCompat) {
                mSharedPrefsUtils.setString(Constants.PREFERENCES.CURRENT_MUSIC, metadataCompat.getString(Constants.METADATA.Title));
                mediaMetadataCompat = metadataCompat;
            }
        });

        return mediaMetadataCompat;
    }

    public void changePlayList(String namePlayList){
        getStateViewModel().setNamePlayList(namePlayList);
        getStateViewModel().setParentId(MusicLibrary.MEDIA_ID_EMPTY_ROOT);
    }

    public void changeAllMusic( ){
        getStateViewModel().setParentId(MusicLibrary.MEDIA_ID_ROOT);
        getStateViewModel().setNamePlayList("");
    }

    public void setCurrentMediaMetadata(MediaMetadataCompat metadataCompat){
        getStateViewModel().setMediaDataCurrent(metadataCompat);
    }
}
