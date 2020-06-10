package com.android.music_player.adapters;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.fragments.ChangeSongFragment;
import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.models.SongModel;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ChangeSongPagerAdapter extends FragmentStatePagerAdapter implements OnClickItemListener {
    private Context context;
    private ArrayList<Fragment> fragments=  new ArrayList<>();
    private ArrayList<SongModel> mSongModels;
    private List<MediaBrowserCompat.MediaItem> items;
    private MediaManager mMediaManager;
    private String songName;
    private TreeMap<String, MediaMetadataCompat> music;

    public ChangeSongPagerAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm);
        this.context = context;
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(context);
    }

    public void addData(ArrayList<SongModel> songModels) {
        mSongModels = songModels;
    }
    public void addData(List<MediaBrowserCompat.MediaItem> items) {
        this.items = items;
    }


    public void addData(TreeMap<String, MediaMetadataCompat> music) {
        this.music = music;
        for (int i = 0; i < music.size();i++){
            ChangeSongFragment fChangeSongFragment = new ChangeSongFragment(this);
            fChangeSongFragment.setMedia(music.get(music.keySet().toArray()[i]));
            fragments.add(fChangeSongFragment);
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

//        fChangeSongFragment.setSongModel(mSongModels.get(position));
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);

    }

    @Override
    public void onClickPosition(int pos) {

    }

    @Override
    public void onClickMusic(String nameChoose) {

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
