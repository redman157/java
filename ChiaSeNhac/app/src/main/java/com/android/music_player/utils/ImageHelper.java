package com.android.music_player.utils;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.android.music_player.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class ImageHelper {

    private Context mContext;
    private static ImageHelper instance;
    public static ImageHelper getInstance(Context context){
        if (instance == null){
            instance = new ImageHelper(context);
        }
        return instance;
    }


    public ImageHelper(Context context) {
        this.mContext = context;
    }

        /*
         * @params: imageView is the ImageView where image should go
         * @params: arrayList is list of MusicModel object with each having getAlbumID()
         * @params: albumIds is List<String> of album ids
         *
         * Three ways to grab album art -
         * 1. getImageByPicasso(String albumId, ImageView imageView)
         * 2. getImageByPicasso(ArrayList<MusicModel> arrayList, ImageView imageView)
         * 3. getImageByPicasso(final List albumIds, final ImageView imageView)
         *
         * Also grab Bitmap with - getAlbumArt(Long albumId) where albumId is a long variable
         * (converted from string in this app)
     */

    public void getSmallImageByPicasso(String albumID, ImageView image) {
        try {
            Picasso.get().load(getSongUri(Long.parseLong(albumID)))
                    .placeholder(R.drawable.app_icon_music)
                    .resize(400,400)
                    .onlyScaleDown()
                    .into(image);
        }
        catch (Exception ignored) {}
    }

    public void getImagePanel(String albumID, ImageView image) {
        try {
            Picasso.get().load(getSongUri(Long.parseLong(albumID)))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext,
                            R.drawable.ic_music_note_black_24dp)))
                    .resize(400,400)
                    .onlyScaleDown()
                    .into(image);
        }
        catch (Exception ignored) {}
    }

    public Bitmap getBitmapIntoPicasso(String albumID){
        final Bitmap[] mBitmap = new Bitmap[1];
        try {
            Picasso.get().load(getSongUri(Long.parseLong(albumID)))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_notes_padded)))
                    .resize(400,400)
                    .onlyScaleDown()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mBitmap[0] = bitmap;
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });}
        catch (Exception ignored) {}
        return mBitmap[0];
    }

    public static Uri getSongUri(Long albumID) {
        Uri uri = ContentUris.withAppendedId(Uri
                .parse("content://media/external/audio/albumart"), albumID);

        return uri;
    }

    public static Bitmap getAlbumArt(Context context, Long albumId) {
        Bitmap bitmap = null;
        try {
            Uri uri = ContentUris.withAppendedId(Uri
                    .parse("content://media/external/audio/albumart"), albumId);
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver() , uri);
        }
        catch (IOException e) {
            //handle exception
            if (e instanceof FileNotFoundException){
                Log.d("GGG","FileNotFoundException Enter: "+albumId);
                bitmap = getBitmapFromVectorDrawable(context,
                        R.drawable.ic_music_notes_padded);
            }
        } catch (NullPointerException e){
            Log.d("GGG","NullPointerException Enter: "+albumId);
        }
        return bitmap;
    }
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap getAlbumArtNotification(Context context, Long albumId) {
        Bitmap bitmap = null;
        try {
            Uri uri = ContentUris.withAppendedId(Uri
                    .parse("content://media/external/audio/albumart"), albumId);
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver() , uri);
        }
        catch (IOException e) {
            //handle exception
            if (e instanceof FileNotFoundException){
                int accent = ChangeTheme.getColorFromResource(context,
                        ChangeTheme.getAccent(context),
                        R.color.white);
                Log.d("GGG","FileNotFoundException Enter: "+albumId);
                bitmap = getLargeIcon(context, accent);
            }
        }
        return bitmap;
    }

    //https://gist.github.com/Gnzlt/6ddc846ef68c587d559f1e1fcd0900d3
    private static Bitmap getLargeIcon(Context context, int accent) {

        final VectorDrawable vectorDrawable =
                (VectorDrawable) context.getDrawable(R.drawable.app_icon_music);

        final int largeIconSize =
                context.getResources().getDimensionPixelSize(R.dimen._256sdp);
        final Bitmap bitmap = Bitmap.createBitmap(largeIconSize, largeIconSize, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.setTint(accent);
            vectorDrawable.setAlpha(100);
            vectorDrawable.draw(canvas);
        }

        return bitmap;
    }
}
