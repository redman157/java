package com.jvit.companycoin.fragment.sliderItemFragment;

import android.app.Activity;
import android.os.Handler;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.jvit.companycoin.api.IdeaAll;
import com.jvit.companycoin.object.PostSlider;
import com.jvit.companycoin.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SliderFeedBackAdapter extends FragmentPagerAdapter
        implements Callback<IdeaAll>, ViewPager.OnPageChangeListener {
    private ArrayList<PostSlider> listPost;
    private ArrayList<TextView> listDots;
    private Activity activity;
    private boolean flag;
    private Timer timer;
    private RendererPost rendererPost;

    public interface RendererPost {
        void Refresh();

        void Render();

        void Switch();
    }

    public SliderFeedBackAdapter(FragmentManager fm, final Activity activity) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        final Handler handler = new Handler();
        flag = true;
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (flag && rendererPost != null) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        rendererPost.Switch();
                                    }
                                });
                            }
                        }
                    };
                    handler.post(runnable);
                }
            }, 1000,3000);
        }
        this.activity = activity;
        listPost = new ArrayList<>();
        listDots = new ArrayList<>();


    }


    public void onRendering(RendererPost rendererPost){
        this.rendererPost = rendererPost;
    }

    public void enable(){
        if (!flag){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rendererPost.Refresh();
                }
            });
        }
        flag = true;
    }

    public void disable(){
        flag = false;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ItemFeedBackFragment.newInstance(listPost.get(position));
    }

    @Override
    public int getCount() {
        return listPost.size();
    }

    public int size() {
        return listPost.size();
    }
    public ArrayList<TextView> getDots() {
        return listDots;
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "page " + position;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        for (int i = 0; i < listDots.size(); ++i) {
            int color;

            if (i == position) {
                color = activity.getResources()
                        .getIntArray(R.array.array_dot_active)[0];
            } else {
                color = activity.getResources()
                        .getIntArray(R.array.array_dot_inactive)[0];
            }

            listDots.get(i).setTextColor(color);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onResponse(Call<IdeaAll> call,
                           Response<IdeaAll> response){


        IdeaAll ideaHome = response.body();
        if (ideaHome != null) {
            for (IdeaAll.InfoIdea idea : ideaHome.getIdeaList()) {
                TextView dot = new TextView(activity);

                dot.setText(Html.fromHtml("&#8226;"));
                dot.setTextSize(35);
                dot.setTextColor(activity.getResources().getIntArray(R.array.array_dot_inactive)[0]);
                if (listDots.size()<3) {
                    listDots.add(dot);
                    listPost.add(new PostSlider(idea.getId(), idea.getReactions_count(),
                            idea.getToken_amount(), idea.getReacted(), idea.getUser().getAvatar_path(),
                            idea.getUser().getName(), idea.getSent_at(),
                            idea.getContent()));
                }else {
                    break;
                }
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                    rendererPost.Render();
                }
            });
        }
    }

    @Override
    public void onFailure(Call<IdeaAll> call, Throwable t) {

    }

}
