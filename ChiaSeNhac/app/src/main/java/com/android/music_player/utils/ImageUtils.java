package com.android.music_player.utils;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.android.music_player.R;
import com.android.music_player.models.SongModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageUtils {

    private Context mContext;
    private static ImageUtils instance;
    public static ImageUtils getInstance(Context context){
        if (instance == null){
            instance = new ImageUtils(context);
        }
        return instance;
    }


    public ImageUtils(Context context) {
        this.mContext = context;
    }

        /*
         * @params: imageView is the ImageView where image should go
         * @params: arrayList is list of SongModel object with each having getAlbumID()
         * @params: albumIds is List<String> of album ids
         *
         * Three ways to grab album art -
         * 1. getImageByPicasso(String albumId, ImageView imageView)
         * 2. getImageByPicasso(ArrayList<SongModel> arrayList, ImageView imageView)
         * 3. getImageByPicasso(final List albumIds, final ImageView imageView)
         *
         * Also grab Bitmap with - getAlbumArt(Long albumId) where albumId is a long variable
         * (converted from string in this app)
     */



    public void getImageByPicasso(String albumId, ImageView image) {
        try {
            Picasso.get().load(getSongUri(Long.parseLong(albumId)))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_black_24dp)))
                    .resize(500,500)
                    .onlyScaleDown()
                    .into(image);}
        catch (Exception ignored) {}
    }

    public void getImageByPicasso(ArrayList<SongModel> songs, ImageView image) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++) {
            list.add(songs.get(i).getAlbumID());
            if (i == 20) {break; } // 20 should be enough, remove this line if you want to queryData whole list
        }
        getImageByPicasso(list, image, 0, list.size() - 1);
    }

    public void getImageByPicasso(final List albumIds, final ImageView imageView) {
        try {
            final int i = 0;
            final int max = albumIds.size()-1;
            if (i < max) {
                Picasso.get().load(getSongUri(Long.parseLong(albumIds.get(i).toString())))
                        .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_black_24dp)))
                        .resize(500,500)
                        .onlyScaleDown()
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                getImageByPicasso(albumIds, imageView, i + 1, max);
                            }
                        });
            }
            else {
                Picasso.get().load(getSongUri(Long.parseLong(albumIds.get(i).toString())))
                        .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_black_24dp))).into(imageView);
            }}
        catch (Exception ignored) {}
    }

    public void getSmallImageByPicasso(String albumID, ImageView image) {
        try {
            Picasso.get().load(getSongUri(Long.parseLong(albumID)))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_black_24dp)))
                    .resize(400,400)
                    .onlyScaleDown()
                    .into(image);}
        catch (Exception ignored) {}
    }

    public void getSmallImage(String albumID, ImageView image) {
        try {
            Picasso.get().load(getSongUri(Long.parseLong(albumID)))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_white_24dp)))
                    .resize(400,400)
                    .onlyScaleDown()
                    .into(image);}
        catch (Exception ignored) {}
    }

    public void getBitmapImageByPicasso(String albumID, final ImageView imageView){
        try {
            Picasso.get().load(getSongUri(Long.parseLong(albumID)))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_black_24dp)))
                    .resize(400,400)
                    .onlyScaleDown()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            imageView.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });}
        catch (Exception ignored) {}
    }

    public Bitmap getBitmapIntoPicasso(String albumID){
        final Bitmap[] mBitmap = new Bitmap[1];
        try {
            Picasso.get().load(getSongUri(Long.parseLong(albumID)))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_white_24dp)))
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

    public void getFullImageByPicasso(String albumID, ImageView imageView) {
        try {
            Picasso.get().load(getSongUri(Long.parseLong(albumID)))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_white_24dp)))
                    .into(imageView);}
        catch (Exception ignored) {}
    }

    public void setImageBitmap(String albumId, ImageView imageView){

        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri,  Long.valueOf(albumId));
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    mContext.getContentResolver(), albumArtUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 40, 40, true);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            bitmap = BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.ic_music_note_white_24dp);

        } catch (IOException e) {

            e.printStackTrace();
        }finally {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }else {
                imageView.setImageResource(R.drawable.ic_music_note_white_24dp);

            }
        }
    }

    public void getFullImageByPicasso(final List albumIds, final ImageView imageView) {
        try {
            final int i = 0;
            final int max = albumIds.size()-1;
            if (i < max) {
                Picasso.get().load(getSongUri(Long.parseLong(albumIds.get(i).toString())))
                        .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_white_24dp)))
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                getImageByPicasso(albumIds, imageView, i + 1, max);
                            }
                        });
            }
            else {
                Picasso.get().load(getSongUri(Long.parseLong(albumIds.get(i).toString())))
                        .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_white_24dp))).into(imageView);
            }}
        catch (Exception ignored) {}
    }

    public Bitmap getAlbumArt(Long albumId) {
        Bitmap albumArtBitMap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {

            final Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");

            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);

            ParcelFileDescriptor pfd = mContext.getContentResolver()
                    .openFileDescriptor(uri, "r");

            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                albumArtBitMap = BitmapFactory.decodeFileDescriptor(fd, null,
                        options);
            }
        } catch (Error ee) {
            ee.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != albumArtBitMap) {
            return albumArtBitMap;
        }
        return getDefaultAlbumArtEfficiently();
    }

    public  void getFullImageByPicasso(final List albumSongs, final ImageView imageView, final int i, final int max) {
        try {
            if (i < max) Picasso.get().load(getSongUri(Long.parseLong(albumSongs.get(i).toString())))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_white_24dp)))
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            getFullImageByPicasso(albumSongs, imageView, i + 1, max);
                        }
                    });
            else if (i == max) {
                Picasso.get().load(getSongUri(Long.parseLong(albumSongs.get(i).toString())))
                        .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_white_24dp))).into(imageView);
            }}
        catch (Exception ignored) {}
    }

    public  void getImageByPicasso(final List albumSongs, final ImageView imageView, final int i, final int max) {
        try {
            if (i < max) Picasso.get().load(getSongUri(Long.parseLong(albumSongs.get(i).toString())))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_white_24dp)))
                    .resize(500,500)
                    .onlyScaleDown()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            getImageByPicasso(albumSongs, imageView, i + 1, max);
                        }
                    });
            else if (i == max) {
                Picasso.get().load(getSongUri(Long.parseLong(albumSongs.get(i).toString())))
                        .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_white_24dp))).into(imageView);
            }}
        catch (Exception ignored) {}
    }

    public  Bitmap getDefaultAlbumArtEfficiently() {

        return BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_music_notes_padded);
    }

    public static Uri getSongUri(Long albumID) {
        return ContentUris.withAppendedId(Uri
                .parse("content://media/external/audio/albumart"), albumID);
    }
}
