package com.android.music_player.managers;

import android.app.Activity;
import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.android.music_player.interfaces.ControllerStyle;
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
    private String currentParent;
    private SharedPrefsUtils mSharedPrefsUtils;
    private MediaMetadataCompat mediaMetadataCompat;
    private List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

    public static QueueManager getInstance(Context context){
        if (instance == null){
            instance = new QueueManager(context);
        }
        return instance;
    }
    private Observer<ControllerStyle> observer;

    public void setNullViewModel(){
        if (mStateViewModel != null){
            mStateViewModel = null;
        }
    }
    private QueueManager(Context context){
        // TODO something code
        mContext = context;
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(mContext);
        mSharedPrefsUtils = new SharedPrefsUtils(mContext);
        // setup data ban đầu của khi mở app
        if (mStateViewModel == null){
            try {
                mStateViewModel = new StateViewModel(((Activity) mContext).getApplication());
                mStateViewModel.setParentId(MusicLibrary.MEDIA_ID_ROOT);
                mStateViewModel.setNamePlayList("");
                mStateViewModel.setControllerStyle(ControllerStyle.ALL_MUSIC);
                if (mSharedPrefsUtils.getString(Constants.PREFERENCES.CURRENT_MUSIC, "").equals("") && MusicLibrary.music.size() > 0) {
                    mStateViewModel.setMediaDataCurrent(MusicLibrary.music.get(MusicLibrary.music.keySet().toArray()[0]));
                }
            }catch (ClassCastException e){
                Log.d("ZZZ",e.getMessage());
            }
        }
    }

    public StateViewModel getStateViewModel() {
        return mStateViewModel;
    }

    public void setStateViewModel(StateViewModel mStateViewModel) {
        this.mStateViewModel = mStateViewModel;
    }

    public List<MediaBrowserCompat.MediaItem> getControllerStyle(){
        Log.d("TTT", "getControllerStyle: "+(mStateViewModel.getControllerStyle() == null ? "null":"khac null"));
        mStateViewModel.getControllerStyle().observe((AppCompatActivity) mContext, new Observer<ControllerStyle>() {
            @Override
            public void onChanged(ControllerStyle controllerStyle) {
                Log.d("TTT", "onChanged: "+controllerStyle.toString());
                if (controllerStyle == ControllerStyle.ALL_MUSIC) {
                    ArrayList<String> playLists = new ArrayList<>(MusicLibrary.music.keySet());
                    mediaItems = MusicLibrary.getAlbumService(playLists);
                } else if (controllerStyle == ControllerStyle.ARTIST) {

                } else if (controllerStyle == ControllerStyle.ALBUM) {

                } else if (controllerStyle == ControllerStyle.FOLDER) {

                } else if (controllerStyle == ControllerStyle.PLAY_LIST) {
                    mediaItems = getNamePlayList();
                }
            }
        });
      /*  try {
            Log.d("TTT", "getControllerStyle: enter");
            mStateViewModel.getControllerStyle().observe((LifecycleOwner) mContext, new Observer<ControllerStyle>() {
                @Override
                public void onChanged(ControllerStyle controllerStyle) {
                    Log.d("TTT", "onChanged: "+controllerStyle.toString());
                    if (controllerStyle == ControllerStyle.ALL_MUSIC) {
                        ArrayList<String> playLists = new ArrayList<>(MusicLibrary.music.keySet());
                        mediaItems = MusicLibrary.getAlbumService(playLists);
                    } else if (controllerStyle == ControllerStyle.ARTIST) {

                    } else if (controllerStyle == ControllerStyle.ALBUM) {

                    } else if (controllerStyle == ControllerStyle.FOLDER) {

                    } else if (controllerStyle == ControllerStyle.PLAY_LIST) {
                        mediaItems = getNamePlayList();
                    }
                }
            });

        } catch (NullPointerException e){

            ArrayList<String> playLists = new ArrayList<>(MusicLibrary.music.keySet());
            mediaItems = MusicLibrary.getAlbumService(playLists);
            Log.d("TTT", "NullPointerException: "+mediaItems.size());
        }catch (Exception e){
            Log.d("TTT", "Exception: "+e.getMessage());
        }*/
        return mediaItems;
    }


    public String getParentId(){
        mStateViewModel.getParentId().observe((AppCompatActivity) mContext, new Observer<String>() {
                @Override
                public void onChanged(String parentId) {
                    currentParent = parentId;

            }
        });
        return currentParent;
    }

    public List<MediaBrowserCompat.MediaItem> getNamePlayList(){
        mStateViewModel.getNamePlayList().observe((AppCompatActivity) mContext, new Observer<String>() {
            @Override
            public void onChanged(String namePlayList) {
                try {
                    if (mMediaManager.getAllMusicOfPlayList(namePlayList) != null) {
                        ArrayList<String> playLists = mMediaManager.getAllMusicOfPlayList(namePlayList);
                        mediaItems = MusicLibrary.getAlbumService(playLists);
                    }
                } catch(NullPointerException e){
                    Utils.ToastShort(mContext, "Play List chưa có bài hát");
                }
            /*if (namePlayList.equals("")) {
                ArrayList<String> playLists = new ArrayList<>(MusicLibrary.music.keySet());
                mediaItems = MusicLibrary.getAlbumService(playLists);
            } else {


            }*/
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
                Log.d("III",mMediaManager.getStatistic().getMusicMost(Constants.VALUE.MOST_MUSIC));

            }
        });

        return mediaMetadataCompat;
    }

    public void setupPlayList(String namePlayList){
        getStateViewModel().setNamePlayList(namePlayList);
        getStateViewModel().setParentId(MusicLibrary.MEDIA_ID_EMPTY_ROOT);
    }

    public void setupAllMusic( ){
        getStateViewModel().setParentId(MusicLibrary.MEDIA_ID_ROOT);
        getStateViewModel().setControllerStyle(ControllerStyle.ALL_MUSIC);
            Log.d("TTT", "HomeActivity --- setupallmusic: if enter");
//        getStateViewModel().setNamePlayList("");
    }

    public void setCurrentMediaMetadata(MediaMetadataCompat metadataCompat){
        if (metadataCompat == null){
            return;
        }
        mSharedPrefsUtils.setString(Constants.PREFERENCES.CURRENT_MUSIC, metadataCompat.getString(Constants.METADATA.Title));
        getStateViewModel().setMediaDataCurrent(metadataCompat);
    }
}
