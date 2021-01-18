package com.android.music_player.view;

import android.app.Activity;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.models.MusicModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MediaItemViewHolder extends RecyclerView.ViewHolder  {
    public ImageView mImageView;
    public TextView mTextNameMusic, mTextArtistMusic;
    public TextView mTextTimeMusic;
    public ImageButton mBtnMenu;
    private Activity mActivity;
    public LinearLayout mLinearMusic;
    private SimpleDateFormat mFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());
    public MediaItemViewHolder(View itemView, Activity activity) {
        super(itemView);
        this.mActivity = activity;
        mLinearMusic = itemView.findViewById(R.id.item_ll_music);
        mTextArtistMusic = itemView.findViewById(R.id.item_text_artist_music);
        mImageView = itemView.findViewById(R.id.item_img_music);
        mBtnMenu = itemView.findViewById(R.id.item_btn_music);
        mTextNameMusic = itemView.findViewById(R.id.item_text_title_music);
        mTextTimeMusic = itemView.findViewById(R.id.item_text_time_music);
    }

    public void assignData(final MediaMetadataCompat song) {
        //UI setting code
        mTextNameMusic.setText(song.getString(Constants.METADATA.Title));
        mTextArtistMusic.setText(song.getString(Constants.METADATA.Artist));
        mTextTimeMusic.setText(mFormat.format(song.getLong(Constants.METADATA.Duration)));
        mImageView.setClipToOutline(true);

        ImageHelper.getInstance(mActivity).getSmallImageByPicasso(String.valueOf(MusicLibrary.getAlbumRes(song.getString(Constants.METADATA.Title))), mImageView);
    }

    public void assignData(final MusicModel song) {
        //UI setting code
        mTextNameMusic.setText(song.getSongName());
        mTextArtistMusic.setText(song.getArtist());
        mTextTimeMusic.setText(mFormat.format(song.getTime()));
        mImageView.setClipToOutline(true);

        ImageHelper.getInstance(mActivity).getSmallImageByPicasso(song.getAlbumID(), mImageView);
    }
}
