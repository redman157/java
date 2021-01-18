package com.android.music_player.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.android.music_player.R;
import com.android.music_player.fragments.HomeFragment;

import java.util.ArrayList;
import java.util.Map;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    private Context context;
    public TextView title;
    public ImageView icon;
    private FragmentManager fragmentManager;
    private HomeFragment homeFragment;
    private Map<Integer, String> mFragmentTags;
    public ViewPagerAdapter(Context context,@NonNull FragmentManager manager) {
        super(manager);
        this.context = context;
        this.fragmentManager = manager;

    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {

        return super.getItemPosition(object);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }



    public View getTabSong(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tablayout_home, null);
        title =  view.findViewById(R.id.item_tl_text_home);
        if (position == 0){
            title.setTextColor(context.getColor(R.color.red));
        }
        title.setText(titleSongList().get(position));

        return view;
    }

    private ArrayList<String> titleSongList(){
        ArrayList<String> titles = new ArrayList<>();
        titles.add("AllSong");
        titles.add("Artist");
        titles.add("Album");
        titles.add("Folder");
        return titles;
    }

    private ArrayList<Drawable> iconList(){
        ArrayList<Drawable> icons = new ArrayList<>();
        icons.add(context.getDrawable(R.drawable.ic_home_accent_24dp));
        icons.add(context.getDrawable(R.drawable.ic_library_accent_24dp));

        return icons;
    }

    private ArrayList<String> titleHomeList(){
        ArrayList<String> titles = new ArrayList<>();
        titles.add("Home");
        titles.add("Library");
        return titles;
    }
}
