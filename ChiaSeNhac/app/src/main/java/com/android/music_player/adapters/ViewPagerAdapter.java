package com.android.music_player.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.android.music_player.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private Context context;
    public TextView title;
    public ImageView icon;
    public ViewPagerAdapter(Context context,@NonNull FragmentManager manager) {
        super(manager);
        this.context = context;
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tablayout_home, null);
        title =  view.findViewById(R.id.item_tl_text_home);
//        icon = view.findViewById(R.id.item_tl_img_home);

//        icon.setImageDrawable(iconList().get(position));
        if (position == 0){
            title.setTextColor(context.getColor(R.color.orange));
        }
        title.setText(titleList().get(position));

        return view;
    }

    private ArrayList<Drawable> iconList(){
        ArrayList<Drawable> icons = new ArrayList<>();
        icons.add(context.getDrawable(R.drawable.ic_home_black_24dp));
        icons.add(context.getDrawable(R.drawable.ic_library_black_24dp));

        return icons;
    }

    private ArrayList<String> titleList(){
        ArrayList<String> titles = new ArrayList<>();
        titles.add("Home");
        titles.add("Library");
        return titles;
    }
}
