package com.android.music_player.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.music_player.R;
import com.android.music_player.interfaces.OnConnectMediaId;
import com.android.music_player.managers.MediaManager;
import com.android.music_player.managers.MusicLibrary;
import com.android.music_player.managers.QueueManager;
import com.android.music_player.utils.Constants;
import com.android.music_player.utils.ImageHelper;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChooseMusicAdapter extends RecyclerView.Adapter<ChooseMusicAdapter.ViewHolder> {
    private Context mContext;
    private MediaSessionCompat.QueueItem item;
    private MediaManager mMediaManager;
    private List<MediaSessionCompat.QueueItem> mQueueItems;
    private ImageHelper mImageUtils;
    private QueueManager mQueueManager;
    private int mPossitionMusic;
    private SimpleDateFormat format = new SimpleDateFormat("mm:ss", Locale.getDefault());
    public ChooseMusicAdapter(Context context ) {
        mContext = context;
        mImageUtils = ImageHelper.getInstance(context);
        mMediaManager = MediaManager.getInstance();
        mMediaManager.setContext(context);
        mQueueManager = QueueManager.getInstance(context);
    }

    public void setQueueMediaID(List<MediaSessionCompat.QueueItem> queueItems){
        this.mQueueItems = queueItems;
    }

    public List<MediaSessionCompat.QueueItem> getQueueItems() {
        return mQueueItems;
    }

    private OnConnectMediaId onConnectMediaId;
    public void setOnConnectMediaIdListener(OnConnectMediaId onConnectMediaId) {
        this.onConnectMediaId = onConnectMediaId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_music, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mQueueItems.size();
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull ViewHolder holder) {
        return super.onFailedToRecycleView(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        item = mQueueItems.get(position);
        holder.setData(position);
        holder.ll_option_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConnectMediaId.onChangeMediaId(mQueueItems.get(position).getDescription().getMediaId());
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private LinearLayout ll_option_music;
        private TextView textTime, textTitle, textArtist;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_option_music = itemView.findViewById(R.id.ll_item_option_music);
            imageView = itemView.findViewById(R.id.item_music_img_music);
            textTime = itemView.findViewById(R.id.item_music_text_time);
            textTitle = itemView.findViewById(R.id.item_music_text_title);
            textArtist = itemView.findViewById(R.id.item_music_text_artists);
        }


        public void setData(int pos){
            MediaSessionCompat.QueueItem item = mQueueItems.get(pos);
            if ((mMediaManager.getCurrentMusic()).equals(item.getDescription().getMediaId())){
//                Log.d("KKK", "setData: "+getListMusic().getData(pos).getMusicId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textTitle.setTextColor(mContext.getColor(R.color.red));
                }else {
                    textTitle.setTextColor(Color.parseColor("#FFFF0000"));
                }
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textTitle.setTextColor(mContext.getColor(R.color.black));
                }else {
                    textTitle.setTextColor(Color.parseColor("#FFFFFFFF"));
                }
            }

            MediaMetadataCompat metadataCompat = mMediaManager.getMetadata(mContext,
                    item.getDescription().getMediaId());

            mImageUtils.getSmallImageByPicasso(String.valueOf(MusicLibrary.getAlbumRes((String) metadataCompat.getText(Constants.METADATA.Title))), imageView);
            textTime.setText(format.format(metadataCompat.getLong(Constants.METADATA.Duration)));
            textArtist.setText(metadataCompat.getText(Constants.METADATA.Artist));
            textTitle.setText(metadataCompat.getText(Constants.METADATA.Title));
        }
    }
}
