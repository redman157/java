package com.android.music_player.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.music_player.R;
import com.android.music_player.activities.HomeActivity;
import com.android.music_player.interfaces.Status;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageUtils;
import com.android.music_player.utils.SharedPrefsUtils;
import com.android.music_player.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.android.music_player.interfaces.Status.COMPLETE;
import static com.android.music_player.interfaces.Status.NEXT;
import static com.android.music_player.interfaces.Status.NEXTING;
import static com.android.music_player.interfaces.Status.PAUSED;
import static com.android.music_player.interfaces.Status.PAUSING;
import static com.android.music_player.interfaces.Status.PLAYED;
import static com.android.music_player.interfaces.Status.PLAYING;
import static com.android.music_player.interfaces.Status.PREVIOUS;
import static com.android.music_player.interfaces.Status.PREVIOUSING;
import static com.android.music_player.interfaces.Status.STOPPED;
import static com.android.music_player.interfaces.Status.STOPPING;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        AudioManager.OnAudioFocusChangeListener{
    // Binder given to clients


    private final IBinder iBinder = new LocalBinder();
    private String tag = "BBB";
    private Status status = PAUSED;
    //MediaSession
    private MediaSessionManager mMediaSessionManager;
    private MediaSessionCompat mMediaSessionCompat;
    private MediaControllerCompat.TransportControls mMediaTransportControls;
    private SongManager mSongManager;
    private SharedPrefsUtils mSharedPrefsUtils;
    private AudioManager mAudioManager;
    //AudioPlayer notification ID

    private final Handler handler = new Handler();
    private boolean isInitAudioError;
    /**
     * Static*/
    public static final int NOTIFICATION_ID = 101;
    public static MediaPlayer mMediaPlayer;
    public static ArrayList<SongModel> mSongs, mSongShuffle;
    private int position;
    private boolean isAutoPlay = false;
    private Intent iIntentSeekBar, iNewMusic, iCheckPlayActivity, iPlayPauseActivity;
//    private Intent iPrevToActivity, iNextToActivity;
    private androidx.core.app.NotificationCompat.Builder notificationBuilder = null;
    private boolean isRepeat, isShuffle;
    private String type;
    private Utils mUtils;
    /**
     * Service Binder
     */
    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods

            return MediaPlayerService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mMediaSessionCompat != null) {
            mMediaSessionCompat.release();
        }
        removeNotification();
        return super.onUnbind(intent);
    }

    /**
    *  Init Service
    * */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(tag, "Start service" );

        // ---Set up intent for seek bar broadcast ---
        iPlayPauseActivity = new Intent(Constants.ACTION.BROADCAST_PLAY_PAUSE);
        iIntentSeekBar = new Intent(Constants.ACTION.BROADCAST_SEEK_BAR);
        iCheckPlayActivity = new Intent(Constants.ACTION.IS_PLAY);
        iNewMusic = new Intent(Constants.ACTION.BROADCAST_PLAY_NEW_AUDIO);


        // khởi tạo media và chờ start
        // cần tao 1 broad cast để play
        initMediaPlayer();
        initMediaSession();

        registerReceiver(brNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
        registerReceiver(brResetMusic,  new IntentFilter(Constants.ACTION.BROADCAST_RESET_AUDIO));
        registerReceiver(brStopMusic, new IntentFilter(Constants.ACTION.BROADCAST_STOP_AUDIO));
        registerReceiver(brCloseNotification, new IntentFilter(Constants.ACTION.CLOSE_NOTIFICATION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            stopMedia();
            releaseMedia();
            removeNotification();
        }
        if (mAudioManager != null) {
            removeAudioFocus();
        }

        unregisterReceiver(brNoisyReceiver);
        unregisterReceiver(brResetMusic);
        unregisterReceiver(brStopMusic);
        unregisterReceiver(brCloseNotification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            mSongManager = SongManager.getInstance();
            mSongManager.setContext(this);
            if (!requestAudioFocus()) {
                //Could not gain focus
                stopSelf();
            }
            //Handle Intent action from MediaSession.TransportControls
            handleUIActions(intent);
        }
        return START_NOT_STICKY;
    }


    private boolean initBundle(Intent intent){
        mSharedPrefsUtils = new SharedPrefsUtils(this);
        if (intent.getExtras() != null) {
            Log.d(tag, "Service --- initBundle: " + intent.getExtras().getString(Constants.INTENT.TYPE, "None"));
        }
        mUtils = new Utils(intent);

        position = mUtils.getInteger(Constants.INTENT.CURR_POS, -1);
        type = mUtils.getString(Constants.INTENT.TYPE, "None");
        Log.d(tag, "Service --- type: "+type + " --- curr_pos: "+position);
        if (!type.equals("None") && position!= -1){
            // true là truyền từ activty
            Log.d("XXX","initBundle: "+type + " --- pos: "+position);
            mSongs = mSongManager.getCurrentSongs(type);

            mSongManager.setPositionCurrent(position);
            return true;
        }else {
            // false là từ notifcation
            Log.d(tag, "Service --- initBundle is null");
            position = mSongManager.getPositionCurrent();
            type = mSongManager.getTypeCurrent();
            return false;
        }
    }

    private void initMediaSession(){
        if (mMediaSessionManager != null) {
            return; //mediaSessionManager exists
        }
        mMediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession

        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        mMediaTransportControls = mMediaSessionCompat.getController().getTransportControls();

        //set MediaSession -> ready to receive media commands
        mMediaSessionCompat.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Attach Callback to receive MediaSession updates
        // MediaTransportControls -> mMediaSessionCompat.setCallBack -> thao tác cho mediaplayer
        mMediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                try {
                    super.onPlay();
                    if (!successfullyRetrievedAudioFocus()) {
                        return;
                    }
                    Log.d(tag, "Service --- onPlay:" + (mSongs.get(position)).getPath());
                    playMedia(mSongs.get(position).getPath());

                } finally {
                    Log.d(tag, "Service --- onPlay: "+status.toString());
                }
            }

            @Override
            public void onPause() {
                try {
                    super.onPause();
                    pauseMedia();
                    Log.d("MMM", "Service --- onPause position:  "+ position);

                } finally {
                    status = Status.PAUSED;
                    initNotification(Constants.NOTIFICATION.PAUSE, mSongManager.getPositionCurrent());
                    // báo len UI là pause
                }


                if (isAutoPlay) {
                    try {
                        isAutoPlay = false;
                        status = PLAYING;
                        mMediaTransportControls.play();
                        Log.d(tag, "Service --- PLAY: " + status.toString());
                    }finally {
                        status = PLAYED;
                    }
                }
            }

            @Override
            public void onSkipToNext() {
                try {
                    status = NEXT;
                    super.onSkipToNext();
                    Log.d(tag, "skipToNext: Enter");
                    Log.d(tag, "Service --- skipToNext: "+SongManager.getInstance().getPositionCurrent());
                    if (!isRepeat) {
                        // thực hiện next
                        skipToNext();
                    }
                }finally {
                    status = PAUSED;
                    try {
                        if (isAutoPlay) {
                            isAutoPlay = false;
                            position = mSongManager.getPositionCurrent();
                            state_play();
                            Log.d(tag, "Service --- PLAY: " + status.toString());
                        }
                    }finally {
                        status = PLAYED;
                        initNotification(Constants.ACTION.PLAY, position);
                    }
                }
            }

            @Override
            public void onSkipToPrevious() {
                try {
                    status = PREVIOUS;
                    super.onSkipToPrevious();
                    Log.d(tag, "skipToPrevious: Enter");
                    Log.d(tag, "Service --- skipToPrevious: "+SongManager.getInstance().getPositionCurrent());
                    if (!isRepeat) {
                        skipToPrevious();
                    }
                }finally {
                    status = PAUSED;
                    try {
                        if (isAutoPlay) {
                            isAutoPlay = false;
                            position = mSongManager.getPositionCurrent();
                            state_play();
                            Log.d(tag, "Service --- PLAY: " + status.toString());
                        }
                    }finally {
                        status = PLAYED;
                        initNotification(Constants.ACTION.PLAY, position);
                    }
                }
            }
            @Override
            public void onStop() {
                super.onStop();
                try {
                    status = STOPPING;
                    if(mMediaPlayer!= null) {
                        mSongManager.setPositionCurrent(position);
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG,0);
                        stopMedia();
                        Log.d(tag, "Service --- onStop Media: Enter");
                    }
                }finally {
                    status = STOPPED;
                }
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
                if (status == PLAYED) {
                    mMediaPlayer.seekTo((int) position);
                    setupHandler();
                    mMediaPlayer.start();
                } else if (status == PAUSED) {
                    mMediaPlayer.seekTo((int) position);
                    setupHandler();
                }
            }
        });
    }

    /**
     * ACTION INTENT
     */
    private void handleUIActions(Intent iAction){
        if (iAction == null || iAction.getAction() == null) {
            return;
        }
        String action = iAction.getAction();
        initBundle(iAction);

        if (action != null) {
            switch (action) {
                case Constants.ACTION.SHUFFLE:
                    isShuffle = iAction.getBooleanExtra(Constants.INTENT.IS_SHUFFLE, false);

                    setShuffleSong(isShuffle);
                    if (status == PLAYED) {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG,0);
                        isAutoPlay = true;
                    }
                    state_pause();

                    break;
                case Constants.ACTION.PLAY:
                    state_stop();
                    state_play();
                    break;
                case Constants.ACTION.PAUSE:
                    state_pause();
                    break;
                case Constants.ACTION.NEXT:
                    state_next();
                    break;
                case Constants.ACTION.PREVIOUS:
                    state_previous();
                    break;
                case Constants.ACTION.STOP:
                    if (status == PLAYED || status == PLAYING || status == PAUSED || status == PAUSING) {
                        status = STOPPING;
                        mMediaTransportControls.stop();
                    }else {
                        Log.d(tag, "Service --- STOP: " + status.toString());
                    }
                    break;
                case Constants.ACTION.SEEK:
                    state_seek(iAction);

                    break;
                case Constants.ACTION.IS_PLAY:
                    if (mSongs != null && mSongs.size() > 0) {
                        if(status == PLAYED) {
                            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                                iCheckPlayActivity.putExtra(Constants.INTENT.IS_PLAY_MEDIA_SERVICE, true);
                                sendBroadcast(iCheckPlayActivity);
                            }
                        }else if (status == PAUSED){
                            if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
                                iCheckPlayActivity.putExtra(Constants.INTENT.IS_PLAY_MEDIA_SERVICE, false);
                                sendBroadcast(iCheckPlayActivity);
                            }
                        }else {
                            Log.d(tag, "Service --- IS_PLAY: "+status.toString());
                        }

                    }
                    break;
                case Constants.ACTION.REPEAT:
                    this.isRepeat = iAction.getExtras().getBoolean(Constants.INTENT.IS_REPEAT, false);
                    Log.d(tag, "Service --- Repeat: "+isRepeat);
                    break;
            default:
                status = Status.PAUSED;
                initNotification(Constants.NOTIFICATION.PAUSE, position);
                break;
            }
        }
    }

    private void state_stop() {
        if (status == STOPPED) {
            initMediaPlayer();
        }
    }

    private void state_seek(Intent iAction) {
        if (status == PLAYED || status == PAUSED) {
            if (status == PLAYED){

            }
            int pos = iAction.getExtras().getInt(Constants.INTENT.POSITION_SONG, 0);
            if (pos != 0) {
                mMediaTransportControls.seekTo(pos);
            }
        }else {
            Log.d(tag, "Service --- SEEK: " + status.toString());
        }
    }

    private void state_play() {
        if (status == PAUSED) {
            status = PLAYING;
            mMediaTransportControls.play();
            Log.d(tag, "Service --- PLAY: " + status.toString());
        } else if (status == PAUSING) {
            Log.d(tag, "Service --- PLAY: " + status.toString());
        }
    }

    private void state_previous() {
        if (status == PLAYED || status == PAUSED) {
            status = PREVIOUSING;
            mMediaTransportControls.skipToPrevious();
        } else {
            Log.d(tag, "Service --- PREVIOUS: " + status.toString());
        }
    }

    private void state_next() {
        if (status == PLAYED || status == PAUSED) {
            status = NEXTING;
            mMediaTransportControls.skipToNext();
        } else {
            Log.d(tag, "Service --- NEXT: " + status.toString());
        }
    }

    private void state_pause() {
        if (status == PLAYED) {
            status = PAUSING;
            mMediaTransportControls.pause();
        } else {
            Log.d(tag, "Service --- PAUSE: " + status.toString());
        }
    }

    private void setShuffleSong(boolean isShuffle) {
        if (isShuffle){
            mSongShuffle = mSongManager.getShuffleSongs();
            // set mảng shuffle bằng mảng chính
        }else {
            mSongShuffle = mSongManager.getCurrentSongs();
        }
        mSongs = mSongShuffle;
    }

    private void initMediaPlayer()  {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(1.0f, 1.0f);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnInfoListener(this);
    }

    private boolean requestAudioFocus() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                mAudioManager.abandonAudioFocus(this);
    }



    private void LogMediaPosition(int position){
        try{
            int mediaPosition = mMediaPlayer.getCurrentPosition();

            int mediaMax = mSongs.get(position).getTime();
           /* Log.d("MMM", "LogMediaPosition : Enter ==== MediaPosition: "+mediaPosition +" ==== " +
                    "MediaMax: "+mediaMax +" ==== Song Title: "+ mSongs.getData(SongManager.getInstance().getPositionCurrent()).getSongName());*/
            iIntentSeekBar.putExtra("current_pos", mediaPosition);
            mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG, mediaPosition);

            iIntentSeekBar.putExtra("media_max", mediaMax);
            iIntentSeekBar.putExtra("song_title",
                    mSongs.get(position).getSongName());
            sendBroadcast(iIntentSeekBar);

        }catch (Exception e){
            if (e instanceof NullPointerException){
                mSongs = mSongManager.getCurrentSongs();

            }
            Log.d(tag, "Tên Exception: "+e.toString());
            Log.e(tag, "ERROR: " + e.getMessage());
        }
    }


    private void playMedia(String path){
        if (!mMediaPlayer.isPlaying()) {
            setMediaPlayer(path);
            isInitAudioError = false;
        }
    }

    private void setMediaPlayer(String path) {
        if (mMediaPlayer != null){
            mMediaPlayer.reset();
        }

        File file = new File(path);
        if (file.exists()) {
            try {
                addVoteToTrack(path);
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
            } catch (IOException e) {
                if (status == PLAYED ||status == PAUSED) {
                    mMediaTransportControls.skipToNext();
                }
                e.printStackTrace();
            }

        } else {
            if (status == PAUSED) {
                mMediaTransportControls.skipToNext();
            }
            Log.d(tag, "Error finding file so we skipped to next.");
            Log.d(tag, "Error finding music file");

        }
    }

    private void pauseMedia() {
        if (mMediaPlayer.isPlaying()) {
            handler.removeCallbacks(sendUpdatesToUI);
            mMediaPlayer.pause();
        }
    }

    private void stopMedia() {

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            try {
                Log.d(tag, "stopMedia: Enter");
                handler.removeCallbacks(sendUpdatesToUI);
                mMediaPlayer.stop();
                mMediaPlayer.prepare();
                mMediaPlayer.reset();
            } catch (Exception e) {
                Log.d(tag, "stopMedia ERROR: " + e.getMessage());
            }
        }
    }

    private void releaseMedia(){
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    private void skipToPrevious(){
        SongManager.getInstance().setPositionCurrent(position - 1);
        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG,0);
        stopMedia();

        iNewMusic.putExtra(Constants.INTENT.CHOOSE_POS,
                SongManager.getInstance().getPositionCurrent());
        sendBroadcast(iNewMusic);
    }

    private void skipToNext(){
        SongManager.getInstance().setPositionCurrent(position + 1);
        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG,0);
        stopMedia();
        iNewMusic.putExtra(Constants.INTENT.CHOOSE_POS,
                SongManager.getInstance().getPositionCurrent());
        sendBroadcast(iNewMusic);

        isAutoPlay = true;
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {

            LogMediaPosition(position);
            handler.postDelayed(this, 500);
        }
    };
    // ---Send seek bar info to activity----
    private void setupHandler() {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000);
    }
    /******* ---------------------------------------------------------------
     Notifications
     ----------------------------------------------------------------*******/
    private void updateMetaData(SongModel songModel){
        Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_music_note_white_24dp);
        // Update the current metadata
        if (SongManager.getInstance().getPositionCurrent() != -1){
            mMediaSessionCompat.setMetadata(new MediaMetadataCompat.Builder()
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, mSongs.get(SongManager.getInstance().getPositionCurrent()).getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, mSongs.get(SongManager.getInstance().getPositionCurrent()).getAlbum())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, songModel.getSongName())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, songModel.getTime())
                    .build());
        }
    }

    private void removeNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        stopMedia();
        releaseMedia();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager
                    mNotificationManager =
                    (NotificationManager) this
                            .getSystemService(Context.NOTIFICATION_SERVICE);
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

    private void initNotification(String type, int position) {
        Log.d("ZZZ", "service --- initNotification:"+ type + " ---- "+position);
        ArrayList<SongModel> mSongs = SongManager.getInstance().getCurrentSongs();
        int icon_Action = 0;

        PendingIntent playPauseIntent = null;
        PendingIntent nextIntent;
        PendingIntent prevIntent;

        // GỬI INTENT TỪ NOTI VỀ SERVICE
        Intent iNext = new Intent(this, MediaPlayerService.class);
        Intent iPrev = new Intent(this, MediaPlayerService.class);
        if (!type.equals(Constants.NOTIFICATION.REPEAT)){
            iNext.setAction(Constants.ACTION.NEXT);
            iPrev.setAction(Constants.ACTION.PREVIOUS);
        }else {
            iNext.setAction(null);
            iPrev.setAction(null);
        }

        if (type.equals(Constants.NOTIFICATION.PLAY)){
            icon_Action = R.drawable.ic_media_pause_light;
            playPauseIntent = PendingIntent.getService(this, 0,
                    (new Intent(this, MediaPlayerService.class))
                            .setAction(Constants.ACTION.PAUSE),
                    0);
            iPlayPauseActivity.putExtra(Constants.INTENT.IS_PLAY_MEDIA_NOTIFICATION, false);

            sendBroadcast(iPlayPauseActivity);

        }
        else if (type.equals(Constants.NOTIFICATION.PAUSE)){
            icon_Action = R.drawable.ic_media_play_light;
            playPauseIntent = PendingIntent.getService(this, 0,
                    (new Intent(this, MediaPlayerService.class)).setAction(Constants.ACTION.PLAY),
                    0);
            iPlayPauseActivity.putExtra(Constants.INTENT.IS_PLAY_MEDIA_NOTIFICATION, true);
            sendBroadcast(iPlayPauseActivity);
        }

        // intent truyền xuống service
        nextIntent = PendingIntent.getService(this, 0,
                iNext, 0);

        prevIntent = PendingIntent.getService(this, 0,
                iPrev, 0);

        createChannel();


        Bitmap bitmap =
                ImageUtils.getInstance(this).getBitmapIntoPicasso(mSongs.get(position).getAlbumID());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = new androidx.core.app.NotificationCompat
                .Builder(this, getString(R.string.default_notification_channel_id))
                .setChannelId(getString(R.string.default_notification_channel_id))
                .setAutoCancel(false)
                .setShowWhen(false)

                // Set the Notification color
                .setColorized(true).setColor(getResources().getColor(R.color.white))
//                .setLargeIcon()
                .setSmallIcon(R.drawable.ic_music_note_white_24dp)
                .setLargeIcon(bitmap)
                .setSubText(mSongs.get(position).getFileName())

                .setContentTitle(mSongs.get(position).getSongName())
                .setContentText(mSongs.get(position).getArtist())
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // click notification intent to home activity
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0))
                // Add playback actions
                .addAction(position == 0 ?
                                R.drawable.ic_previous_black : R.drawable.ic_previous_white,
                        Constants.NOTIFICATION.PREVIOUS,
                        prevIntent)
                .addAction(icon_Action, Constants.NOTIFICATION.PAUSE, playPauseIntent)

                .addAction(position == mSongs.size() ?
                                R.drawable.ic_next_black : R.drawable.ic_next_white,
                        Constants.NOTIFICATION.NEXT,
                        nextIntent)
                .addAction(R.drawable.ic_close_black_24dp, "Stop", closeNotification())

                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()

                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2)
                        // Attach our MediaSession token
                        .setMediaSession(mMediaSessionCompat.getSessionToken()));

        }
        else {
            notificationBuilder = new NotificationCompat.Builder(this)
                .setChannelId(getString(R.string.default_notification_channel_id))
                .setAutoCancel(false)
                .setShowWhen(false)

                // Set the Notification color
                .setColorized(true).setColor(getResources().getColor(R.color.white))
//                .setLargeIcon()
                .setSmallIcon(R.drawable.ic_music_note_white_24dp)
                .setLargeIcon(bitmap)
                .setSubText(mSongs.get(position).getFileName())

                .setContentTitle(mSongs.get(position).getSongName())
                .setContentText(mSongs.get(position).getArtist())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                // click notification intent to home activity
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), 0))
                // Add playback actions
                .addAction(
                        R.drawable.ic_previous_white ,
                        Constants.NOTIFICATION.PREVIOUS,
                        prevIntent)
                .addAction(icon_Action, Constants.NOTIFICATION.PAUSE, playPauseIntent)

                .addAction(
                        R.drawable.ic_next_white ,
                        Constants.NOTIFICATION.NEXT,
                        nextIntent)
                .addAction(R.drawable.ic_close_black_24dp, "Stop", closeNotification())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        // Show our playback controls in the compact notification view.
                        .setShowActionsInCompactView(0, 1, 2)
                        // Attach our MediaSession token
                        .setMediaSession(mMediaSessionCompat.getSessionToken()));
        }

        notificationBuilder.setDeleteIntent(dismissNotification());

        startForeground(NOTIFICATION_ID,
                notificationBuilder.build());
    }
    /*
    Audio Manager Focus Change
    * */
    @Override
    public void onAudioFocusChange(int state) {
        switch (state) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mMediaPlayer == null) {
                    initMediaPlayer();
                }
                else if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }
    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return result == AudioManager.AUDIOFOCUS_GAIN;
    }



    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        Log.d(tag,  "MediaPlayer.OnInfoListener: " + what);
        return false;

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        /*
         * Getting to saved location of song if playback STATE is none i.e. first instance of music playback
         * , and if we are playing same track we were playing before, if track is new then we won't seek to last
         * remembered location
         * We reset this location to zero when we start playing a new song
         */
        /*
         * Setting Equalizer
         */
        try {
            setEqualizer();

            /*
             * Setting metaData
             */
            setGraphics();

            int pos = mSharedPrefsUtils.getInteger(Constants.PREFERENCES.POSITION_SONG,0);
            if (mSongs!= null) {
                Log.d(tag, "Service --- onPrepared: " + (mSongs.get(position).getSongName()));
                Log.d(tag, "Service ---  "+(mSongs.get(position).getSongName())+": "+pos);
            }

            setupHandler();
            if (pos != 0){
                mediaPlayer.seekTo(pos);
            }

            mediaPlayer.start();

        }finally {
            status = PLAYED;
            mSongManager.setPositionCurrent(position);
            initNotification(Constants.NOTIFICATION.PLAY, mSongManager.getPositionCurrent());
        }

    }
    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.d("MMM", "Service --- onSeekComplete Enter");
//        handler.removeCallbacks(sendUpdatesToUI);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            status = COMPLETE;
            handler.removeCallbacks(sendUpdatesToUI);
            Log.d(tag, "onCompletion");
            if (mSongs != null) {
                setShuffleSong(isShuffle);
                status = PAUSED;
                Log.d(tag, "isRepeat: "+isRepeat);
                if (!isRepeat) {
                    try {
                        if (status == PAUSED) {
                            status = NEXT;
                            mMediaTransportControls.skipToNext();
                        }
                    }finally {
                        status = PAUSED;
                        initNotification(Constants.NOTIFICATION.PLAY,
                                SongManager.getInstance().getPositionCurrent());
                    }

                }else if (isRepeat){
                    Log.d(tag, "mSongs: "+(mSongs == null? "null":"khac null"));
                    try {
                        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.POSITION_SONG, 0);
                        setupHandler();
                        state_play();
                    }finally {
                        status = PLAYED;
                        initNotification(Constants.NOTIFICATION.PLAY,
                                SongManager.getInstance().getPositionCurrent());
                    }
                }
            }
        } finally {
            status = PAUSED;
            if (!isRepeat) {
                iNewMusic.putExtra(Constants.INTENT.CHOOSE_POS,
                        SongManager.getInstance().getPositionCurrent());
                sendBroadcast(iNewMusic);

                initNotification(Constants.NOTIFICATION.PAUSE,
                        SongManager.getInstance().getPositionCurrent());
                // báo lên ui quyết định pause
            }else {
                initNotification(Constants.NOTIFICATION.PLAY,
                        SongManager.getInstance().getPositionCurrent());
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mSongs!= null) {
            Log.d(tag,
                    "initMediaPlayer ERROR ---- " + what + "---  Extra " + extra + " - " + mSongs.get(SongManager.getInstance().getPositionCurrent()).getSongName());
        }

        isInitAudioError = true;
        //Invoked when there has been an error during an asynchronous operation.
        switch (what) {

            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }
    /**
    * BroadCast Serivce
    */

    private PendingIntent dismissNotification() {
        Intent dismissIntent = new Intent(Constants.ACTION.BROADCAST_STOP_AUDIO);
        return PendingIntent.getBroadcast(this, 0, dismissIntent, 0);

    }

    private PendingIntent closeNotification() {
        Intent dismissIntent = new Intent(Constants.ACTION.CLOSE_NOTIFICATION);
        return PendingIntent.getBroadcast(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private BroadcastReceiver brNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( mMediaPlayer != null && mMediaPlayer.isPlaying() ) {
                mMediaPlayer.pause();
            }
        }
    };

    private BroadcastReceiver brCloseNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
            stopMedia();
            mMediaPlayer.reset();
            releaseMedia();
            stopForeground(false);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();

            System.exit(1);

        }
    };

    private BroadcastReceiver brStopMusic =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
//                SongManager.getInstance().setPositionCurrent(0);
            }

        }
    };

    private BroadcastReceiver brResetMusic = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // reset mediaPlayer to play the new Audio
            mMediaPlayer.reset();
//            initMediaPlayer();
            updateMetaData(mSongs.get(SongManager.getInstance().getPositionCurrent()));
//            buildNotification(Constants.NOTIFICATION.PLAY);
        }
    };

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    void addVoteToTrack(String path) {
      /*  path = path.trim();
        try {
            mSongManager.getCategorySongsDB()

            if (mSongManager.getCategorySongsDB().searchCategory(path)) {
                mCategorySongs.updateRow(path);
            } else {
                mCategorySongs.addCategory(1, mSongs.getData(SongManager.getInstance().getPositionCurrent()));
            }
            Log.d(tag, mCategorySongs.checkRow(path) +"" );
            mCategorySongs.closeDatabase();
        } catch (Exception e) {

            *//*Log.d(tag, "addVoteToTrack crashed.");
            Log.d(tag,"==============");*//*
        }*/
    }
    Equalizer eq;
    BassBoost bassBoost;
    Virtualizer virtualizer;
    private void setEqualizer(){
        boolean isEqInSettings =
                mSharedPrefsUtils.getBoolean(Constants.PREFERENCES.TURN_EQUALIZER, false);
        int currentEqProfile =
                mSharedPrefsUtils.getInteger(Constants.PREFERENCES.CURRENT_EQUALIZER_PROFILE, 0);
        try {
            eq = new Equalizer(0, mMediaPlayer.getAudioSessionId());
            eq.setEnabled(isEqInSettings);
            for (int i = 0; i < eq.getNumberOfBands(); i++) {
                eq.setBandLevel((short) i, (short) mSharedPrefsUtils.getInteger(
                        "profile" + currentEqProfile + "Band" + i, 0));
            }

        } catch (Exception e) {
            Log.d(tag, "Unable to run Equalizer");

        }
        try {
            bassBoost = new BassBoost(0, mMediaPlayer.getAudioSessionId());
            bassBoost.setEnabled(isEqInSettings);
            bassBoost.setStrength((short)  mSharedPrefsUtils.getInteger(Constants.PREFERENCES.BASS_LEVEL + currentEqProfile,
                    0));
        } catch (Exception ignored) {}
        try {
            virtualizer = new Virtualizer(0, mMediaPlayer.getAudioSessionId());
            virtualizer.setEnabled(isEqInSettings);
            virtualizer.setStrength((short) mSharedPrefsUtils.getInteger(Constants.PREFERENCES.VIRTUAL_LEVEL + currentEqProfile, 0));
        } catch (Exception ignored) {}
    }
    private void setGraphics(){
        mSharedPrefsUtils.setInteger(Constants.PREFERENCES.AUDIO_SESSION_ID,
                mMediaPlayer.getAudioSessionId());
    }
}
