package com.android.music_player.utils;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.android.music_player.R;
import com.android.music_player.models.MusicModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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



    public void getImageByPicasso(String albumId, ImageView image) {
        try {
            Picasso.get().load(getSongUri(Long.parseLong(albumId)))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_black_24dp)))
                    .resize(500,500)
                    .onlyScaleDown()
                    .into(image);}
        catch (Exception ignored) {}
    }

    public void getImageByPicasso(ArrayList<MusicModel> songs, ImageView image) {
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
                    .into(image);
        }
        catch (Exception ignored) {}
    }

    public void getImageByPicassoAnimation(final String albumID, final ImageView imageView) {
        try {
            imageView.setAlpha(0f);
            Picasso.get().load(getSongUri(Long.parseLong(albumID)))
                    .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.ic_music_note_black_24dp)))
                    .resize(400,400)
                    .onlyScaleDown()
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (imageView.getAlpha() == 0f) {
                                /*Picasso.get().load(getSongUri(Long.parseLong(albumID)))
                                        .into(imageView);*/
                                imageView.animate().setDuration(700).alpha(0.7f).start();
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("EEE", e.getMessage());
                            imageView.setAlpha(0f);
//                            imageView.setImageResource(R.drawable.ic_music_note_black_24dp);
                            imageView.animate().setDuration(500).alpha(0.7f).start();
                        }
                    });
        }
        catch (Exception ignored) {

        }
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
}
