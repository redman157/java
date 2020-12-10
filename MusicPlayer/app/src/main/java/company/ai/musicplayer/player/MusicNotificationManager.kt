package company.ai.musicplayer.player

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.app.NotificationCompat.MediaStyle
import company.ai.musicplayer.R
import company.ai.musicplayer.activiy.HomeActivity
import company.ai.musicplayer.extensions.getAlbumArt
import company.ai.musicplayer.mPreferences
import company.ai.musicplayer.service.PlayerService
import company.ai.musicplayer.utils.Constants
import company.ai.musicplayer.utils.ThemeHelper
import company.ai.musicplayer.utils.VersioningHelper


class MusicNotificationManager(private val playerService: PlayerService){
    //notification manager/builder
    private val mNotificationManager = NotificationManagerCompat.from(playerService)
    private lateinit var mNotificationBuilder: NotificationCompat.Builder

    private val mNotificationActions
        @SuppressLint("RestrictedApi")
        get() = mNotificationBuilder.mActions

    private val sFastSeekingActions get() = mPreferences.isFastSeekingActions

    private var mAlbumArt = BitmapFactory.decodeResource(
        playerService.resources,
        R.drawable.app_icon_music
    )
    private fun playerAction(action: String): PendingIntent {

        val pauseIntent = Intent()
        pauseIntent.action = action

        return PendingIntent.getBroadcast(
            playerService,
            Constants.NOTIFICATION_INTENT_REQUEST_CODE,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getFirstAdditionalAction() = if (sFastSeekingActions) {
        Constants.REWIND_ACTION
    } else {
        Constants.REPEAT_ACTION
    }

    private fun getSecondAdditionalAction() = if (sFastSeekingActions) {
        Constants.FAST_FORWARD_ACTION
    } else {
        Constants.CLOSE_ACTION
    }

    fun createNotification(): Notification{
        mNotificationBuilder = NotificationCompat.Builder(
            playerService,
            Constants.NOTIFICATION_CHANNEL_ID
        )

        if (VersioningHelper.isOreo()){
            createNotificationChannel()
        }
        val openPlayerIntent = Intent(playerService, HomeActivity::class.java)
        openPlayerIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val contentIntent = PendingIntent.getActivity(
            playerService,
            Constants.NOTIFICATION_INTENT_REQUEST_CODE,
            openPlayerIntent,
            0
        )

        mNotificationBuilder
            .setShowWhen(false)
            .setStyle(
                MediaStyle()
                    .setShowActionsInCompactView(1, 2, 3)
                    .setMediaSession(playerService.getMediaSession().sessionToken)
            )
            .setContentIntent(contentIntent)
            .addAction(notificationAction(getFirstAdditionalAction()))
            .addAction(notificationAction(Constants.PREV_ACTION))
            .addAction(notificationAction(Constants.PLAY_PAUSE_ACTION))
            .addAction(notificationAction(Constants.NEXT_ACTION))
            .addAction(notificationAction(getSecondAdditionalAction()))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        updateNotificationContent()
        return mNotificationBuilder.build()
    }

    fun cancelNotification() {
        mNotificationManager.cancel(Constants.NOTIFICATION_ID)
    }

    fun updateNotification() {
        mNotificationManager
            .notify(
                Constants.NOTIFICATION_ID,
                mNotificationBuilder.build()
            )
    }

    fun onUpdateDefaultAlbumArt(bitmapRes: Bitmap, updateNotification: Boolean) {
        mAlbumArt = bitmapRes
        if (updateNotification) {
            onHandleNotificationUpdate(false)
        }
    }

    fun onHandleNotificationUpdate(isAdditionalActionsChanged: Boolean){
        if (::mNotificationBuilder.isInitialized){
            updateNotificationContent()
            updateNotification()
        }else{
            mNotificationActions[0] =
                notificationAction(getFirstAdditionalAction())
            mNotificationActions[4] =
                notificationAction(getSecondAdditionalAction())
            updateNotification()
        }
    }

    fun updateNotificationContent(){
        val mediaPlayerHolder = playerService.mediaPlayerHolder
        mediaPlayerHolder.currentSong.first?.let{song ->
            val cover = song.getAlbumArt(playerService)
            mNotificationBuilder
                .setContentText(playerService.getString(
                    R.string.artist_and_album,
                    song.artist,
                    song.album
                ))
                .setContentTitle(song.title)
                .setLargeIcon(cover)
                .setColorized(true)
                .setSmallIcon(getNotificationSmallIcon(mediaPlayerHolder))
        }
    }

    private fun getNotificationSmallIcon(mediaPlayerHolder: MediaPlayerHolder) =
        when (mediaPlayerHolder.launchedBy) {
            Constants.FOLDER_VIEW -> R.drawable.ic_folder
            Constants.ALBUM_VIEW -> R.drawable.ic_library_music
            else -> R.drawable.ic_music_note
        }

    fun updateRepeatIcon() {
        if (::mNotificationBuilder.isInitialized && !sFastSeekingActions) {
            mNotificationActions[0] =
                notificationAction(Constants.REPEAT_ACTION)
            updateNotification()
        }
    }

    fun updatePlayPauseAction() {
        if (::mNotificationBuilder.isInitialized) {
            mNotificationActions[2] =
                notificationAction(Constants.PLAY_PAUSE_ACTION)
        }
    }

    private fun notificationAction(action: String): NotificationCompat.Action {
        var icon =
            if (playerService.mediaPlayerHolder.state != Constants.PAUSED) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }
        when (action) {
            Constants.REPEAT_ACTION -> icon =
                ThemeHelper.getRepeatIcon(playerService.mediaPlayerHolder)
            Constants.PREV_ACTION -> icon = R.drawable.ic_skip_previous
            Constants.NEXT_ACTION -> icon = R.drawable.ic_skip_next
            Constants.CLOSE_ACTION -> icon = R.drawable.ic_close
            Constants.FAST_FORWARD_ACTION -> icon = R.drawable.ic_fast_forward
            Constants.REWIND_ACTION -> icon = R.drawable.ic_fast_rewind
        }
        return NotificationCompat.Action.Builder(icon, action, playerAction(action)).build()
    }


    @RequiresApi(26)
    private fun createNotificationChannel() {
        if (mNotificationManager.getNotificationChannel(Constants.NOTIFICATION_CHANNEL_ID) == null) {
            NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                playerService.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = playerService.getString(R.string.app_name)
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
                mNotificationManager.createNotificationChannel(this)
            }
        }
    }
}