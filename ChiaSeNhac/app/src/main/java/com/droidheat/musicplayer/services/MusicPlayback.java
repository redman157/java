package com.droidheat.musicplayer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.droidheat.musicplayer.BuildNotification;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.activities.HomeActivity;
import com.droidheat.musicplayer.utils.CommonUtils;
import com.droidheat.musicplayer.utils.SharedPrefsUtils;
import com.droidheat.musicplayer.utils.SongsUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class MusicPlayback extends MediaBrowserServiceCompat implements
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener, MediaPlayer.OnPreparedListener{

    // Available PlayBackStates
    //  STATE_NONE = 0;
    //  STATE_STOPPED = 1;
    //  STATE_PAUSED = 2;
    //  STATE_PLAYING = 3;


    /******* ---------------------------------------------------------------
     Private
     ----------------------------------------------------------------*******/

    private final String TAG = "PlaybackServiceConsole";

    private MediaPlayer mMediaPlayer;
    private MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat.Builder mPlaybackStateBuilder;
    private SharedPrefsUtils prefsUtils;
    private SongsUtils songsUtils;

    private  Equalizer eq;
    private BassBoost bassBoost;
    private Virtualizer virtualizer;

    private int currentMediaPlayer = 0; // 0 - mMediaPlayer; 1 - mMediaPlayer2
    private int crossfadeDuration = 3000; // 3 seconds
    private Handler mHandler;
    private boolean autoPaused = false;
    private final int NOTIFICATION_ID = 34213134;

    /******* ---------------------------------------------------------------
     Service Methods and Intents
     ----------------------------------------------------------------*******/
    public MusicPlayback(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefsUtils = new SharedPrefsUtils(this);
        songsUtils = SongsUtils.getInstance();
        songsUtils.setContext(this);
//        mHandler = new Handler();

         /*
         Initialize
         */
        checkErrorInPrefs();
        initMediaPlayer();
        initMediaSession();
        initNoisyReceiver();

        /*
         * Calling startForeground() under 5 seconds to avoid ANR
         */
        initNotification();

    }

    private void initMediaPlayer(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(1.0f, 1.0f);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
    }

    private void resetMediaPlayer(){
        if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PLAYING ||
                mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PAUSED){
            mMediaPlayer.reset();
        }
    }

    private void setMediaPlayer(String path){
        resetMediaPlayer();
        File file = new File(path);
        if (file.exists()){
            try {
                addVoteToTrack(String path);
                mMediaPlayer.setDataSource(path);
            }catch (Exception e){
                processNextRequest();
                e.printStackTrace();
            }
            try {
                mMediaPlayer.prepare();
            } catch (IOException ignored) {
            }
        } else {
            processNextRequest();
            Log.d(TAG, "Error finding file so we skipped to next.");
            (new CommonUtils(this)).showTheToast("Error finding music file");
        }
    }

    private void initMediaSession(){
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(),
                MediaButtonReceiver.class);
        mMediaSessionCompat =new MediaSessionCompat(getApplicationContext(),"TAG",
                mediaButtonReceiver, null);
        mMediaSessionCompat.setCallback(mMediaSessionCallback);
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0 ,mediaButtonIntent,0);
        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);

        mPlaybackStateBuilder =new PlaybackStateCompat.Builder();

        setSessionToken(mMediaSessionCompat.getSessionToken());

        setMetaData();
    }

    /******* ---------------------------------------------------------------
     MediaSessionCompat Methods
     ----------------------------------------------------------------*******/
    private MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlayFromUri(Uri uri, Bundle extras) {
            super.onPlayFromUri(uri, extras);
        }

        @Override
        public void onPlay() {
            super.onPlay();
            processPlayPause();
        }

        @Override
        public void onPause() {
            super.onPause();
            processPauseRequest();
        }

        @Override
        public void onSkipToNext() {
            super.onSkipToNext();
            processNextRequest();
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
            processPrevRequest();
        }

        @Override
        public void onRewind() {
            super.onRewind();
        }

        @Override
        public void onStop() {
            super.onStop();
            processCloseRequest();
        }

        @Override
        public void onSetRepeatMode(int repeatMode) {
            super.onSetRepeatMode(repeatMode);
        }

        @Override
        public void onSkipToQueueItem(long id) {
            super.onSkipToQueueItem(id);
            doPushPlay((int) id);
        }

        @Override
        public void onSeekTo(long pos) {
            super.onSeekTo(pos);
            mMediaPlayer.seekTo((int) (long) pos);

            Log.d(TAG, "onSeekTo: " + pos);
            setMediaPlaybackState(mPlaybackStateBuilder.build().getState());
        }
    };
    private void setMediaPlaybackState(int state){
        mPlaybackStateBuilder.setActions(PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_PAUSE
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                | PlaybackStateCompat.ACTION_STOP
                | PlaybackStateCompat.ACTION_SEEK_TO);

        mPlaybackStateBuilder.setState(state, mMediaPlayer.getCurrentPosition(), 1.0f,
                SystemClock.elapsedRealtime());

        if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PLAYING) {
            Log.d("PlaybackServiceConsole", "State Changed to Playing");
        } else if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PAUSED) {
            Log.d("PlaybackServiceConsole", "State Changed to Paused");
        } else if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_STOPPED) {
            Log.d("PlaybackServiceConsole", "State Changed to Stopped");
        }
        mMediaSessionCompat.setPlaybackState(mPlaybackStateBuilder.build());
    }

    private BroadcastReceiver mNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                processPauseRequest();
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            /*
             * Analyze and Acting on the intent received by Service
             * Every request to Service should be with one of the intents in our switch
             */
            MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);

            Log.d(TAG, "Intent received.");
            switch (Objects.requireNonNull(intent.getAction())){
                case Constants.ACTION.ACTION_PLAY:
                    resetMediaPlayerPosition();
                    processPlayRequest();
                    break;

                case Constants.ACTION.ACTION_PLAY_PAUSE:
                    resetMediaPlayerPosition();
                    processPlayPause();
                    break;
                case Constants.ACTION.ACTION_TRACK_PREV:
                    processPrevRequest();
                    break;
                case Constants.ACTION.ACTION_TRACK_NEXT:
                    processNextRequest();
                    break;
                case Constants.ACTION.ACTION_REPEAT:
                    musicWidgetsReset();
                    break;
                case Constants.ACTION.ACTION_CLOSE:
                    if (!prefsUtils.readSharedPrefsBoolean(
                            Constants.PREFERENCES.persistentNotificationPref, false)) {
                        processCloseRequest();
                    } else {
                        processPauseRequest();
                    }
                    break;
                case Constants.ACTION.ACTION_PERSISTENT_NOTIFICATION:
                    if (mPlaybackStateBuilder.build().getState() != PlaybackStateCompat.STATE_PLAYING) {
                        showPausedNotification();
                    }
                    break;
                default: {
                    initNotification();
                }
            }
        }
        return START_STICKY;
    }
    /*
     * Getting and Switching MediaPlayers for Cross-fade
     */
  /*  private MediaPlayer getCurrentMediaPlayer() {
        return (currentMediaPlayer == 0) ? mMediaPlayer : mMediaPlayer2;
    }*/

    private void checkErrorInPrefs() {
        if (songsUtils.getCurrentMusicID() > songsUtils.queue().size() - 1) {
            songsUtils.setCurrentMusicID(0);
        }
    }

    private void switchMediaPlayer() {
        if (currentMediaPlayer == 0) {
            currentMediaPlayer++; // output 1
        } else if (currentMediaPlayer == 1) {
            currentMediaPlayer--; // output 0
        } else {
            currentMediaPlayer = 0;
        }
    }

 /*   private void crossfade() {
        mHandler.post(detectCrossFadeRunnable);
    }

    private void cancelCrossfade() {
        mHandler.removeCallbacks(detectCrossFadeRunnable);
    }*/

    // Detects and starts cross-fading
    /*public Runnable detectCrossFadeRunnable = new Runnable() {
        @Override
        public void run() {
            //Check if we're in the last part of the current song.
            try {
                if (getCurrentMediaPlayer().isPlaying()) {
                    if (getCurrentMediaPlayer().getCurrentPosition() >=
                            (getCurrentMediaPlayer().getDuration() - crossfadeDuration)) {
                        // Start cross-fading the songs
                        switchMediaPlayer();
                        mHandler.postDelayed(crossFadeRunnable, 100);
                    } else {
                        mHandler.postDelayed(detectCrossFadeRunnable, 1000);
                    }
                } else {
                    mHandler.postDelayed(detectCrossFadeRunnable, 1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };*/


    // Volume
    private float mFadeOutVolume = 1.0f;
    private float mFadeInVolume = 0.0f;

    /*public Runnable crossFadeRunnable = new Runnable() {
        @Override
        public void run() {
            try {

                int crossFadeStep = 10000; // 10 seconds before finishing and into next song

                if (currentMediaPlayer == 0) {
                    // mMediaPlayer

                    mMediaPlayer.setVolume(mFadeInVolume, mFadeInVolume);
                    mMediaPlayer2.setVolume(mFadeOutVolume, mFadeOutVolume);

                    // Start next track and run Crossfade
                    mMediaPlayer.seekTo(crossFadeStep);
                    mMediaPlayer2.start();
                } else {
                    // mMediaPlayer2

                    mMediaPlayer2.setVolume(mFadeInVolume, mFadeInVolume);
                    mMediaPlayer.setVolume(mFadeOutVolume, mFadeOutVolume);

                    // Start next track and run Crossfade
                    mMediaPlayer2.seekTo(crossFadeStep);
                    mMediaPlayer2.start();
                }
                mFadeInVolume = mFadeInVolume + (1.0f / (((float) crossfadeDuration / 1000) * 10.0f));
                mFadeOutVolume = mFadeOutVolume - (1.0f / (((float) crossfadeDuration / 1000) * 10.0f));

                mHandler.postDelayed(crossFadeRunnable, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };*/

    /*
     * saveData() writes current song parameters to sharedPrefs which can be retrieved in
     * other activities or fragments as well as when we start app next time
     * musicID: is id of current item in queue
     * title, artist, album, albumid: are all fields of SongModel()
     *
     */

    private void saveData(){
        int musicID = songsUtils.getCurrentMusicID();

        try {
            prefsUtils.writeSharedPrefs(Constants.PREFERENCES.AUDIO_SESSION_ID, mMediaPlayer.getAudioSessionId());
            prefsUtils.writeSharedPrefs(Constants.PREFERENCES.MUSIC_ID , musicID);
            prefsUtils.writeSharedPrefs(Constants.PREFERENCES.TITLE , songsUtils.queue().get(musicID).getTitle());
            prefsUtils.writeSharedPrefs(Constants.PREFERENCES.ARTIST , songsUtils.queue().get(musicID).getArtist());
            prefsUtils.writeSharedPrefs(Constants.PREFERENCES.ALBUM , songsUtils.queue().get(musicID).getAlbum());
            prefsUtils.writeSharedPrefs(Constants.PREFERENCES.ALBUMID , songsUtils.queue().get(musicID).getAlbumID());
            prefsUtils.writeSharedPrefs(Constants.PREFERENCES.RAW_PATH , songsUtils.queue().get(musicID).getPath());
            prefsUtils.writeSharedPrefs(Constants.PREFERENCES.DURATION , songsUtils.queue().get(musicID).getDuration());
            prefsUtils.writeSharedPrefs(Constants.PREFERENCES.DURATION_IN_MS , mMediaPlayer.getDuration());
        }catch (Exception e ){
            Log.d(TAG, "Unable to save song info in persistent storage. MusicID " + musicID);
        }
    }

    private void doPushPlay(int id) {
        songsUtils.setCurrentMusicID(id);
        mMediaPlayer.reset();
        if (successfullyRetrievedAudioFocus()) {
            showPausedNotification();
            return;
        }
        setMediaPlayer(songsUtils.queue().get(id).getPath());
    }

    private void processNextRequest(){
        resetMediaPlayerPosition();
        int musicID = songsUtils.getCurrentMusicID();

        if (musicID + 1 != songsUtils.queue().size()){
            musicID++;
        }else {
            musicID = 0;
        }
        songsUtils.setCurrentMusicID(musicID);
        if (successfullyRetrievedAudioFocus()) {
            showPausedNotification();
            return;
        }

        Log.d(TAG, "Skipping to Next track.");
        setMediaPlayer(songsUtils.queue().get(musicID).getPath());
    }

    private void processPrevRequest(){
        resetMediaPlayerPosition();
        if (mMediaPlayer.getCurrentPosition() < 5000){
            int musicID =  songsUtils.getCurrentMusicID();
            if (musicID > 0) {
                musicID--;
                songsUtils.setCurrentMusicID(musicID);
                Log.d(TAG, "Skipping to Previous track.");
                if (successfullyRetrievedAudioFocus()) {
                    showPausedNotification();
                    return;
                }
                setMediaPlayer(songsUtils.queue().get(musicID).getPath());
            }else {
                mMediaSessionCompat.getController().getTransportControls().seekTo(0);
            }
        } else {
            mMediaSessionCompat.getController().getTransportControls().seekTo(0);
        }
    }

    private void processPlayRequest(){
        if (successfullyRetrievedAudioFocus()) {
            showPausedNotification();
            return;
        }
        Log.d(TAG, "Processing Play Request for musicID: " + songsUtils.getCurrentMusicID());

        setMediaPlayer(songsUtils.queue().get(songsUtils.getCurrentMusicID()).getPath());
    }

    private void processPauseRequest(){
        if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PLAYING){
            mMediaSessionCompat.setActive(false);
            mMediaPlayer.pause();
            prefsUtils.writeSharedPrefs(Constants.PREFERENCES.song_position, mMediaPlayer.getCurrentPosition());
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            showPausedNotification();
        }
    }


    private void processCloseRequest(){
        stopSelf();
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
    }

    private void processPlayPause(){
        Log.d(TAG, "Play/Pausing");
        if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PLAYING){
            processPlayPause();
        }else if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PAUSED){
            if (successfullyRetrievedAudioFocus()) {
                return;
            }
            mMediaSessionCompat.setActive(true);
            mMediaPlayer.start();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            showPlayingNotification();
        } else {
            processPlayRequest();
        }
    }


    private void setMetaData(){
        if (songsUtils.queue().size() == 0){
            MediaMetadataCompat mMediaMetadataCompat = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE,
                            prefsUtils.readSharedPrefsString(Constants.PREFERENCES.TITLE,
                                    "Placeholder Title"))
                    .putString(MediaMetadata.METADATA_KEY_ALBUM,
                            prefsUtils.readSharedPrefsString(Constants.PREFERENCES.ALBUM,
                                    "Placeholder Album"))
                    .putString(MediaMetadata.METADATA_KEY_ARTIST,
                            prefsUtils.readSharedPrefsString(Constants.PREFERENCES.ARTIST,
                                    "Placeholder Artist"))
                    .putLong(MediaMetadata.METADATA_KEY_DURATION,
                            (long) prefsUtils.readSharedPrefsInt(Constants.PREFERENCES.DURATION_IN_MS,
                                    0))
                    .putBitmap(MediaMetadata.METADATA_KEY_ART,
                            grabAlbumArt(prefsUtils.readSharedPrefsString(Constants.PREFERENCES.ALBUMID,
                                    "0")))
                    .build();
            mMediaSessionCompat.setMetadata(mMediaMetadataCompat);
        }else {
            int musicID = songsUtils.getCurrentMusicID();
            MediaMetadataCompat mMediaMetadataCompat = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE,
                            songsUtils.queue().get(musicID).getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,
                            songsUtils.queue().get(musicID).getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                            songsUtils.queue().get(musicID).getArtist())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                            (long) prefsUtils.readSharedPrefsInt(Constants.PREFERENCES.DURATION_IN_MS, 0))
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                            grabAlbumArt(songsUtils.queue().get(musicID).getAlbumID()))
                    .build();
            mMediaSessionCompat.setMetadata(mMediaMetadataCompat);
        }
    }

    private void setGraphics() {
        saveData();
        setMetaData();
    }

    private Bitmap grabAlbumArt(String albumID){
        Bitmap art = null;
        try {
            try {
                final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                Uri uri = ContentUris.withAppendedId(sArtworkUri, Long.parseLong(albumID));
                ParcelFileDescriptor pfd = getApplicationContext()
                        .getContentResolver()
                        .openFileDescriptor(uri, "r");
                if (pfd != null) {
                    FileDescriptor fd = pfd.getFileDescriptor();
                    art = BitmapFactory.decodeFileDescriptor(fd);
                }


            }catch (Exception ignored){

            }
            if (art == null) {
                return drawableToBitmap(ContextCompat.getDrawable(this, R.mipmap.ic_launcher));
            } else {
                return art;
            }
        }catch (Exception e){
            return drawableToBitmap(ContextCompat.getDrawable(MusicPlayback.this,
                    R.mipmap.ic_launcher));
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable){
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 300;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 300;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /******* ---------------------------------------------------------------
     Notifications
     ----------------------------------------------------------------*******/

    private void createChannel(){
        if(Build.VERSION.SDK_INT > 26) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // The id of the channel.
            String id = "channel_music_playback";
            // The user-visible name of the channel.
            CharSequence name = "Media Playback";
            // The user-visible description of the channel.
            String description = "Media playback controls";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.setShowBadge(false);

            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    private void showPlayingNotification(){
//        musicWidgetsReset();

        createChannel();
        NotificationCompat.Builder builder = BuildNotification.from(MusicPlayback.this,
                mMediaSessionCompat );


        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.ACTION_CLOSE), 0);

        PendingIntent pPlayIntent = PendingIntent.getService(this,0,
                (new Intent(this, MusicPlayback.class).setAction(Constants.ACTION.ACTION_PLAY)),0);
        PendingIntent pNextIntent = PendingIntent.getService(this,0,
                (new Intent(this, MusicPlayback.class).setAction(Constants.ACTION.ACTION_TRACK_NEXT)),0);
        PendingIntent pPrevIntent = PendingIntent.getService(this,0,
                (new Intent(this, MusicPlayback.class).setAction(Constants.ACTION.ACTION_TRACK_PREV)),0);

        builder.addAction(new NotificationCompat.Action(R.drawable.app_previous, "Previous", pPrevIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_pause, "Play", pPlayIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_next, "Next", pNextIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_close_black_24dp,"Close",
                pCloseIntent));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0)
                .setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.drawable.ic_music_note_black_24dp);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
        // when click notification auto intent HomeActivity
        builder.setContentIntent(PendingIntent.getActivity(this,
                0, new Intent(this, HomeActivity.class), 0));
        builder.setDeleteIntent(pCloseIntent);
        builder.setShowWhen(false);
        startForeground(1, builder.build());
    }

    private void showPausedNotification() {
//        musicWidgetsReset();

        createChannel();
        NotificationCompat.Builder builder
                = BuildNotification.from(MusicPlayback.this, mMediaSessionCompat);

        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.ACTION_CLOSE), 0);

        PendingIntent prevIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.ACTION_TRACK_PREV), 0);
        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.ACTION_PLAY_PAUSE), 0);
        PendingIntent nextIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.ACTION_TRACK_NEXT), 0);

        builder.addAction(new NotificationCompat.Action(R.drawable.app_previous, "Previous", prevIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_play, "Play", playPauseIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_next, "Next", nextIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.ic_close_black_24dp,"Close",
                        pCloseIntent));

        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.drawable.ic_music_note_black_24dp);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0));
        builder.setDeleteIntent(pCloseIntent);
        builder.setShowWhen(false);
        startForeground(1, builder.build());
        stopForeground(false);
    }

    private void initNotification() {
        createChannel();
        NotificationCompat.Builder builder
                = BuildNotification.from(MusicPlayback.this, mMediaSessionCompat);

        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.ACTION_CLOSE), 0);

        PendingIntent prevIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.ACTION_TRACK_PREV), 0);
        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.ACTION_PLAY_PAUSE), 0);
        PendingIntent nextIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.ACTION_TRACK_NEXT), 0);

        builder.addAction(new NotificationCompat.Action(R.drawable.app_previous, "Previous", prevIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_play, "Play", playPauseIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_next, "Next", nextIntent));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.drawable.ic_music_note_black_24dp);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0));
        builder.setDeleteIntent(pCloseIntent);
        builder.setShowWhen(false);
        startForeground(1, builder.build());

    }
    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    /******* ---------------------------------------------------------------
     AudioFocus
     ----------------------------------------------------------------*******/
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mMediaPlayer!= null){
                    if (mMediaPlayer.isPlaying()){
                        processPauseRequest();
                        autoPaused = true;
                        Log.d(TAG, "Auto paused enabled");
                    }
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mMediaPlayer != null) {
                    mMediaPlayer.setVolume(0.3f, 0.3f);
                }
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mMediaPlayer != null) {
                    Log.d(TAG, "Auto-paused is " + ((autoPaused) ? "enabled" : "disabled"));
                    if (!mMediaPlayer.isPlaying() && autoPaused) {
                        processPlayPause();
                        autoPaused = false;
                        Log.d(TAG, "Auto paused disabled");
                    }
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
        }
    }

    private boolean successfullyRetrievedAudioFocus(){

    }

    /******* ---------------------------------------------------------------
     MediaPlayer
     ----------------------------------------------------------------*******/
    @Override
    public void onCompletion(MediaPlayer mp) {
        resetMediaPlayer();
        resetMediaPlayerPosition();

        if (!prefsUtils.readSharedPrefsBoolean(Constants.PREFERENCES.repeat, false)){
            Log.d(TAG, "OnCompletion playing next track");
            processNextRequest();
        }else {
            setMediaPlayer(songsUtils.queue().get(songsUtils.getCurrentMusicID()).getPath());
        }

    }
    public void resetMediaPlayerPosition() {
        prefsUtils.writeSharedPrefs(Constants.PREFERENCES.song_position,0);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        /*
         * Getting to saved location of song if playback state is none i.e. first instance of music playback
         * , and if we are playing same track we were playing before, if track is new then we won't seek to last
         * remembered location
         * We reset this location to zero when we start playing a new song
         */
        if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_NONE &&
            prefsUtils.readSharedPrefsString("raw_path","").equals(
                    songsUtils.queue()
                            .get(songsUtils.getCurrentMusicID())
                            .getPath())){
            mMediaSessionCompat.getController()
                    .getTransportControls()
                    .seekTo(prefsUtils.readSharedPrefsInt(Constants.PREFERENCES.song_position, 0));

            /*
             * Setting Equalizer
             */
            setEqualizer();

            /*
             * Setting metaData
             */
            setGraphics();
            Log.d(TAG, "starting playback");

            mMediaSessionCompat.setActive(true);
            mMediaPlayer.start();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            showPlayingNotification();
        }
    }

    private void initNoisyReceiver(){
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        IntentFilter filter =new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mNoisyReceiver, filter);
    }


}
