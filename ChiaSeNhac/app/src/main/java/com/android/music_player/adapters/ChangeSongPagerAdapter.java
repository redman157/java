package com.android.music_player.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.music_player.interfaces.OnClickItemListener;
import com.android.music_player.activities.PlayActivity;
import com.android.music_player.fragments.ChangeSongFragment;
import com.android.music_player.managers.SongManager;
import com.android.music_player.models.SongModel;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.Utils;

import java.util.ArrayList;

public class ChangeSongPagerAdapter extends FragmentStatePagerAdapter implements OnClickItemListener {
    private Context context;
    private ArrayList<Fragment> fragments=  new ArrayList<>();
    private ArrayList<SongModel> mSongModels;
    private SongManager mSongManager;

    public ChangeSongPagerAdapter(Context context, @NonNull FragmentManager fm) {
        super(fm);
        this.context = context;
        mSongManager = SongManager.getInstance();
        mSongManager.setContext(context);
    }

    public void addData(ArrayList<SongModel> songModels) {
        mSongModels = songModels;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }



    @Override
    public void onClick(int pos) {
        SongManager.getInstance().setPositionCurrent(pos - 1);
        if (((PlayActivity)context).isShuffle){
            Utils.NextMediaService(context, Constants.VALUE.SHUFFLE, mSongManager.getPositionCurrent());
        }else {
            Utils.NextMediaService(context, mSongManager.getTypeCurrent(), mSongManager.getPositionCurrent());
        }

    }

    @Override
    public void onClick(String type, int index) {

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        ChangeSongFragment fChangeSongFragment = new ChangeSongFragment(this);
        fChangeSongFragment.setMusicMain(mSongModels);
        fChangeSongFragment.setSongModel(mSongModels.get(position));
        return fChangeSongFragment;
    }

    @Override
    public int getCount() {
        return mSongModels.size();
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
