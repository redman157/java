package company.ai.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import android.graphics.Canvas;

import company.ai.musicplayer.models.Music;

public class CollapsingProfileHeaderView extends CoordinatorLayout {
    private int profileDrawable,miscIcon, profileNameTextSize, profileSubtitleTextSize, profileMiscTextSize;
    private String profileName, subtitle,misc;
    private Context context;
    private TextView mTextTitle, mTextArtist, mTextAlbums;
    private ImageView mImageProfile;
    private CardView mCardImage;
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
            profileDrawable = a.getResourceId(R.styleable.CollapsingProfileHeaderView_profileImage, 0);
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
        inflater.inflate(R.layout.view_collapsing_profile_header, this, true);
        loadViews();
    }

    public void applyAttributes() {
        mImageProfile.setImageResource(profileDrawable);
        mTextTitle.setText(profileName);
        mTextTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mTextArtist.setText(subtitle);
        mTextArtist.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        mTextAlbums.setText(misc);
        mTextAlbums.setCompoundDrawablesWithIntrinsicBounds(miscIcon, 0, 0, 0);
        mTextAlbums.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
    }

    private void loadViews() {
        mImageProfile = this.findViewById(R.id.image_player);
        mTextTitle = this.findViewById(R.id.text_title);
        mTextArtist = this.findViewById(R.id.text_artist);
        mTextAlbums = this.findViewById(R.id.text_album);
    }

    private Uri getSongUri(Long albumID) {
        if (albumID == null){
            return null;
        }else{
            return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumID);
        }
    }

    public void applyAttributes(Music musicModel) {
/*        Picasso.get()
                .load(getSongUri(musicModel.getAlbumID()))
                .placeholder(R.drawable.ic_music_note)
                .resize(400, 400)
                .onlyScaleDown()
                .into(mImageProfile);*/
        mImageProfile.setImageBitmap(getAlbumArt(musicModel, context));
        mTextTitle.setText(musicModel.getDisplayName().substring(0, musicModel.getDisplayName().length() - 4).split("-")[0]);
        mTextArtist.setText(musicModel.getAlbum());
        mTextAlbums.setText(musicModel.getArtist());
    }

    public Bitmap getAlbumArt(Music music, Context context){
        Bitmap bitmap = null;
        Uri uri = null;
        try {
            if (music.getAlbumID() != null){
                uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), music.getAlbumID());
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                return bitmap;
            }else {
                bitmap = getLargeIcon(context);
            }
        }catch (IOException e ){
            if (e instanceof FileNotFoundException){
                bitmap = getLargeIcon(context);
            }
        }
        return bitmap;
    }

    //https://gist.github.com/Gnzlt/6ddc846ef68c587d559f1e1fcd0900d3
    private Bitmap getLargeIcon(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_music_note);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
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