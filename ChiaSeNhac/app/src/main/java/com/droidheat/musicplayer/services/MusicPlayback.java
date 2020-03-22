package com.droidheat.musicplayer.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
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
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;

import com.droidheat.musicplayer.BuildNotification;
import com.droidheat.musicplayer.Constants;
import com.droidheat.musicplayer.MusicWidget4x1;
import com.droidheat.musicplayer.R;
import com.droidheat.musicplayer.activities.HomeActivity;
import com.droidheat.musicplayer.database.CategorySongs;
import com.droidheat.musicplayer.manager.CommonUtils;
import com.droidheat.musicplayer.manager.SharedPrefsManager;
import com.droidheat.musicplayer.manager.SongsUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.List;

public class MusicPlayback extends MediaBrowserServiceCompat implements
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnPreparedListener{

    // Available PlayBackStates
    //  STATE_NONE = 0;
    //  STATE_STOPPED = 1;
    //  STATE_PAUSED = 2;
    //  STATE_PLAYING = 3;


    /******* ---------------------------------------------------------------
     Private
     ----------------------------------------------------------------*******/

    private final String tag = "NNN";

    private MediaPlayer mMediaPlayer;
    private MediaPlayer mMediaPlayer2;
    public static MediaSessionCompat mMediaSessionCompat;
    private PlaybackStateCompat.Builder mPlaybackStateBuilder;
    private SharedPrefsManager mSharedPrefsManager;
    private SongsUtils mSongsUtils;

    private  Equalizer mEqualizer;
    private BassBoost mBassBoost;
    private Virtualizer mVirtualizer;
    private boolean autoPaused = false;

    private int currentMediaPlayer = 0; // 0 - mMediaPlayer; 1 - mMediaPlayer2
    private int crossfadeDuration = 3000; // 3 seconds
    private Handler mHandler;
    private boolean isAutoPaused = false;
    private final int NOTIFICATION_ID = 34213134;
    private int resumePosition;
    /******* ---------------------------------------------------------------
     Service Methods and Intents
     ----------------------------------------------------------------*******/
    public MusicPlayback(){
        super();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();

        mSharedPrefsManager = new SharedPrefsManager();
        mSharedPrefsManager.setContext(this);
        mSongsUtils = SongsUtils.getInstance();
        mSongsUtils.setContext(this);

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
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            /*
             * Analyze and Acting on the intent received by Service
             * Every request to Service should be with one of the intents in our switch
             */
            MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);

            Log.d(tag, "Intent received.");
            String action = intent.getAction();
            Log.d(tag, action== null? "null": "khac null");
            if (action != null) {
                switch (action) {
                    case Constants.ACTION.PLAYING: {
                        resetMediaPlayerPosition();
                        processPlayRequest();
                        break;
                    }
                    case Constants.ACTION.PAUSE: {

                        resetMediaPlayerPosition();

                        processPlayPause();
                        break;
                    }
                    case Constants.ACTION.PREVIOUS: {
                        processPrevRequest();
                        break;
                    }
                    case Constants.ACTION.NEXT: {
                        processNextRequest();
                        break;
                    }
                    case Constants.ACTION.REPEAT: {
                        musicWidgetsReset();
                        break;
                    }
                    case Constants.ACTION.STOP: {
                        if (!mSharedPrefsManager.getBoolean("persistentNotificationPref", false)) {
                            processCloseRequest();
                        } else {
                            processPauseRequest();
                        }
                        break;
                    }
                    case Constants.ACTION.ACTION_PERSISTENT_NOTIFICATION:
                        if (mPlaybackStateBuilder.build().getState() != PlaybackStateCompat.STATE_PLAYING) {
                            showPausedNotification();
                        }
                        break;
                    default: {
                        initNotification();
                    }
                }
            } else {
                initNotification();
            }
        }
        return START_STICKY;
    }
    /*
     * Getting and Switching MediaPlayers for Cross-fade
     */
    private MediaPlayer getCurrentMediaPlayer() {
        return (currentMediaPlayer == 0) ? mMediaPlayer : mMediaPlayer2;
    }

    private void checkErrorInPrefs() {
        if (mSongsUtils.getCurrentMusicID() > mSongsUtils.queue().size() - 1) {
            mSongsUtils.setCurrentMusicID(0);
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

    private void crossfade() {
        mHandler.post(detectCrossFadeRunnable);
    }

    private void cancelCrossfade() {
        mHandler.removeCallbacks(detectCrossFadeRunnable);
    }

    // Detects and starts cross-fading
    public Runnable detectCrossFadeRunnable = new Runnable() {
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
    };

    // Volume
    private float mFadeOutVolume = 1.0f;
    private float mFadeInVolume = 0.0f;


     public Runnable crossFadeRunnable = new Runnable() {
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
                    mMediaPlayer.start();
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
    };

    /*
     * saveData() writes current song parameters to sharedPrefs which can be retrieved in
     * other activities or fragments as well as when we start app next time
     * musicID: is id of current item in queue
     * title, artist, album, albumid: are all fields of SongModel()
     *
     */

    private void saveData(){
        int musicID = mSongsUtils.getCurrentMusicID();
        mSharedPrefsManager.setInteger(Constants.PREFERENCES.audio_session_id,
                getCurrentMediaPlayer().getAudioSessionId());
        try {


            mSharedPrefsManager.setInteger(Constants.PREFERENCES.MUSIC_ID ,
                    musicID);

            mSharedPrefsManager.setString(Constants.PREFERENCES.TITLE ,
                    mSongsUtils.queue().get(musicID).getTitle());

            mSharedPrefsManager.setString(Constants.PREFERENCES.ARTIST ,
                    mSongsUtils.queue().get(musicID).getArtist());

            mSharedPrefsManager.setString(Constants.PREFERENCES.ALBUM ,
                    mSongsUtils.queue().get(musicID).getAlbum());

            mSharedPrefsManager.setString(Constants.PREFERENCES.ALBUMID ,
                    mSongsUtils.queue().get(musicID).getAlbumID());

            mSharedPrefsManager.setString(Constants.PREFERENCES.RAW_PATH ,
                    mSongsUtils.queue().get(musicID).getPath());

            mSharedPrefsManager.setString(Constants.PREFERENCES.DURATION ,
                    mSongsUtils.queue().get(musicID).getDuration());

            mSharedPrefsManager.setInteger(Constants.PREFERENCES.DURATION_IN_MS ,
                    getCurrentMediaPlayer().getDuration());
        }catch (Exception e ){
            Log.d(tag, "Unable to save song info in persistent storage. MusicID " + musicID);
        }
    }

    private void doPushPlay(int id) {
        mSongsUtils.setCurrentMusicID(id);
        getCurrentMediaPlayer().reset();
        if (successfullyRetrievedAudioFocus()) {
            showPausedNotification();
            return;
        }
        setMediaPlayer(mSongsUtils.queue().get(id).getPath());
    }

    private void processNextRequest(){
        resetMediaPlayerPosition();

        int musicID = mSongsUtils.getCurrentMusicID();


        if (musicID + 1 != mSongsUtils.queue().size()){
            musicID++;
        }else {
            musicID = 0;
        }
        mSongsUtils.setCurrentMusicID(musicID);
        if (successfullyRetrievedAudioFocus()) {
            showPausedNotification();
            return;
        }

        Log.d(tag, "Skipping to Next track.");
        setMediaPlayer(mSongsUtils.queue().get(musicID).getPath());
    }

    private void processPrevRequest(){
        resetMediaPlayerPosition();
        if (getCurrentMediaPlayer().getCurrentPosition() < 5000){
            int musicID =  mSongsUtils.getCurrentMusicID();
            if (musicID > 0) {
                musicID--;
                mSongsUtils.setCurrentMusicID(musicID);
                Log.d(tag, "Skipping to Previous track.");
                if (successfullyRetrievedAudioFocus()) {
                    showPausedNotification();
                    return;
                }
                setMediaPlayer(mSongsUtils.queue().get(musicID).getPath());
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
        Log.d(tag, "Processing Play Request for musicID: " + mSongsUtils.getCurrentMusicID());

        setMediaPlayer(mSongsUtils.queue().get(mSongsUtils.getCurrentMusicID()).getPath());
    }

    private void processPauseRequest(){
        if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PLAYING){
            mMediaSessionCompat.setActive(false);
            getCurrentMediaPlayer().pause();
            resumePosition = getCurrentMediaPlayer().getCurrentPosition();
            mSharedPrefsManager.setInteger(Constants.PREFERENCES.song_position,
                    getCurrentMediaPlayer().getCurrentPosition());
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
            showPausedNotification();
        }
    }


    private void processCloseRequest(){
        stopSelf();
    }

    private void processPlayPause(){
        Log.d(tag, "Play/Pausing");
        if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PLAYING){
            processPlayPause();
        }else if (mPlaybackStateBuilder.build().getState() == PlaybackStateCompat.STATE_PAUSED){
            if (successfullyRetrievedAudioFocus()) {
                return;
            }
            mMediaSessionCompat.setActive(true);
            getCurrentMediaPlayer().start();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            showPlayingNotification();
        } else {
            processPlayRequest();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mSharedPrefsManager.setInteger(Constants.PREFERENCES.song_position,
                getCurrentMediaPlayer().getCurrentPosition());
        Log.d(tag, "Shutting MediaPlayer service down...");

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assert audioManager != null;
        audioManager.abandonAudioFocus(this);
        unregisterReceiver(mNoisyReceiver);
        setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
        mMediaSessionCompat.setActive(false);
        mMediaSessionCompat.release();
        getCurrentMediaPlayer().release();
        try {
            mEqualizer.release();
            mBassBoost.release();
            mVirtualizer.release();
        } catch (Exception ignored) {}
        stopForeground(true);
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID);
    }

    private void setMetaData(){
        if (mSongsUtils.queue().size() == 0){
            MediaMetadataCompat mMediaMetadataCompat = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE,
                            mSharedPrefsManager.getString(Constants.PREFERENCES.TITLE,
                                    "Placeholder Title"))
                    .putString(MediaMetadata.METADATA_KEY_ALBUM,
                            mSharedPrefsManager.getString(Constants.PREFERENCES.ALBUM,
                                    "Placeholder Album"))
                    .putString(MediaMetadata.METADATA_KEY_ARTIST,
                            mSharedPrefsManager.getString(Constants.PREFERENCES.ARTIST,
                                    "Placeholder Artist"))
                    .putLong(MediaMetadata.METADATA_KEY_DURATION,
                            (long) mSharedPrefsManager.getInteger(Constants.PREFERENCES.DURATION_IN_MS,
                                    0))
                    .putBitmap(MediaMetadata.METADATA_KEY_ART,
                            grabAlbumArt(mSharedPrefsManager.getString(Constants.PREFERENCES.ALBUMID,
                                    "0")))
                    .build();
            mMediaSessionCompat.setMetadata(mMediaMetadataCompat);
        }else {
            int musicID = mSongsUtils.getCurrentMusicID();
            MediaMetadataCompat mMediaMetadataCompat = new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE,
                            mSongsUtils.queue().get(musicID).getTitle())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM,
                            mSongsUtils.queue().get(musicID).getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST,
                            mSongsUtils.queue().get(musicID).getArtist())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                            (long) mSharedPrefsManager.getInteger(Constants.PREFERENCES.DURATION_IN_MS, 0))
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                            grabAlbumArt(mSongsUtils.queue().get(musicID).getAlbumID()))
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
        if(Build.VERSION.SDK_INT >  Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE));

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
        musicWidgetsReset();

        createChannel();
        NotificationCompat.Builder builder
                = BuildNotification.from(MusicPlayback.this, mMediaSessionCompat);

        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.STOP), 0);

        PendingIntent prevIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.PREVIOUS), 0);
        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.PAUSE), 0);
        PendingIntent nextIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.NEXT),
                0);

        builder.addAction(new NotificationCompat.Action(R.drawable.app_previous, "Previous", prevIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_pause, "Play", playPauseIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_next, "Next", nextIntent));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.drawable.ic_music_note_black_24dp);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0));
        builder.setDeleteIntent(pCloseIntent);
        builder.setColor(ContextCompat.getColor(this, (new CommonUtils(this)).accentColor(mSharedPrefsManager)));
        builder.setShowWhen(false);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void showPausedNotification() {
        musicWidgetsReset();

        createChannel();
        NotificationCompat.Builder builder
                = BuildNotification.from(MusicPlayback.this, mMediaSessionCompat);

        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.STOP), 0);

        PendingIntent prevIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.PREVIOUS), 0);
        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.PAUSE), 0);
        PendingIntent nextIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.NEXT), 0);

        builder.addAction(new NotificationCompat.Action(R.drawable.app_previous, "Previous", prevIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_play, "Play", playPauseIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_next, "Next", nextIntent));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.drawable.ic_music_note_black_24dp);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0));
        builder.setDeleteIntent(pCloseIntent);
        builder.setColor(ContextCompat.getColor(this, (new CommonUtils(this)).accentColor(mSharedPrefsManager)));
        builder.setShowWhen(false);
        startForeground(NOTIFICATION_ID, builder.build());
        if (!mSharedPrefsManager.getBoolean(Constants.PREFERENCES.persistentNotificationPref, false)) {
            stopForeground(false);
        }
    }

    private void initNotification() {
        createChannel();
        NotificationCompat.Builder builder
                = BuildNotification.from(MusicPlayback.this, mMediaSessionCompat);

        PendingIntent pCloseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.STOP), 0);

        PendingIntent prevIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.PREVIOUS), 0);
        PendingIntent playPauseIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.PAUSE), 0);
        PendingIntent nextIntent = PendingIntent.getService(this, 0,
                (new Intent(this, MusicPlayback.class)).setAction(Constants.ACTION.NEXT), 0);

        builder.addAction(new NotificationCompat.Action(R.drawable.app_previous, "Previous", prevIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_play, "Play", playPauseIntent));
        builder.addAction(new NotificationCompat.Action(R.drawable.app_next, "Next", nextIntent));
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.drawable.ic_music_note_black_24dp);
        builder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(1, 2).setMediaSession(getSessionToken()));
        builder.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0));
        builder.setDeleteIntent(pCloseIntent);
        builder.setColor(ContextCompat.getColor(this, (new CommonUtils(this)).accentColor(mSharedPrefsManager)));
        builder.setShowWhen(false);
        startForeground(NOTIFICATION_ID, builder.build());

    }

    /******* ---------------------------------------------------------------
     Defaults
     ----------------------------------------------------------------*******/

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    /******* ---------------------------------------------------------------
     AudioFocus
     ----------------------------------------------------------------*******/

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange){
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (getCurrentMediaPlayer()!= null){
                    if (getCurrentMediaPlayer().isPlaying()){
                        processPauseRequest();
                        isAutoPaused = true;
                        Log.d(tag, "Auto paused enabled");
                    }
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (getCurrentMediaPlayer() != null) {
                    getCurrentMediaPlayer().setVolume(0.3f, 0.3f);
                }
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                if (getCurrentMediaPlayer() != null) {
                    Log.d(tag, "Auto-paused is " + ((isAutoPaused) ? "enabled" : "disabled"));
                    if (!getCurrentMediaPlayer().isPlaying() && isAutoPaused) {
                        processPlayPause();
                        isAutoPaused = false;
                        Log.d(tag, "Auto paused disabled");
                    }
                    getCurrentMediaPlayer().setVolume(1.0f, 1.0f);
                }
                break;
        }
    }

    private boolean successfullyRetrievedAudioFocus(){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        assert audioManager!=null;
        int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result != AudioManager.AUDIOFOCUS_GAIN){
            Log.d(tag, "Failed to gain AudioFocus");
        }
        return result != AudioManager.AUDIOFOCUS_GAIN;
    }

    private void setEqualizer(){
        boolean isEqInSettings = mSharedPrefsManager.getBoolean(Constants.PREFERENCES.turnEqualizer, false);
        int currentEqProfile = mSharedPrefsManager.getInteger(Constants.PREFERENCES.currentEqProfile, 0);
        try {
            mEqualizer = new Equalizer(0, getCurrentMediaPlayer().getAudioSessionId());
            mEqualizer.setEnabled(isEqInSettings);
            for (int i = 0; i < mEqualizer.getNumberOfBands(); i++) {
                mEqualizer.setBandLevel((short) i, (short) mSharedPrefsManager.getInteger(
                        "profile" + currentEqProfile + "Band" + i, 0));
            }
            Log.d(tag, "Equalizer successfully initiated with profile " + currentEqProfile);
        } catch (Exception e) {
            (new CommonUtils(this)).showTheToast("Unable to run Equalizer");
        }
        try {
            mBassBoost = new BassBoost(0, getCurrentMediaPlayer().getAudioSessionId());
            mBassBoost.setEnabled(isEqInSettings);
            mBassBoost.setStrength((short) mSharedPrefsManager.getInteger(Constants.PREFERENCES.bassLevel + currentEqProfile,
                    0));
        } catch (Exception ignored) {}
        try {
            mVirtualizer = new Virtualizer(0, getCurrentMediaPlayer().getAudioSessionId());
            mVirtualizer.setEnabled(isEqInSettings);
            mVirtualizer.setStrength((short) mSharedPrefsManager.getInteger(Constants.PREFERENCES.vzLevel + currentEqProfile,
                    0));
        } catch (Exception ignored) {}
    }

    public void addVoteToTrack(String path){
        try {
            path = path.trim();
            Log.d(tag, path );
            CategorySongs mCategorySongs = CategorySongs.getInstance();

            mCategorySongs.newRenderDB(getApplicationContext());

            mCategorySongs.open();

            if (mCategorySongs.checkRow(path)){

                mCategorySongs.updateRow(path);
            }else {
                mCategorySongs.addRow(1, mSongsUtils.queue().get(mSongsUtils.getCurrentMusicID()));
            }
            Log.d(tag, mCategorySongs.checkRow(path) +"" );
            mCategorySongs.close();
        }catch (Exception e) {
            e.printStackTrace();
            Log.d(tag, "addVoteToTrack crashed.");
            Log.d(tag, e.getMessage());
        }

    }



    /******* ---------------------------------------------------------------
     MediaPlayer
     ----------------------------------------------------------------*******/
    @Override
    public void onCompletion(MediaPlayer mp) {
        getCurrentMediaPlayer().reset();
        resetMediaPlayerPosition();

        if (!mSharedPrefsManager.getBoolean(Constants.PREFERENCES.repeat, false)){
            Log.d(tag, "OnCompletion playing next track");
            processNextRequest();
        }else {
            setMediaPlayer(mSongsUtils.queue().get(mSongsUtils.getCurrentMusicID()).getPath());
        }

    }
    public void resetMediaPlayerPosition() {
        mSharedPrefsManager.setInteger(Constants.PREFERENCES.song_position,0);
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
            mSharedPrefsManager.getString(Constants.PREFERENCES.RAW_PATH,"")
                    .equals(mSongsUtils.queue()
                            .get(mSongsUtils.getCurrentMusicID())
                            .getPath())){
            mMediaSessionCompat.getController()
                    .getTransportControls()
                    .seekTo(mSharedPrefsManager.getInteger(Constants.PREFERENCES.song_position, 0));

            /*
             * Setting Equalizer
             */
            setEqualizer();

            /*
             * Setting metaData
             */
            setGraphics();
            Log.d(tag, "starting playback");

            mMediaSessionCompat.setActive(true);
            autoPaused= false;
            getCurrentMediaPlayer().start();
            setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
            showPlayingNotification();
        }
    }

    private void initMediaPlayer(){
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(1.0f, 1.0f);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer2 = new MediaPlayer();
        mMediaPlayer2.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer2.setVolume(1.0f, 1.0f);
        mMediaPlayer2.setOnCompletionListener(this);
        mMediaPlayer2.setOnPreparedListener(this);
        mMediaPlayer2.setAudioSessionId(mMediaPlayer.getAudioSessionId());
        mSharedPrefsManager.getInteger(Constants.PREFERENCES.audio_session_id, mMediaPlayer.getAudioSessionId());

    }

    private void setMediaPlayer(String path){
        Log.d(tag, path);
        getCurrentMediaPlayer().reset();
        File file = new File(path);
        if (file.exists()){
            try {
                addVoteToTrack(path);
                getCurrentMediaPlayer().setDataSource(path);
            }catch (Exception e){
                processNextRequest();
                e.printStackTrace();
            }
            try {
                getCurrentMediaPlayer().prepare();
            } catch (IOException ignored) {
            }
        } else {
            processNextRequest();
            Log.d(tag, "Error finding file so we skipped to next.");
            (new CommonUtils(this)).showTheToast("Error finding music file");
        }
    }

    private void initMediaSession(){
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(),
                MediaButtonReceiver.class);

        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(),"tag",
                mediaButtonReceiver, null);

        mMediaSessionCompat.setCallback(mMediaSessionCallback);
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                0 ,mediaButtonIntent,0);
        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);

        mPlaybackStateBuilder = new PlaybackStateCompat.Builder();

        setSessionToken(mMediaSessionCompat.getSessionToken());

        setMetaData();
    }

    private void initNoisyReceiver(){
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        IntentFilter filter =new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver( mNoisyReceiver, filter);
    }
    /******* ---------------------------------------------------------------
     Music Widgets
     ----------------------------------------------------------------*******/

    private void musicWidgetsReset() {
        updateMusicWidget(this, MusicWidget4x1.class);
  /*      updateMusicWidget(this, MusicWidget4x1v2.class);
        updateMusicWidget(this, MusicWidget4x2.class);*/
    }
    private void updateMusicWidget(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra("state", mPlaybackStateBuilder.build().getState());
        int[] ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, cls));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }
    /******* ---------------------------------------------------------------
     Broad Cast Service
     ----------------------------------------------------------------*******/



    /******* ---------------------------------------------------------------
     Init Music Media
     ----------------------------------------------------------------*******/
    /*private void playMedia() {
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            isInitAudioError = false;
            Log.d(tag, "playMedia");

        }
    }*/





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
            getCurrentMediaPlayer().seekTo((int) (long) pos);

            Log.d(tag, "onSeekTo: " + pos);
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

        mPlaybackStateBuilder.setState(state, getCurrentMediaPlayer().getCurrentPosition(), 1.0f,
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
            if (getCurrentMediaPlayer() != null && getCurrentMediaPlayer().isPlaying()) {
                processPauseRequest();
            }
        }
    };
}
