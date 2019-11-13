package com.jvit.companycoin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jvit.companycoin.object.Notification;
import com.jvit.companycoin.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NotificationUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Notification> listNotification;
    private final int VIEW_TYPE_ITEM=0,VIEW_TYPE_LOADING=1;
    private int beg0, beg1, beg2, end0, end1, end2;
    public NotificationUserAdapter(Context context, ArrayList<Notification> listNotification) {
        this.context = context;
        this.listNotification = listNotification;
    }
    class LoadingNotification extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingNotification(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBarLoadedPostAll);
        }
    }
    class ItemNotification extends RecyclerView.ViewHolder{
        TextView textTime, textInfoNoti, textMessage;
        ImageView icon;
        ItemNotification(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.iconItemNotification);
            textMessage = itemView.findViewById(R.id.textItemMessageNotification);
            textTime = itemView.findViewById(R.id.textItemTittleNotification);
            textInfoNoti = itemView.findViewById(R.id.textItemNameSendNotification);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
            return new ItemNotification(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading_postall, parent,false);
            return new LoadingNotification(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemNotification) {
            final Notification notification = listNotification.get(position);
            final ItemNotification itemNotification = (ItemNotification) holder;
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

                Date past = format.parse(notification.getTime());
                Date now = new Date();

                itemNotification.textTime.setText(checkTime(now, past));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            itemNotification.icon.setImageResource(notification.getIcon());
            itemNotification.textMessage.setText(notification.getMess().replaceAll("\n", " "));
            if (!itemNotification.textMessage.getText().toString().equals("")) {
                itemNotification.textMessage.setVisibility(View.VISIBLE);
            }
            itemNotification.textInfoNoti.setText(notification.getMessageContent());
        }else if (holder instanceof LoadingNotification){
            LoadingNotification loadingNotification = (LoadingNotification) holder;
            loadingNotification.progressBar.setIndeterminate(true);
        }
    }


    @Override
    public int getItemCount() {
        return listNotification  == null ? 0: listNotification.size();
    }
    @Override
    public int getItemViewType(int position) {
        return listNotification.get(position) == null ? VIEW_TYPE_LOADING:VIEW_TYPE_ITEM;
    }



    private String checkTime(Date now, Date past){
        long sec = TimeUnit.MILLISECONDS.toSeconds(now.getTime()) - TimeUnit.MILLISECONDS.toSeconds(past.getTime());
        if (sec < 0){
            return "vài giây trước";
        }
        if (sec < 60) {
            return sec + " "+context.getString(R.string.sec_ago);
        } else if (sec < 60*60) {
            return (sec/60) + " "+context.getString(R.string.minute_ago);
        } else if (sec < 24*60*60) {
            return (sec/(60*60)) + " "+context.getString(R.string.hour_ago);
        } else if (sec < 7*24*60*60) {
            return (sec/(24*60*60)) + " "+context.getString(R.string.day_ago);
        } else {
            return (sec / (7 * 24 * 60 * 60)) + " "+context.getString(R.string.week_ago);
        }
    }
}
