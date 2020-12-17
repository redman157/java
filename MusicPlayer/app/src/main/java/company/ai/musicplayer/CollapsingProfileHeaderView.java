package company.ai.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import android.content.ContentUris;

import company.ai.musicplayer.models.Music;

public class CollapsingProfileHeaderView extends LinearLayout {
    private int profileDrawable,miscIcon, profileNameTextSize, profileSubtitleTextSize, profileMiscTextSize;
    private String profileName, subtitle,misc;
    private Context context;
    private TextView mTextSongName, mTextArtist, mTextAlbums;
    private ImageView profileImage;


    public CollapsingProfileHeaderView(Context context) {
        super(context);
        this.context = context;
    }

    public CollapsingProfileHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CollapsingProfileHeaderView, 0 ,0);

        try {
            profileDrawable =
                    a.getResourceId(R.styleable.CollapsingProfileHeaderView_profileImage, 0);
            profileName = a.getString(R.styleable.CollapsingProfileHeaderView_profileName);
            subtitle = a.getString(R.styleable.CollapsingProfileHeaderView_profileSubtitle);
            misc = a.getString(R.styleable.CollapsingProfileHeaderView_profileMisc);
            miscIcon = a.getResourceId(R.styleable.CollapsingProfileHeaderView_profileMiscIcon, 0);
            profileNameTextSize =
                    a.getResourceId(R.styleable.CollapsingProfileHeaderView_profileNameTextSizeSp, 20);
            profileSubtitleTextSize =
                    a.getResourceId(R.styleable.CollapsingProfileHeaderView_profileSubtitleTextSizeSp, 12);
            profileMiscTextSize =
                    a.getResourceId(R.styleable.CollapsingProfileHeaderView_profileMiscTextSizeSp, 15);
        } finally {
            a.recycle();
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_collapsing_profile_header, this,
                true);
        loadViews();

    }


    public void applyAttributes() {
        /*ImageHelper.getInstance(context).getSmallImageByPicasso(songModel.getAlbumID(),
                mProfile);*/

        profileImage.setImageResource(profileDrawable);
        mTextSongName.setText(profileName);
        mTextSongName.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                12);
        mTextArtist.setText(subtitle);
        mTextArtist.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        mTextAlbums.setText(misc);
        mTextAlbums.setCompoundDrawablesWithIntrinsicBounds(miscIcon, 0, 0, 0);
        mTextAlbums.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
    }
    private void loadViews() {
        profileImage = this.findViewById(R.id.profileImage);
        mTextSongName = this.findViewById(R.id.profileName);
        mTextArtist = this.findViewById(R.id.profileSubtitle);
        mTextAlbums = this.findViewById(R.id.profileMisc);
    }

    private Uri getSongUri(Long albumID) {
        if (albumID == null){
            return null;
        }else{
            return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumID);
        }
    }

    public void applyAttributes(Music musicModel) {
        Picasso.get()
                .load(getSongUri(musicModel.getAlbumID()))
                .placeholder(R.drawable.app_icon_music)
                .resize(400, 400)
                .onlyScaleDown()
                .into(profileImage);
        mTextSongName.setText(musicModel.getDisplayName());
        mTextSongName.setTextSize(TypedValue.COMPLEX_UNIT_SP,
                12);
        mTextArtist.setText(musicModel.getAlbum());
        mTextArtist.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        mTextAlbums.setText(musicModel.getArtist());
        mTextAlbums.setCompoundDrawablesWithIntrinsicBounds(miscIcon, 0, 0, 0);
        mTextAlbums.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
    }

    public int getProfileDrawable() {
        return profileDrawable;
    }

    public void setProfileDrawable(int profileDrawable) {
        this.profileDrawable = profileDrawable;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getMiscIcon() {
        return miscIcon;
    }

    public void setMiscIcon(int miscIcon) {
        this.miscIcon = miscIcon;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }
}