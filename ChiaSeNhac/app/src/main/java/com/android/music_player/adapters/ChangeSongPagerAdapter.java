package com.android.music_player.adapters;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.fragments.ChangeSongFragment;
import com.android.music_player.interfaces.OnClickItem;
import com.android.music_player.managers.MusicManager;
import com.android.music_player.models.SongModel;

import java.util.ArrayList;
import java.util.TreeMap;

public class ChangeSongPagerAdapter extends FragmentStatePagerAdapter implements OnClickItem {
    private Context context;
    private ArrayList<Fragment> fragments=  new ArrayList<>();
    private ArrayList<SongModel> mSongModels;
    private MusicManager mMusicManager;
    private String songName;
    private TreeMap<String, MediaMetadataCompat> music;
    public ChangeSongPagerAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm);
        this.context = context;
        mMusicManager = MusicManager.getInstance();
        mMusicManager.setContext(context);
    }

    public void addData(ArrayList<SongModel> songModels) {
        mSongModels = songModels;
    }
    public void addData(String music) {
        this.songName = music;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void onClick(int pos) {
       /* MusicManager.getInstance().setPosition(pos - 1);

        if (((PlayActivity)context).isShuffle){
            Utils.NextMediaService(context, Constants.VALUE.SHUFFLE, mMusicManager.getPosition());
        }else {
            Utils.NextMediaService(context, mMusicManager.getType(), mMusicManager.getPosition());
        }*/

    }

    @Override
    public void onClick(String type, int index) {

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        ChangeSongFragment fChangeSongFragment = new ChangeSongFragment(this);
        fChangeSongFragment.setMusicMain(MusicLibrary.getCurrentMusic(songName));
//        fChangeSongFragment.setSongModel(mSongModels.get(position));
        return fChangeSongFragment;
    }

    @Override
    public int getCount() {
        return MusicLibrary.getSize();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);

    }

    public static class ZoomOutPageTransformer  implements ViewPager.PageTransformer{
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }


    }
    public static class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }
}
