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

import com.jvit.companycoin.object.GiftSlider;
import com.jvit.companycoin.R;
import com.jvit.companycoin.api.NewGiftExchange;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SliderGiftAdapter extends FragmentPagerAdapter
        implements Callback<NewGiftExchange>,
        ViewPager.OnPageChangeListener {
    private ArrayList<GiftSlider> listGift;
    private ArrayList<TextView> listDots;
    private Activity activity;
    private RendererGift rendererGift;
    private boolean flag;
    private Timer timer;

    public interface RendererGift {
        void Refresh();

        void Render();

        void Switch();
    }

    public SliderGiftAdapter(
            FragmentManager fm, Activity context) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        final Handler handler = new Handler();

        flag = true;
        activity = context;
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (flag && rendererGift != null) {
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        rendererGift.Switch();
                                    }
                                });
                            }
                        }
                    };

                    handler.post(runnable);
                }
            }, 1000, 3000);
        }
        listGift = new ArrayList<>();
        listDots = new ArrayList<>();


    }

    public void onRendering(RendererGift rendererGift) {
        this.rendererGift = rendererGift;
    }

    public void enable() {
        if (!flag) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    rendererGift.Refresh();
                }
            });
        }

        flag = true;
    }

    public void disable() {
        flag = false;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ItemGiftFragment.newInstance(listGift.get(position));
    }

    @Override
    public int getCount() {
        return listGift.size();
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
    public void onResponse(Call<NewGiftExchange> call,
                           Response<NewGiftExchange> response) {
        NewGiftExchange exchange = response.body();
        if (exchange != null) {
            for (NewGiftExchange.Gift gift : exchange.ListGift()) {
                TextView dot = new TextView(activity);

                dot.setText(Html.fromHtml("&#8226;"));
                dot.setTextSize(35);
                dot.setTextColor(activity.getResources().getIntArray(R.array.array_dot_inactive)[0]);

                listDots.add(dot);
                listGift.add(new GiftSlider(gift.getId(), gift.getImage_path(),
                        gift.getName(),
                        gift.getQuantity(),
                        gift.getPrice(),
                        "Description"));
            }

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    notifyDataSetChanged();
                    rendererGift.Render();
                }
            });
        }
    }

    @Override
    public void onFailure(Call<NewGiftExchange> call, Throwable t) {
    }

    @Override
    public void onPageScrolled(int position, float offset, int pixels) {
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
}
